package com.example.service;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service("jlibmodbus")
public class JlibModbusStrategy implements ModbusStrategy {
    private static final Logger logger = LoggerFactory.getLogger(JlibModbusStrategy.class);
    
    @Value("${modbus.timeout:3}")
    private int timeout;
    
    @Autowired
    private ModbusConnectionPool connectionPool;
    
    private static final String STRATEGY_TYPE = "jlibmodbus";
    
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        ModbusMaster master = getConnection(ip, port);
        
        try {
            int[] values = master.readHoldingRegisters(slaveId, address, 1);
            
            // 释放连接回池中
            connectionPool.releaseConnection(ip, port, STRATEGY_TYPE);
            return values[0];
            
        } catch (Exception e) {
            logger.error("读取寄存器失败 {}:{}, slaveId={}, address={}", ip, port, slaveId, address, e);
            // 发生异常时移除连接，强制重新创建
            connectionPool.removeConnection(ip, port, STRATEGY_TYPE);
            throw e;
        }
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        ModbusMaster master = getConnection(ip, port);
        
        try {
            master.writeSingleRegister(slaveId, address, value);
            
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
    private ModbusMaster getConnection(String ip, int port) {
        return connectionPool.getConnection(ip, port, STRATEGY_TYPE, new JlibModbusConnectionFactory());
    }
    
    /**
     * JlibModbus连接工厂
     */
    private class JlibModbusConnectionFactory implements ModbusConnectionPool.ConnectionFactory<ModbusMaster> {
        
        @Override
        public ModbusMaster create(String ip, int port) throws Exception {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(ip));
            tcpParameters.setPort(port);
            tcpParameters.setKeepAlive(true);
            tcpParameters.setConnectionTimeout(timeout * 1000);
            
            ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(timeout * 1000);
            
            // 连接到服务器
            master.connect();
            
            logger.info("成功创建并连接到Modbus服务器: {}:{}", ip, port);
            return master;
        }
        
        @Override
        public boolean isValid(ModbusMaster connection) {
            return connection != null && connection.isConnected();
        }
        
        @Override
        public void close(ModbusMaster connection) {
            if (connection != null) {
                try {
                    if (connection.isConnected()) {
                        connection.disconnect();
                    }
                    logger.info("关闭Modbus连接");
                } catch (Exception e) {
                    logger.warn("关闭Modbus连接时发生异常", e);
                }
            }
        }
    }
}
