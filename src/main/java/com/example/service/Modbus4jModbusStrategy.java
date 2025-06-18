package com.example.service;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("modbus4j")
public class Modbus4jModbusStrategy implements ModbusStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Modbus4jModbusStrategy.class);
    
    @Value("${modbus.timeout:3}")
    private int timeout;
    
    @Autowired
    private ModbusConnectionPool connectionPool;
    
    private static final String STRATEGY_TYPE = "modbus4j";
    
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        ModbusMaster master = getConnection(ip, port);
        
        try {
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, address, DataType.TWO_BYTE_INT_UNSIGNED);
            int value = master.getValue(locator).intValue();
            
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
        ModbusMaster master = getConnection(ip, port);
        
        try {
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, address, DataType.TWO_BYTE_INT_UNSIGNED);
            master.setValue(locator, value);
            
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
        return connectionPool.getConnection(ip, port, STRATEGY_TYPE, new Modbus4jConnectionFactory());
    }
    
    /**
     * Modbus4j连接工厂
     */
    private class Modbus4jConnectionFactory implements ModbusConnectionPool.ConnectionFactory<ModbusMaster> {
        
        @Override
        public ModbusMaster create(String ip, int port) throws Exception {
            ModbusFactory modbusFactory = new ModbusFactory();
            IpParameters ipParameters = new IpParameters();
            ipParameters.setHost(ip);
            ipParameters.setPort(port);
            
            ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
            master.setTimeout(timeout * 1000);
            
            // 初始化连接
            master.init();
            
            logger.info("成功创建并连接到Modbus服务器: {}:{}", ip, port);
            return master;
        }
        
        @Override
        public boolean isValid(ModbusMaster connection) {
            // Modbus4j没有直接的连接状态检查方法，这里简单检查是否为null
            // 实际使用中可以通过尝试读取一个寄存器来检查连接状态
            return connection != null;
        }
        
        @Override
        public void close(ModbusMaster connection) {
            if (connection != null) {
                try {
                    connection.destroy();
                    logger.info("关闭Modbus连接");
                } catch (Exception e) {
                    logger.warn("关闭Modbus连接时发生异常", e);
                }
            }
        }
    }
}
