package com.example.config;

import com.example.service.ModbusConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * Modbus配置类
 * 管理连接池的生命周期
 */
@Configuration
public class ModbusConfig {
    private static final Logger logger = LoggerFactory.getLogger(ModbusConfig.class);
    
    @Autowired
    private ModbusConnectionPool connectionPool;
    
    /**
     * 应用关闭时优雅关闭连接池
     */
    @PreDestroy
    public void destroy() {
        logger.info("应用关闭，正在关闭Modbus连接池...");
        if (connectionPool != null) {
            connectionPool.shutdown();
        }
        logger.info("Modbus连接池已关闭");
    }
} 