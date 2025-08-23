package com.example.dto;

/**
 * 条件配置数据传输对象
 */
public class ConditionConfigDto {
    
    private Long id;
    private String operator;
    private Integer triggerValue;
    private Integer writeAddress;
    private Integer writeValue;
    private Long modbusConfigId;
    private String description;
    
    // 构造函数
    public ConditionConfigDto() {}
    
    public ConditionConfigDto(String operator) {
        this.operator = operator;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
}
