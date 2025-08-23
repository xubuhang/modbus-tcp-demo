package com.example.dto;

/**
 * Modbus TCP配置数据传输对象
 */
public class ModbusTcpConfigDto {
    
    private Long id;
    private String configName;
    private String host;
    private Integer port;
    private Integer slaveId;
    private Integer timeout;
    private String dependency;
    private Boolean isDefault;
    private Boolean isEnabled;
    private String description;
    
    // 构造函数
    public ModbusTcpConfigDto() {}
    
    public ModbusTcpConfigDto(String configName, String host, Integer port, Integer slaveId, String dependency) {
        this.configName = configName;
        this.host = host;
        this.port = port;
        this.slaveId = slaveId;
        this.dependency = dependency;
        this.isDefault = false;
        this.isEnabled = true;
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
}
