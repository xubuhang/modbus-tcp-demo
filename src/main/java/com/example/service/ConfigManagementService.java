package com.example.service;

import com.example.dto.RegisterConfigDto;
import com.example.dto.ConditionConfigDto;
import com.example.entity.RegisterConfig;
import com.example.entity.ConditionConfig;
import com.example.repository.RegisterConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置管理服务
 */
@Service
public class ConfigManagementService {
    
    @Autowired
    private RegisterConfigRepository registerConfigRepository;
    
    /**
     * 保存所有寄存器配置
     */
    @Transactional
    public void saveAllConfigs(List<RegisterConfigDto> configs) {
        // 清空现有配置
        registerConfigRepository.deleteAll();
        
        // 保存新配置
        for (RegisterConfigDto configDto : configs) {
            RegisterConfig registerConfig = new RegisterConfig(configDto.getRegisterAddress());
            registerConfig.setIsEnabled(configDto.getIsEnabled());
            registerConfig.setModbusConfigId(configDto.getModbusConfigId());
            registerConfig.setDescription(configDto.getDescription());
            
            // 保存寄存器配置
            RegisterConfig savedRegister = registerConfigRepository.save(registerConfig);
            
            // 保存条件配置
            if (configDto.getConditions() != null) {
                List<ConditionConfig> conditions = configDto.getConditions().stream()
                    .map(conditionDto -> {
                        ConditionConfig condition = new ConditionConfig();
                        condition.setRegisterConfig(savedRegister);
                        condition.setOperator(conditionDto.getOperator());
                        condition.setTriggerValue(conditionDto.getTriggerValue());
                        condition.setWriteAddress(conditionDto.getWriteAddress());
                        condition.setWriteValue(conditionDto.getWriteValue());
                        condition.setModbusConfigId(conditionDto.getModbusConfigId());
                        condition.setDescription(conditionDto.getDescription());
                        return condition;
                    })
                    .collect(Collectors.toList());
                
                savedRegister.setConditions(conditions);
            }
        }
    }
    
    /**
     * 加载所有寄存器配置
     */
    public List<RegisterConfigDto> loadAllConfigs() {
        List<RegisterConfig> configs = registerConfigRepository.findAll();
        
        return configs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * 加载启用的寄存器配置
     */
    public List<RegisterConfigDto> loadEnabledConfigs() {
        List<RegisterConfig> configs = registerConfigRepository.findByIsEnabledTrue();
        
        return configs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为DTO
     */
    private RegisterConfigDto convertToDto(RegisterConfig entity) {
        RegisterConfigDto dto = new RegisterConfigDto();
        dto.setId(entity.getId());
        dto.setRegisterAddress(entity.getRegisterAddress());
        dto.setIsEnabled(entity.getIsEnabled());
        dto.setModbusConfigId(entity.getModbusConfigId());
        dto.setDescription(entity.getDescription());
        
        if (entity.getConditions() != null) {
            List<ConditionConfigDto> conditionDtos = entity.getConditions().stream()
                .map(this::convertConditionToDto)
                .collect(Collectors.toList());
            dto.setConditions(conditionDtos);
        }
        
        return dto;
    }
    
    /**
     * 将条件实体转换为DTO
     */
    private ConditionConfigDto convertConditionToDto(ConditionConfig entity) {
        ConditionConfigDto dto = new ConditionConfigDto();
        dto.setId(entity.getId());
        dto.setOperator(entity.getOperator());
        dto.setTriggerValue(entity.getTriggerValue());
        dto.setWriteAddress(entity.getWriteAddress());
        dto.setWriteValue(entity.getWriteValue());
        dto.setModbusConfigId(entity.getModbusConfigId());
        dto.setDescription(entity.getDescription());
        return dto;
    }
    
    /**
     * 清空所有配置
     */
    @Transactional
    public void clearAllConfigs() {
        registerConfigRepository.deleteAll();
    }
    
    /**
     * 获取配置数量
     */
    public long getConfigCount() {
        return registerConfigRepository.count();
    }
}
