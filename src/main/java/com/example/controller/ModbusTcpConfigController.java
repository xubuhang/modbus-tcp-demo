package com.example.controller;

import com.example.dto.ModbusTcpConfigDto;
import com.example.service.ModbusTcpConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Modbus TCP配置管理控制器
 */
@RestController
@RequestMapping("/api/modbus-config")
@CrossOrigin(origins = "*")
public class ModbusTcpConfigController {
    
    @Autowired
    private ModbusTcpConfigService modbusTcpConfigService;
    
    /**
     * 创建新的Modbus TCP配置
     */
    @PostMapping("/create")
    public ResponseEntity<?> createConfig(@RequestBody ModbusTcpConfigDto configDto) {
        try {
            ModbusTcpConfigDto savedConfig = modbusTcpConfigService.saveConfig(configDto);
            return ResponseEntity.ok(savedConfig);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("配置创建失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("配置创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新现有配置
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateConfig(@PathVariable Long id, @RequestBody ModbusTcpConfigDto configDto) {
        try {
            ModbusTcpConfigDto updatedConfig = modbusTcpConfigService.updateConfig(id, configDto);
            return ResponseEntity.ok(updatedConfig);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("配置更新失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("配置更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteConfig(@PathVariable Long id) {
        try {
            modbusTcpConfigService.deleteConfig(id);
            return ResponseEntity.ok("配置删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("配置删除失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("配置删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有配置
     */
    @GetMapping("/list")
    public ResponseEntity<List<ModbusTcpConfigDto>> getAllConfigs() {
        try {
            List<ModbusTcpConfigDto> configs = modbusTcpConfigService.getAllConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * 获取启用的配置
     */
    @GetMapping("/list/enabled")
    public ResponseEntity<List<ModbusTcpConfigDto>> getEnabledConfigs() {
        try {
            List<ModbusTcpConfigDto> configs = modbusTcpConfigService.getEnabledConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * 获取默认配置
     */
    @GetMapping("/default")
    public ResponseEntity<ModbusTcpConfigDto> getDefaultConfig() {
        try {
            ModbusTcpConfigDto config = modbusTcpConfigService.getDefaultConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * 根据ID获取配置
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModbusTcpConfigDto> getConfigById(@PathVariable Long id) {
        try {
            ModbusTcpConfigDto config = modbusTcpConfigService.getConfigById(id);
            if (config != null) {
                return ResponseEntity.ok(config);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    /**
     * 设置默认配置
     */
    @PostMapping("/set-default/{id}")
    public ResponseEntity<String> setDefaultConfig(@PathVariable Long id) {
        try {
            modbusTcpConfigService.setDefaultConfig(id);
            return ResponseEntity.ok("默认配置设置成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("设置默认配置失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("设置默认配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用配置
     */
    @PostMapping("/toggle-status/{id}")
    public ResponseEntity<String> toggleConfigStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            modbusTcpConfigService.toggleConfigStatus(id, enabled);
            String status = enabled ? "启用" : "禁用";
            return ResponseEntity.ok("配置" + status + "成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("状态切换失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("状态切换失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试配置连接
     */
    @PostMapping("/test-connection")
    public ResponseEntity<String> testConnection(@RequestBody ModbusTcpConfigDto configDto) {
        try {
            boolean success = modbusTcpConfigService.testConnection(configDto);
            if (success) {
                return ResponseEntity.ok("连接测试成功");
            } else {
                return ResponseEntity.badRequest().body("连接测试失败");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("连接测试失败: " + e.getMessage());
        }
    }
}
