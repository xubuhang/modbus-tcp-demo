package com.example.service;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.SimpleRegister;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
@Service("jamod")
public class JamodModbusStrategy implements ModbusStrategy {
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        InetAddress addr = InetAddress.getByName(ip);
        TCPMasterConnection con = new TCPMasterConnection(addr);
        con.setPort(port);
        con.connect();

        ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);
        req.setUnitID(slaveId);
        ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
        trans.setRequest(req);
        trans.execute();

        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
        int value = res.getRegister(0).getValue();

        con.close();
        return value;
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        InetAddress addr = InetAddress.getByName(ip);
        TCPMasterConnection con = new TCPMasterConnection(addr);
        con.setPort(port);
        con.connect();

        WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(address, new SimpleRegister(value));
        req.setUnitID(slaveId);
        ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
        trans.setRequest(req);
        trans.execute();

        con.close();
    }
}
