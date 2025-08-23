package com.example.service;

import com.example.entity.AppSettings;
import com.example.repository.AppSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 应用程序设置服务
 */
@Service
public class AppSettingsService {
    
    public static final String AUTO_READ_ENABLED = "auto_read_enabled";
    
    @Autowired
    private AppSettingsRepository appSettingsRepository;
    
    /**
     * 获取设置值
     */
    public String getSettingValue(String key) {
        Optional<AppSettings> setting = appSettingsRepository.findBySettingKey(key);
        return setting.map(AppSettings::getSettingValue).orElse(null);
    }
    
    /**
     * 获取设置值（带默认值）
     */
    public String getSettingValue(String key, String defaultValue) {
        String value = getSettingValue(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 获取布尔设置值
     */
    public boolean getBooleanSetting(String key, boolean defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 保存或更新设置
     */
    @Transactional
    public void saveSetting(String key, String value) {
        saveSetting(key, value, null);
    }
    
    /**
     * 保存或更新设置（带描述）
     */
    @Transactional
    public void saveSetting(String key, String value, String description) {
        Optional<AppSettings> existingSetting = appSettingsRepository.findBySettingKey(key);
        
        if (existingSetting.isPresent()) {
            // 更新现有设置
            AppSettings setting = existingSetting.get();
            setting.setSettingValue(value);
            if (description != null) {
                setting.setDescription(description);
            }
            appSettingsRepository.save(setting);
        } else {
            // 创建新设置
            AppSettings newSetting = new AppSettings(key, value, description);
            appSettingsRepository.save(newSetting);
        }
    }
    
    /**
     * 保存布尔设置
     */
    @Transactional
    public void saveBooleanSetting(String key, boolean value) {
        saveSetting(key, String.valueOf(value));
    }
    
    /**
     * 删除设置
     */
    @Transactional
    public void deleteSetting(String key) {
        Optional<AppSettings> setting = appSettingsRepository.findBySettingKey(key);
        setting.ifPresent(appSettingsRepository::delete);
    }
    
    /**
     * 获取自动读取状态
     */
    public boolean isAutoReadEnabled() {
        return getBooleanSetting(AUTO_READ_ENABLED, false);
    }
    
    /**
     * 设置自动读取状态
     */
    @Transactional
    public void setAutoReadEnabled(boolean enabled) {
        saveSetting(AUTO_READ_ENABLED, String.valueOf(enabled), "自动读取寄存器开关状态");
    }
}
