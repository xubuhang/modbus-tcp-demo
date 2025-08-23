# Modbus TCP 通信演示项目

## 项目简介

这是一个基于Spring Boot的Modbus TCP通信演示项目，支持多种Modbus库实现，提供Web界面进行寄存器读写测试和条件触发控制。

## 主要功能

### 1. Modbus通信功能
- 支持多种Modbus库：DigitalPetri、JLibModbus、Modbus4j、Jamod
- 读写保持寄存器（Holding Register）
- 连接池管理和连接测试

### 2. Web测试界面
- 寄存器动态管理（添加/删除）
- 自动读取控制（每秒读取一次）
- 条件触发系统（支持多种比较操作）
- 实时数据显示和操作

### 3. 配置持久化功能 ⭐ 新功能
- **配置保存**：将页面上的所有寄存器配置和条件设置保存到数据库
- **配置加载**：从数据库加载之前保存的配置，恢复页面状态
- **配置清空**：清空数据库中的所有配置
- **自动恢复**：页面刷新后可通过"加载配置"按钮恢复之前的工作状态

### 4. Modbus TCP配置管理 ⭐ 新功能
- **多配置支持**：支持创建和管理多个Modbus TCP连接配置
- **界面配置**：通过Web界面录入Modbus TCP连接参数，无需修改配置文件
- **配置管理**：支持配置的增删改查、启用/禁用、设置默认配置等操作
- **连接测试**：提供连接测试功能，验证配置的正确性

## 技术架构

- **后端**：Spring Boot 2.5.14 + Spring Data JPA
- **数据库**：H2内存数据库（支持文件持久化）
- **前端**：Bootstrap + jQuery + 原生JavaScript
- **Modbus库**：支持4种主流Java Modbus实现

## 快速开始

### 1. 环境要求
- Java 8+
- Maven 3.6+

### 2. 启动项目
```bash
mvn spring-boot:run
```

### 3. 访问应用
- 主界面：http://localhost:8080
- H2数据库控制台：http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./modbus_config_db`
  - 用户名: `sa`
  - 密码: 留空

## 配置说明

### 应用配置 (application.yml)
```yaml
modbus:
  host: 127.0.0.1  # PLC IP地址
  port: 502         # Modbus TCP端口
  slave-id: 1       # 从站地址
  dependency: digitalpetri  # 使用的Modbus库
```

### 数据库配置
项目使用H2数据库，数据文件保存在项目根目录的`modbus_config_db.mv.db`文件中。

## 使用说明

### 基本操作
1. **添加寄存器**：点击"添加寄存器"按钮添加新的寄存器行
2. **设置地址**：为每个寄存器设置Modbus地址
3. **读写操作**：手动读取和写入寄存器值
4. **条件设置**：为寄存器配置触发条件和自动写入逻辑

### 配置持久化操作
1. **保存配置**：点击"保存配置"按钮，将当前页面所有配置保存到数据库
2. **加载配置**：点击"加载配置"按钮，从数据库恢复之前保存的配置
3. **清空配置**：点击"清空配置"按钮，删除数据库中的所有配置

### Modbus TCP配置管理操作
1. **新增配置**：点击"新增配置"按钮，填写Modbus TCP连接参数
2. **编辑配置**：点击配置项的"编辑"按钮，修改现有配置
3. **删除配置**：点击配置项的"删除"按钮，删除不需要的配置
4. **设置默认**：点击"设为默认"按钮，将配置设为默认配置
5. **启用/禁用**：点击"启用"/"禁用"按钮，控制配置的使用状态
6. **测试连接**：点击"测试连接"按钮，验证配置的正确性

### 条件触发系统
支持以下触发条件：
- 等于、不等于
- 大于、小于
- 大于等于、小于等于

当条件满足时，系统会自动向指定地址写入指定值。

## 数据库表结构

### register_configs (寄存器配置表)
- `id`: 主键
- `register_address`: 寄存器地址
- `is_enabled`: 是否启用条件触发
- `created_time`: 创建时间
- `updated_time`: 更新时间

### condition_configs (条件配置表)
- `id`: 主键
- `register_config_id`: 关联的寄存器配置ID
- `operator`: 触发条件操作符
- `trigger_value`: 触发值
- `write_address`: 写入目标地址
- `write_value`: 写入值
- `created_time`: 创建时间
- `updated_time`: 更新时间

### modbus_tcp_configs (Modbus TCP配置表)
- `id`: 主键
- `config_name`: 配置名称（唯一）
- `host`: Modbus设备IP地址
- `port`: Modbus TCP端口
- `slave_id`: 从站地址
- `timeout`: 超时时间（秒）
- `dependency`: 使用的Modbus库
- `is_default`: 是否为默认配置
- `is_enabled`: 是否启用
- `description`: 配置描述
- `created_time`: 创建时间
- `updated_time`: 更新时间

## API接口

### 配置管理接口
- `POST /api/config/save` - 保存配置
- `GET /api/config/load` - 加载所有配置
- `GET /api/config/load/enabled` - 加载启用的配置
- `DELETE /api/config/clear` - 清空配置
- `GET /api/config/count` - 获取配置数量

### Modbus TCP配置管理接口
- `POST /api/modbus-config/create` - 创建新配置
- `PUT /api/modbus-config/update/{id}` - 更新配置
- `DELETE /api/modbus-config/delete/{id}` - 删除配置
- `GET /api/modbus-config/list` - 获取所有配置
- `GET /api/modbus-config/list/enabled` - 获取启用的配置
- `GET /api/modbus-config/default` - 获取默认配置
- `GET /api/modbus-config/{id}` - 根据ID获取配置
- `POST /api/modbus-config/set-default/{id}` - 设置默认配置
- `POST /api/modbus-config/toggle-status/{id}` - 启用/禁用配置
- `POST /api/modbus-config/test-connection` - 测试连接

### Modbus操作接口
- `GET /api/modbus/read/{address}` - 读取寄存器
- `POST /api/modbus/write/{address}?value={value}` - 写入寄存器

## 注意事项

1. **数据持久化**：配置数据保存在H2数据库文件中，重启应用后数据仍然存在
2. **条件验证**：只有完整配置的条件（包含触发值、写入地址、写入值）才会被保存
3. **自动恢复**：页面刷新后，需要手动点击"加载配置"按钮来恢复之前的工作状态
4. **数据库访问**：可通过H2控制台查看和调试数据库内容

## 扩展功能

项目采用策略模式设计，可以轻松扩展：
- 添加新的Modbus库实现
- 扩展条件触发逻辑
- 增加数据统计和分析功能
- 支持更多Modbus功能码

## 故障排除

### 常见问题
1. **Modbus连接失败**：检查IP地址、端口和从站地址配置
2. **配置保存失败**：检查数据库连接和表结构
3. **页面显示异常**：检查浏览器控制台错误信息

### 日志查看
应用日志级别设置为DEBUG，可在控制台查看详细的Modbus通信和数据库操作日志。
