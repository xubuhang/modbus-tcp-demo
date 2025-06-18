package com.example.service;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.SimpleRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service("jamod")
public class JamodModbusStrategy implements ModbusStrategy {
    private static final Logger logger = LoggerFactory.getLogger(JamodModbusStrategy.class);
    
    @Value("${modbus.timeout:3}")
    private int timeout;
    
    @Autowired
    private ModbusConnectionPool connectionPool;
    
    private static final String STRATEGY_TYPE = "jamod";
    
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        TCPMasterConnection con = getConnection(ip, port);
        
        try {
            ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);
            req.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);
            trans.execute();

            ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
            int value = res.getRegister(0).getValue();
            
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
        TCPMasterConnection con = getConnection(ip, port);
        
        try {
            WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(address, new SimpleRegister(value));
            req.setUnitID(slaveId);
            ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
            trans.setRequest(req);
            trans.execute();
            
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
    private TCPMasterConnection getConnection(String ip, int port) {
        return connectionPool.getConnection(ip, port, STRATEGY_TYPE, new JamodConnectionFactory());
    }
    
    /**
     * Jamod连接工厂
     */
    private class JamodConnectionFactory implements ModbusConnectionPool.ConnectionFactory<TCPMasterConnection> {
        
        @Override
        public TCPMasterConnection create(String ip, int port) throws Exception {
            InetAddress addr = InetAddress.getByName(ip);
            TCPMasterConnection con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.setTimeout(timeout * 1000);
            
            // 连接到服务器
            con.connect();
            
            logger.info("成功创建并连接到Modbus服务器: {}:{}", ip, port);
            return con;
        }
        
        @Override
        public boolean isValid(TCPMasterConnection connection) {
            return connection != null && connection.isConnected();
        }
        
        @Override
        public void close(TCPMasterConnection connection) {
            if (connection != null) {
                try {
                    connection.close();
                    logger.info("关闭Modbus连接");
                } catch (Exception e) {
                    logger.warn("关闭Modbus连接时发生异常", e);
                }
            }
        }
    }
}
