package com.example.entity;

import javax.persistence.*;
import java.util.List;

/**
 * 寄存器配置实体类
 */
@Entity
@Table(name = "register_configs")
public class RegisterConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "register_address", nullable = false)
    private Integer registerAddress;
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = false;
    
    @Column(name = "modbus_config_id")
    private Long modbusConfigId;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "registerConfig", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ConditionConfig> conditions;
    
    @Column(name = "created_time")
    private java.time.LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private java.time.LocalDateTime updatedTime;
    
    // 构造函数
    public RegisterConfig() {
        this.createdTime = java.time.LocalDateTime.now();
        this.updatedTime = java.time.LocalDateTime.now();
    }
    
    public RegisterConfig(Integer registerAddress) {
        this();
        this.registerAddress = registerAddress;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getRegisterAddress() {
        return registerAddress;
    }
    
    public void setRegisterAddress(Integer registerAddress) {
        this.registerAddress = registerAddress;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public Long getModbusConfigId() {
        return modbusConfigId;
    }
    
    public void setModbusConfigId(Long modbusConfigId) {
        this.modbusConfigId = modbusConfigId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<ConditionConfig> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<ConditionConfig> conditions) {
        this.conditions = conditions;
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
