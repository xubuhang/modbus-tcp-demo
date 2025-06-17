package com.example.service;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
@Service("jlibmodbus")
public class JlibModbusStrategy implements ModbusStrategy {
    @Value("${modbus.timeout:3}")
    private int timeout;
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        TcpParameters tcpParameters = new TcpParameters();
        tcpParameters.setHost(InetAddress.getByName(ip));
        tcpParameters.setPort(port);
        tcpParameters.setKeepAlive(true);
        tcpParameters.setConnectionTimeout(timeout*1000);
        ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        master.setResponseTimeout(timeout*1000);
        try {
            master.connect();
            int[] values = master.readHoldingRegisters(slaveId, address, 1);
            return values[0];
        } finally {
            if (master.isConnected()) {
                master.disconnect();
            }
        }
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        TcpParameters tcpParameters = new TcpParameters();
        tcpParameters.setHost(InetAddress.getByName(ip));
        tcpParameters.setPort(port);
        tcpParameters.setKeepAlive(true);
        tcpParameters.setConnectionTimeout(timeout*1000);
        ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        master.setResponseTimeout(timeout*1000);
        try {
            master.connect();
            master.writeSingleRegister(slaveId, address, value);
        } finally {
            if (master.isConnected()) {
                master.disconnect();
            }
        }
    }
}
