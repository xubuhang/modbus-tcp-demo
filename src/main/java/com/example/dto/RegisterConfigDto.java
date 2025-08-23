package com.example.dto;

import java.util.List;

/**
 * 寄存器配置数据传输对象
 */
public class RegisterConfigDto {
    
    private Long id;
    private Integer registerAddress;
    private Boolean isEnabled;
    private Long modbusConfigId; // 添加Modbus配置ID字段
    private String description;
    private List<ConditionConfigDto> conditions;
    
    // 构造函数
    public RegisterConfigDto() {}
    
    public RegisterConfigDto(Integer registerAddress) {
        this.registerAddress = registerAddress;
        this.isEnabled = false;
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
    
    public List<ConditionConfigDto> getConditions() {
        return conditions;
    }
    
    public void setConditions(List<ConditionConfigDto> conditions) {
        this.conditions = conditions;
    }
}
