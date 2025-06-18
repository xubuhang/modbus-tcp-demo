package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Modbus连接池管理器
 * 支持连接复用、健康检查和自动重连
 */
@Component
public class ModbusConnectionPool {
    @Value("${modbus.timeout:3}")
    private int timeout;
    private static final Logger logger = LoggerFactory.getLogger(ModbusConnectionPool.class);
    
    // 连接池：key为连接标识，value为连接包装器
    private final ConcurrentHashMap<String, ModbusConnectionWrapper> connectionPool = new ConcurrentHashMap<>();
    
    // 连接锁：防止并发创建同一个连接
    private final ConcurrentHashMap<String, ReentrantLock> connectionLocks = new ConcurrentHashMap<>();
    
    // 健康检查线程池
    private final ScheduledExecutorService healthCheckExecutor = Executors.newScheduledThreadPool(2);
    
    // 连接超时时间（毫秒）
    private static final long CONNECTION_TIMEOUT = 30000;

    // 健康检查间隔（秒）
    private static final long HEALTH_CHECK_INTERVAL = 30;
    
    public ModbusConnectionPool() {
        // 启动健康检查任务
        startHealthCheck();
    }
    
    /**
     * 获取或创建连接
     */
    public <T> T getConnection(String ip, int port, String strategyType, ConnectionFactory<T> factory) {
        String connectionKey = buildConnectionKey(ip, port, strategyType);
        
        ModbusConnectionWrapper wrapper = connectionPool.get(connectionKey);
        
        // 检查连接是否有效
        if (wrapper != null && wrapper.isValid()) {
            wrapper.updateLastUsed();
            return (T) wrapper.getConnection();
        }
        
        // 需要创建新连接，使用锁防止并发创建
        ReentrantLock lock = connectionLocks.computeIfAbsent(connectionKey, k -> new ReentrantLock());
        
        lock.lock();
        try {
            // 双重检查
            wrapper = connectionPool.get(connectionKey);
            if (wrapper != null && wrapper.isValid()) {
                wrapper.updateLastUsed();
                return (T) wrapper.getConnection();
            }
            
            // 创建新连接
            logger.info("创建新的Modbus连接: {}", connectionKey);
            T connection = factory.create(ip, port);
            wrapper = new ModbusConnectionWrapper(connection, factory);
            connectionPool.put(connectionKey, wrapper);
            
            return connection;
            
        } catch (Exception e) {
            logger.error("创建Modbus连接失败: {}", connectionKey, e);
            throw new RuntimeException("创建Modbus连接失败", e);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 释放连接（实际上是标记为可复用）
     */
    public void releaseConnection(String ip, int port, String strategyType) {
        String connectionKey = buildConnectionKey(ip, port, strategyType);
        ModbusConnectionWrapper wrapper = connectionPool.get(connectionKey);
        if (wrapper != null) {
            wrapper.updateLastUsed();
        }
    }
    
    /**
     * 强制移除连接
     */
    public void removeConnection(String ip, int port, String strategyType) {
        String connectionKey = buildConnectionKey(ip, port, strategyType);
        ModbusConnectionWrapper wrapper = connectionPool.remove(connectionKey);
        if (wrapper != null) {
            wrapper.close();
            logger.info("移除Modbus连接: {}", connectionKey);
        }
    }
    
    /**
     * 启动健康检查
     */
    private void startHealthCheck() {
        healthCheckExecutor.scheduleWithFixedDelay(() -> {
            try {
                checkAndCleanupConnections();
            } catch (Exception e) {
                logger.error("健康检查异常", e);
            }
        }, HEALTH_CHECK_INTERVAL, HEALTH_CHECK_INTERVAL, TimeUnit.SECONDS);
    }
    
    /**
     * 检查并清理无效连接
     */
    private void checkAndCleanupConnections() {
        long currentTime = System.currentTimeMillis();

        connectionPool.entrySet().removeIf(entry -> {
            ModbusConnectionWrapper wrapper = entry.getValue();
            
            // 检查连接是否超时或无效
            if (!wrapper.isValid() || (currentTime - wrapper.getLastUsed()) > CONNECTION_TIMEOUT) {
                logger.info("清理无效或超时的连接: {}", entry.getKey());
                wrapper.close();
                return true;
            }
            
            return false;
        });
    }
    
    /**
     * 构建连接标识
     */
    private String buildConnectionKey(String ip, int port, String strategyType) {
        return String.format("%s:%d:%s", ip, port, strategyType);
    }
    
    /**
     * 关闭连接池
     */
    public void shutdown() {
        logger.info("关闭Modbus连接池");
        
        // 关闭所有连接
        connectionPool.values().forEach(ModbusConnectionWrapper::close);
        connectionPool.clear();
        
        // 关闭线程池
        healthCheckExecutor.shutdown();
    }
    
    /**
     * 连接工厂接口
     */
    @FunctionalInterface
    public interface ConnectionFactory<T> {
        T create(String ip, int port) throws Exception;
        
        default boolean isValid(T connection) {
            return connection != null;
        }
        
        default void close(T connection) {
            // 默认实现为空，子类可以重写
        }
    }
    
    /**
     * 连接包装器
     */
    private static class ModbusConnectionWrapper {
        private final Object connection;
        private final ConnectionFactory factory;
        private volatile long lastUsed;
        
        public ModbusConnectionWrapper(Object connection, ConnectionFactory factory) {
            this.connection = connection;
            this.factory = factory;
            this.lastUsed = System.currentTimeMillis();
        }
        
        public Object getConnection() {
            return connection;
        }
        
        public long getLastUsed() {
            return lastUsed;
        }
        
        public void updateLastUsed() {
            this.lastUsed = System.currentTimeMillis();
        }
        
        public boolean isValid() {
            return factory.isValid(connection);
        }
        
        public void close() {
            try {
                factory.close(connection);
            } catch (Exception e) {
                // 忽略关闭异常
            }
        }
    }
} 