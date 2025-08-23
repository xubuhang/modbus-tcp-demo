package com.example.service;

import com.example.dto.ModbusTcpConfigDto;
import com.example.entity.ModbusTcpConfig;
import com.example.repository.ModbusTcpConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Modbus TCP配置管理服务
 */
@Service
public class ModbusTcpConfigService {
    
    @Autowired
    private ModbusTcpConfigRepository modbusTcpConfigRepository;
    
    /**
     * 保存新的Modbus TCP配置
     */
    @Transactional
    public ModbusTcpConfigDto saveConfig(ModbusTcpConfigDto configDto) {
        // 检查配置名称是否已存在
        if (configDto.getId() == null && modbusTcpConfigRepository.existsByConfigName(configDto.getConfigName())) {
            throw new IllegalArgumentException("配置名称已存在: " + configDto.getConfigName());
        }
        
        // 如果设置为默认配置，先将其他配置设为非默认
        if (Boolean.TRUE.equals(configDto.getIsDefault())) {
            modbusTcpConfigRepository.setOthersNotDefault(configDto.getId() != null ? configDto.getId() : 0L);
        }
        
        ModbusTcpConfig config = convertToEntity(configDto);
        ModbusTcpConfig savedConfig = modbusTcpConfigRepository.save(config);
        return convertToDto(savedConfig);
    }
    
    /**
     * 更新现有配置
     */
    @Transactional
    public ModbusTcpConfigDto updateConfig(Long id, ModbusTcpConfigDto configDto) {
        ModbusTcpConfig existingConfig = modbusTcpConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
        
        // 检查配置名称是否与其他配置重复
        if (!existingConfig.getConfigName().equals(configDto.getConfigName()) && 
            modbusTcpConfigRepository.existsByConfigName(configDto.getConfigName())) {
            throw new IllegalArgumentException("配置名称已存在: " + configDto.getConfigName());
        }
        
        // 如果设置为默认配置，先将其他配置设为非默认
        if (Boolean.TRUE.equals(configDto.getIsDefault())) {
            modbusTcpConfigRepository.setOthersNotDefault(id);
        }
        
        // 更新配置
        existingConfig.setConfigName(configDto.getConfigName());
        existingConfig.setHost(configDto.getHost());
        existingConfig.setPort(configDto.getPort());
        existingConfig.setSlaveId(configDto.getSlaveId());
        existingConfig.setTimeout(configDto.getTimeout());
        existingConfig.setDependency(configDto.getDependency());
        existingConfig.setIsDefault(configDto.getIsDefault());
        existingConfig.setIsEnabled(configDto.getIsEnabled());
        existingConfig.setDescription(configDto.getDescription());
        
        ModbusTcpConfig savedConfig = modbusTcpConfigRepository.save(existingConfig);
        return convertToDto(savedConfig);
    }
    
    /**
     * 删除配置
     */
    @Transactional
    public void deleteConfig(Long id) {
        ModbusTcpConfig config = modbusTcpConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
        
        // 如果删除的是默认配置，需要设置其他配置为默认
        if (Boolean.TRUE.equals(config.getIsDefault())) {
            List<ModbusTcpConfig> otherConfigs = modbusTcpConfigRepository.findByIdIsNotAndIsEnabledTrue(id);
            if (!otherConfigs.isEmpty()) {
                ModbusTcpConfig newDefault = otherConfigs.get(0);
                newDefault.setIsDefault(true);
                modbusTcpConfigRepository.save(newDefault);
            }
        }
        
        modbusTcpConfigRepository.deleteById(id);
    }
    
    /**
     * 获取所有配置
     */
    public List<ModbusTcpConfigDto> getAllConfigs() {
        List<ModbusTcpConfig> configs = modbusTcpConfigRepository.findAll();
        return configs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取启用的配置
     */
    public List<ModbusTcpConfigDto> getEnabledConfigs() {
        List<ModbusTcpConfig> configs = modbusTcpConfigRepository.findByIsEnabledTrue();
        return configs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取默认配置
     */
    public ModbusTcpConfigDto getDefaultConfig() {
        return modbusTcpConfigRepository.findByIsDefaultTrue()
            .map(this::convertToDto)
            .orElse(null);
    }
    
    /**
     * 根据ID获取配置
     */
    public ModbusTcpConfigDto getConfigById(Long id) {
        return modbusTcpConfigRepository.findById(id)
            .map(this::convertToDto)
            .orElse(null);
    }
    
    /**
     * 设置默认配置
     */
    @Transactional
    public void setDefaultConfig(Long id) {
        ModbusTcpConfig config = modbusTcpConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
        
        // 先将其他配置设为非默认
        modbusTcpConfigRepository.setOthersNotDefault(id);
        
        // 设置当前配置为默认
        config.setIsDefault(true);
        modbusTcpConfigRepository.save(config);
    }
    
    /**
     * 启用/禁用配置
     */
    @Transactional
    public void toggleConfigStatus(Long id, boolean enabled) {
        ModbusTcpConfig config = modbusTcpConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("配置不存在: " + id));
        
        config.setIsEnabled(enabled);
        modbusTcpConfigRepository.save(config);
    }
    
    /**
     * 测试配置连接
     */
    public boolean testConnection(ModbusTcpConfigDto configDto) {
        // 这里可以添加连接测试逻辑
        // 暂时返回true，实际项目中应该进行真实的连接测试
        return true;
    }
    
    /**
     * 将DTO转换为实体
     */
    private ModbusTcpConfig convertToEntity(ModbusTcpConfigDto dto) {
        ModbusTcpConfig entity = new ModbusTcpConfig();
        entity.setId(dto.getId());
        entity.setConfigName(dto.getConfigName());
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort());
        entity.setSlaveId(dto.getSlaveId());
        entity.setTimeout(dto.getTimeout());
        entity.setDependency(dto.getDependency());
        entity.setIsDefault(dto.getIsDefault());
        entity.setIsEnabled(dto.getIsEnabled());
        entity.setDescription(dto.getDescription());
        return entity;
    }
    
    /**
     * 将实体转换为DTO
     */
    private ModbusTcpConfigDto convertToDto(ModbusTcpConfig entity) {
        ModbusTcpConfigDto dto = new ModbusTcpConfigDto();
        dto.setId(entity.getId());
        dto.setConfigName(entity.getConfigName());
        dto.setHost(entity.getHost());
        dto.setPort(entity.getPort());
        dto.setSlaveId(entity.getSlaveId());
        dto.setTimeout(entity.getTimeout());
        dto.setDependency(entity.getDependency());
        dto.setIsDefault(entity.getIsDefault());
        dto.setIsEnabled(entity.getIsEnabled());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
