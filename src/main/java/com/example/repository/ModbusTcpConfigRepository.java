package com.example.repository;

import com.example.entity.ModbusTcpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Modbus TCP配置数据访问接口
 */
@Repository
public interface ModbusTcpConfigRepository extends JpaRepository<ModbusTcpConfig, Long> {
    
    /**
     * 根据配置名称查找配置
     */
    Optional<ModbusTcpConfig> findByConfigName(String configName);
    
    /**
     * 查找所有启用的配置
     */
    List<ModbusTcpConfig> findByIsEnabledTrue();
    
    /**
     * 查找默认配置
     */
    Optional<ModbusTcpConfig> findByIsDefaultTrue();
    
    /**
     * 根据依赖库查找配置
     */
    List<ModbusTcpConfig> findByDependency(String dependency);
    
    /**
     * 检查配置名称是否存在
     */
    boolean existsByConfigName(String configName);
    
    /**
     * 设置其他配置为非默认
     */
    @Modifying
    @Query("UPDATE ModbusTcpConfig c SET c.isDefault = false WHERE c.id != :id")
    void setOthersNotDefault(@Param("id") Long id);
    
    /**
     * 根据主机和端口查找配置
     */
    List<ModbusTcpConfig> findByHostAndPort(String host, Integer port);
    
    /**
     * 根据ID查找非指定ID的启用配置
     */
    List<ModbusTcpConfig> findByIdIsNotAndIsEnabledTrue(Long id);
}
