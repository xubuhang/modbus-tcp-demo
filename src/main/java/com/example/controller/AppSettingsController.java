package com.example.controller;

import com.example.service.AppSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 应用程序设置控制器
 */
@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class AppSettingsController {
    
    @Autowired
    private AppSettingsService appSettingsService;
    
    /**
     * 获取自动读取状态
     */
    @GetMapping("/auto-read")
    public ResponseEntity<Boolean> getAutoReadStatus() {
        try {
            boolean isEnabled = appSettingsService.isAutoReadEnabled();
            return ResponseEntity.ok(isEnabled);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    /**
     * 设置自动读取状态
     */
    @PostMapping("/auto-read")
    public ResponseEntity<String> setAutoReadStatus(@RequestParam boolean enabled) {
        try {
            appSettingsService.setAutoReadEnabled(enabled);
            return ResponseEntity.ok("自动读取状态设置成功: " + (enabled ? "开启" : "关闭"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("设置自动读取状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取设置值
     */
    @GetMapping("/{key}")
    public ResponseEntity<String> getSetting(@PathVariable String key) {
        try {
            String value = appSettingsService.getSettingValue(key);
            if (value != null) {
                return ResponseEntity.ok(value);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存设置值
     */
    @PostMapping("/{key}")
    public ResponseEntity<String> saveSetting(@PathVariable String key, @RequestParam String value) {
        try {
            appSettingsService.saveSetting(key, value);
            return ResponseEntity.ok("设置保存成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("保存设置失败: " + e.getMessage());
        }
    }
}
