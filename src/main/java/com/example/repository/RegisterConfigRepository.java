package com.example.repository;

import com.example.entity.RegisterConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 寄存器配置数据访问接口
 */
@Repository
public interface RegisterConfigRepository extends JpaRepository<RegisterConfig, Long> {
    
    /**
     * 根据寄存器地址查找配置
     */
    RegisterConfig findByRegisterAddress(Integer registerAddress);
    
    /**
     * 查找所有启用的寄存器配置
     */
    List<RegisterConfig> findByIsEnabledTrue();
    
    /**
     * 根据地址范围查找配置
     */
    List<RegisterConfig> findByRegisterAddressBetween(Integer startAddress, Integer endAddress);
}
