package com.example.repository;

import com.example.entity.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 应用程序设置数据访问层
 */
@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
    
    /**
     * 根据设置键查找设置
     */
    Optional<AppSettings> findBySettingKey(String settingKey);
    
    /**
     * 检查设置键是否存在
     */
    boolean existsBySettingKey(String settingKey);
}
