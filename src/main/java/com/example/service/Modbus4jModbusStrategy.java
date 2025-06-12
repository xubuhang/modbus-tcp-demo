package com.example.service;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.springframework.stereotype.Service;

@Service("modbus4j")
public class Modbus4jModbusStrategy implements ModbusStrategy {
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        ModbusFactory modbusFactory = new ModbusFactory();
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(ip);
        ipParameters.setPort(port);
        
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        try {
            master.init();
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, address, DataType.TWO_BYTE_INT_UNSIGNED);
            return master.getValue(locator).intValue();
        } finally {
            master.destroy();
        }
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        ModbusFactory modbusFactory = new ModbusFactory();
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost(ip);
        ipParameters.setPort(port);
        
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        try {
            master.init();
            BaseLocator<Number> locator = BaseLocator.holdingRegister(slaveId, address, DataType.TWO_BYTE_INT_UNSIGNED);
            master.setValue(locator, value);
        } finally {
            master.destroy();
        }
    }
}
