package com.example.entity;

import javax.persistence.*;

/**
 * Modbus TCP配置实体类
 */
@Entity
@Table(name = "modbus_tcp_configs")
public class ModbusTcpConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_name", nullable = false, unique = true)
    private String configName; // 配置名称，用于标识不同的配置
    
    @Column(name = "host", nullable = false)
    private String host; // Modbus设备IP地址
    
    @Column(name = "port", nullable = false)
    private Integer port; // Modbus TCP端口
    
    @Column(name = "slave_id", nullable = false)
    private Integer slaveId; // 从站地址
    
    @Column(name = "timeout")
    private Integer timeout; // 超时时间（秒）
    
    @Column(name = "dependency", nullable = false)
    private String dependency; // 使用的Modbus库: digitalpetri, jlibmodbus, modbus4j, jamod
    
    @Column(name = "is_default")
    private Boolean isDefault = false; // 是否为默认配置
    
    @Column(name = "is_enabled")
    private Boolean isEnabled = true; // 是否启用
    
    @Column(name = "description")
    private String description; // 配置描述
    
    @Column(name = "created_time")
    private java.time.LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private java.time.LocalDateTime updatedTime;
    
    // 构造函数
    public ModbusTcpConfig() {
        this.createdTime = java.time.LocalDateTime.now();
        this.updatedTime = java.time.LocalDateTime.now();
        this.isDefault = false;
        this.isEnabled = true;
    }
    
    public ModbusTcpConfig(String configName, String host, Integer port, Integer slaveId, String dependency) {
        this();
        this.configName = configName;
        this.host = host;
        this.port = port;
        this.slaveId = slaveId;
        this.dependency = dependency;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getConfigName() {
        return configName;
    }
    
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public Integer getSlaveId() {
        return slaveId;
    }
    
    public void setSlaveId(Integer slaveId) {
        this.slaveId = slaveId;
    }
    
    public Integer getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public String getDependency() {
        return dependency;
    }
    
    public void setDependency(String dependency) {
        this.dependency = dependency;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public java.time.LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(java.time.LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public java.time.LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(java.time.LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedTime = java.time.LocalDateTime.now();
    }
}
