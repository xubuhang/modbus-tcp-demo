package com.example.controller;

import com.example.dto.RegisterConfigDto;
import com.example.service.ConfigManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配置管理控制器
 */
@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {
    
    @Autowired
    private ConfigManagementService configManagementService;
    
    /**
     * 保存所有寄存器配置
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveConfigs(@RequestBody List<RegisterConfigDto> configs) {
        try {
            configManagementService.saveAllConfigs(configs);
            return ResponseEntity.ok("配置保存成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("配置保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 加载所有寄存器配置
     */
    @GetMapping("/load")
    public ResponseEntity<List<RegisterConfigDto>> loadConfigs() {
        try {
            List<RegisterConfigDto> configs = configManagementService.loadAllConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * 加载启用的寄存器配置
     */
    @GetMapping("/load/enabled")
    public ResponseEntity<List<RegisterConfigDto>> loadEnabledConfigs() {
        try {
            List<RegisterConfigDto> configs = configManagementService.loadEnabledConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    /**
     * 清空所有配置
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearConfigs() {
        try {
            configManagementService.clearAllConfigs();
            return ResponseEntity.ok("配置清空成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("配置清空失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取配置数量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getConfigCount() {
        try {
            long count = configManagementService.getConfigCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0L);
        }
    }
}
