package com.example.controller;

import com.example.service.ModbusStrategy;
import com.example.service.ModbusStrategyFactory;
import com.example.service.ModbusTcpConfigService;
import com.example.dto.ModbusTcpConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {

    @Autowired
    private ModbusStrategyFactory strategyFactory;
    
    @Autowired
    private ModbusTcpConfigService modbusTcpConfigService;

    @Value("${modbus.host}")
    private String defaultIp;
    @Value("${modbus.port}")
    private int defaultPort;
    @Value("${modbus.slave-id}")
    private int defaultSlaveId;
    @Value("${modbus.dependency}")
    private String defaultDependency;

    @GetMapping("/read/{address}")
    public int readRegister(@PathVariable int address, @RequestParam(required = false) Long configId) throws Exception {
        if (configId != null) {
            // 使用数据库中的配置
            ModbusTcpConfigDto config = modbusTcpConfigService.getConfigById(configId);
            if (config == null) {
                throw new IllegalArgumentException("配置不存在: " + configId);
            }
            if (!config.getIsEnabled()) {
                throw new IllegalArgumentException("配置已禁用: " + configId);
            }
            
            ModbusStrategy strategy = strategyFactory.getStrategy(config.getDependency());
            return strategy.readRegister(config.getHost(), config.getPort(), config.getSlaveId(), address);
        } else {
            // 使用默认配置
            ModbusStrategy strategy = strategyFactory.getStrategy(defaultDependency);
            return strategy.readRegister(defaultIp, defaultPort, defaultSlaveId, address);
        }
    }

    @PostMapping("/write/{address}")
    public void writeRegister(@PathVariable int address, @RequestParam int value, @RequestParam(required = false) Long configId) throws Exception {
        if (configId != null) {
            // 使用数据库中的配置
            ModbusTcpConfigDto config = modbusTcpConfigService.getConfigById(configId);
            if (config == null) {
                throw new IllegalArgumentException("配置不存在: " + configId);
            }
            if (!config.getIsEnabled()) {
                throw new IllegalArgumentException("配置已禁用: " + configId);
            }
            
            ModbusStrategy strategy = strategyFactory.getStrategy(config.getDependency());
            strategy.writeRegister(config.getHost(), config.getPort(), config.getSlaveId(), address, value);
        } else {
            // 使用默认配置
            ModbusStrategy strategy = strategyFactory.getStrategy(defaultDependency);
            strategy.writeRegister(defaultIp, defaultPort, defaultSlaveId, address, value);
        }
    }
}

