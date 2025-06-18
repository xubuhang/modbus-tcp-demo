package com.example.controller;

import com.example.service.ModbusStrategy;
import com.example.service.ModbusStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {

    @Autowired
    private ModbusStrategyFactory strategyFactory;

    @Value("${modbus.host}")
    private String ip;
    @Value("${modbus.port}")
    private int port;
    @Value("${modbus.slave-id}")
    private int slaveId;
    @Value("${modbus.dependency}")
    private String dependency;

    @GetMapping("/read/{address}")
    public int readRegister(@PathVariable int address) throws Exception {
        ModbusStrategy strategy = strategyFactory.getStrategy(dependency);
        return strategy.readRegister(ip, port, slaveId, address);
    }

    @PostMapping("/write/{address}")
    public void writeRegister(@PathVariable int address, @RequestParam int value) throws Exception {
        ModbusStrategy strategy = strategyFactory.getStrategy(dependency);
        strategy.writeRegister(ip, port, slaveId, address, value);
    }
}

