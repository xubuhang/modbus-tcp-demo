package com.example.entity;

import javax.persistence.*;

/**
 * 条件配置实体类
 */
@Entity
@Table(name = "condition_configs")
public class ConditionConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "register_config_id", nullable = false)
    private RegisterConfig registerConfig;
    
    @Column(name = "operator", nullable = false)
    private String operator; // equals, not_equals, greater_than, less_than, greater_equal, less_equal
    
    @Column(name = "trigger_value")
    private Integer triggerValue;
    
    @Column(name = "write_address")
    private Integer writeAddress;
    
    @Column(name = "write_value")
    private Integer writeValue;
    
    @Column(name = "modbus_config_id")
    private Long modbusConfigId;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_time")
    private java.time.LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private java.time.LocalDateTime updatedTime;
    
    // 构造函数
    public ConditionConfig() {
        this.createdTime = java.time.LocalDateTime.now();
        this.updatedTime = java.time.LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RegisterConfig getRegisterConfig() {
        return registerConfig;
    }
    
    public void setRegisterConfig(RegisterConfig registerConfig) {
        this.registerConfig = registerConfig;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public Integer getTriggerValue() {
        return triggerValue;
    }
    
    public void setTriggerValue(Integer triggerValue) {
        this.triggerValue = triggerValue;
    }
    
    public Integer getWriteAddress() {
        return writeAddress;
    }
    
    public void setWriteAddress(Integer writeAddress) {
        this.writeAddress = writeAddress;
    }
    
    public Integer getWriteValue() {
        return writeValue;
    }
    
    public void setWriteValue(Integer writeValue) {
        this.writeValue = writeValue;
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
