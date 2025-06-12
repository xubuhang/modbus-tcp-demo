package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModbusStrategyFactory {

    // key为@Qualifier里的名字，value为对应实现
    private final Map<String, ModbusStrategy> strategyMap;

    @Autowired
    public ModbusStrategyFactory(Map<String, ModbusStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public ModbusStrategy getStrategy(String type) {
        ModbusStrategy strategy = strategyMap.get(type.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的Modbus实现类型: " + type);
        }
        return strategy;
    }
}
