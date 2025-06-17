package com.example.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import io.netty.buffer.ByteBuf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
@Service("digitalpetri")
public class DigitalPetriModbusStrategy implements ModbusStrategy {
    @Value("${modbus.timeout:3}")
    private int timeout;
    @Override
    public int readRegister(String ip, int port, int slaveId, int address) throws Exception {
        ModbusTcpMaster master = null;
        try {
            ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(ip)
                    .setPort(port)
                    .setTimeout(Duration.ofSeconds(timeout))
                    .build();
            master = new ModbusTcpMaster(config);

            CompletableFuture<ReadHoldingRegistersResponse> future =
                    master.sendRequest(new ReadHoldingRegistersRequest(address, 1), slaveId)
                            .thenApply(response -> (ReadHoldingRegistersResponse) response);

            ReadHoldingRegistersResponse response = future.get();
            ByteBuf buf = response.getRegisters();
            int value = buf.getUnsignedShort(0);
            buf.release();
            return value;
        } finally {
            if (master != null) {
                master.disconnect();
            }
        }
    }

    @Override
    public void writeRegister(String ip, int port, int slaveId, int address, int value) throws Exception {
        ModbusTcpMaster master = null;
        try {
            ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(ip)
                    .setPort(port)
                    .setTimeout(Duration.ofSeconds(timeout))
                    .build();
            master = new ModbusTcpMaster(config);

            CompletableFuture<Void> future =
                    master.sendRequest(new WriteSingleRegisterRequest(address, value), slaveId)
                            .thenAccept(response -> {});
            future.get();
        } finally {
            if (master != null) {
                master.disconnect();
            }
        }
    }
}
