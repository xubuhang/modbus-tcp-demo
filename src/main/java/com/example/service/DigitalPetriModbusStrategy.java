package com.example.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service("digitalpetri")
public class DigitalPetriModbusStrategy implements ModbusStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DigitalPetriModbusStrategy.class);
    
    @Value("${modbus.timeout:3}")
    private int timeout;
    
    @Autowired
    private ModbusConnectionPool connectionPool;
    
    private static final String STRATEGY_TYPE = "digitalpetri";
    
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        ModbusTcpMaster master = getConnection(ip, port);
        
        try {
            CompletableFuture<ReadHoldingRegistersResponse> future =
                    master.sendRequest(new ReadHoldingRegistersRequest(address, 1), slaveId)
                            .thenApply(response -> (ReadHoldingRegistersResponse) response);

            ReadHoldingRegistersResponse response = future.get();
            ByteBuf buf = response.getRegisters();
            int value = buf.getUnsignedShort(0);
            buf.release();
            
            // 释放连接回池中
            connectionPool.releaseConnection(ip, port, STRATEGY_TYPE);
            return value;
            
        } catch (Exception e) {
            logger.error("读取寄存器失败 {}:{}, slaveId={}, address={}", ip, port, slaveId, address, e);
            // 发生异常时移除连接，强制重新创建
            connectionPool.removeConnection(ip, port, STRATEGY_TYPE);
            throw e;
        }
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        ModbusTcpMaster master = getConnection(ip, port);
        
        try {
            CompletableFuture<Void> future =
                    master.sendRequest(new WriteSingleRegisterRequest(address, value), slaveId)
                            .thenAccept(response -> {});
            future.get();
            
            // 释放连接回池中
            connectionPool.releaseConnection(ip, port, STRATEGY_TYPE);
            
        } catch (Exception e) {
            logger.error("写入寄存器失败 {}:{}, slaveId={}, address={}, value={}", ip, port, slaveId, address, value, e);
            // 发生异常时移除连接，强制重新创建
            connectionPool.removeConnection(ip, port, STRATEGY_TYPE);
            throw e;
        }
    }
    
    /**
     * 获取连接（从连接池或创建新连接）
     */
    private ModbusTcpMaster getConnection(String ip, int port) {
        return connectionPool.getConnection(ip, port, STRATEGY_TYPE, new DigitalPetriConnectionFactory());
    }
    
    /**
     * DigitalPetri连接工厂
     */
    private class DigitalPetriConnectionFactory implements ModbusConnectionPool.ConnectionFactory<ModbusTcpMaster> {
        
        @Override
        public ModbusTcpMaster create(String ip, int port) throws Exception {
            ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(ip)
                    .setPort(port)
                    .setTimeout(Duration.ofSeconds(timeout))
                    .build();
            
            ModbusTcpMaster master = new ModbusTcpMaster(config);
            
            // 连接到服务器
            CompletableFuture<ModbusTcpMaster> connectFuture = master.connect();
            connectFuture.get(); // 等待连接完成
            
            logger.info("成功创建并连接到Modbus服务器: {}:{}", ip, port);
            return master;
        }
        
        @Override
        public boolean isValid(ModbusTcpMaster connection) {
            return connection != null && connection.isConnected();
        }
        
        @Override
        public void close(ModbusTcpMaster connection) {
            if (connection != null) {
                try {
                    connection.disconnect();
                    logger.info("关闭Modbus连接");
                } catch (Exception e) {
                    logger.warn("关闭Modbus连接时发生异常", e);
                }
            }
        }
    }
}
