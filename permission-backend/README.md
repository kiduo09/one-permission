# one-permission 后端项目

基于 Spring Boot 的企业级权限管理系统后端服务，提供RESTful API接口和完整的权限管理功能。

## 🛠️ 技术栈

- **框架**: Spring Boot 2.7.18
- **Java版本**: JDK 1.8+ (兼容性优化)
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 5.7+
- **缓存**: Redis 5.0+
- **连接池**: Druid 1.2.18
- **权限认证**: Sa-Token 1.37.0
- **密码加密**: Spring Security BCrypt
- **工具类**: Hutool 5.8.22
- **JSON处理**: FastJSON2 2.0.43
- **构建工具**: Maven 3.6+

## 📁 项目结构

```
permission-backend/
├── src/main/java/com/zhangyu/permission/
│   ├── controller/           # 控制器层 (13个控制器)
│   │   ├── AuthController.java           # 认证接口
│   │   ├── LoginUserController.java     # 管理员管理
│   │   ├── ApplicationController.java   # 应用管理
│   │   ├── NormalUserController.java    # 普通用户管理
│   │   ├── AppMenuController.java       # 应用菜单管理
│   │   ├── AppRoleController.java       # 应用角色管理
│   │   ├── AppRoleUserController.java   # 角色用户分配
│   │   ├── AppRoleDepartmentController.java # 角色部门分配
│   │   ├── StatisticsController.java    # 统计数据
│   │   ├── ConsumerInfoController.java  # 消费者管理
│   │   └── ExternalApiController.java   # 外部API接口
│   │
│   ├── service/             # 服务层
│   │   ├── impl/           # 服务实现类
│   │   ├── AuthService.java            # 认证服务
│   │   ├── LoginUserService.java       # 管理员服务
│   │   ├── ApplicationService.java     # 应用服务
│   │   ├── AppMenuService.java         # 菜单服务
│   │   ├── AppRoleService.java         # 角色服务
│   │   └── DatabaseInitializationService.java # 数据库初始化服务
│   │
│   ├── mapper/             # 数据访问层 (MyBatis-Plus)
│   │   ├── ApplicationMapper.java      # 应用Mapper
│   │   ├── LoginUserMapper.java        # 管理员Mapper
│   │   ├── AppMenuMapper.java          # 菜单Mapper
│   │   ├── AppRoleMapper.java          # 角色Mapper
│   │   └── ...                         # 其他Mapper
│   │
│   ├── entity/             # 实体类 (11个实体)
│   │   ├── LoginUser.java              # 管理员实体
│   │   ├── Application.java            # 应用实体
│   │   ├── AppMenu.java                # 菜单实体
│   │   ├── AppRole.java                # 角色实体
│   │   ├── Department.java             # 部门实体
│   │   ├── NormalUser.java             # 普通用户实体
│   │   └── ...                         # 其他实体
│   │
│   ├── dto/               # 数据传输对象 (26个DTO)
│   │   ├── LoginDTO.java               # 登录DTO
│   │   ├── LoginUserCreateDTO.java     # 管理员创建DTO
│   │   ├── ApplicationCreateDTO.java   # 应用创建DTO
│   │   └── ...                         # 其他DTO
│   │
│   ├── vo/                # 视图对象 (14个VO)
│   │   ├── LoginUserListVO.java        # 管理员列表VO
│   │   ├── ApplicationListVO.java      # 应用列表VO
│   │   └── ...                         # 其他VO
│   │
│   ├── config/            # 配置类
│   │   ├── SaTokenConfig.java          # Sa-Token配置
│   │   ├── MybatisPlusConfig.java      # MyBatis-Plus配置
│   │   ├── CorsConfig.java             # 跨域配置
│   │   └── PasswordEncoderConfig.java  # 密码编码配置
│   │
│   ├── common/            # 公共类
│   │   ├── exception/     # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── BusinessException.java
│   │   ├── Result.java    # 统一响应对象
│   │   └── PageResult.java # 分页响应对象
│   │
│   └── util/              # 工具类
│       └── PasswordUtil.java           # 密码工具类
│
├── src/main/resources/
│   ├── sql/               # SQL脚本
│   │   └── init_database.sql          # 数据库初始化脚本
│   ├── application.yml    # 主配置文件
│   ├── application-dev.yml # 开发环境配置
│   ├── application-prod.yml # 生产环境配置
│   └── logback-spring.xml # 日志配置
│
├── pom.xml                # Maven配置
└── README.md              # 项目说明
```

## 🚀 快速开始

### 环境要求

- JDK 1.8+
- MySQL 5.7+
- Redis 5.0+
- Maven 3.6+

### 基础服务启动

```bash
# 1. 启动MySQL
sudo systemctl start mysql

# 2. 启动Redis
sudo systemctl start redis-server

# 3. 创建数据库
mysql -u root -p
CREATE DATABASE permission_db DEFAULT CHARACTER SET utf8mb4;
```

### 项目启动

#### 方式1：使用IDE
1. 导入项目到IDE (IntelliJ IDEA推荐)
2. 修改 `application.yml` 中的数据库和Redis配置
3. 运行 `PermissionApplication.java`

#### 方式2：命令行
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

服务将在 `http://localhost:1105/one-permission` 启动

### 验证启动

```bash
# 检查服务状态
curl http://localhost:1105/one-permission/actuator/health

# 测试登录接口
curl -X POST http://localhost:1105/one-permission/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginAccount":"admin","password":"123456"}'
```

## ⚙️ 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/permission_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
```

### Redis配置

```yaml
spring:
  redis:
    host: 10.1.120.44
    port: 6379
    password: Xdsw_2021.Com
    database: 2
    timeout: 2000ms

    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### Sa-Token配置

```yaml
sa-token:
  dao:
    type: redis-jackson  # Redis存储

  token-name: satoken
  timeout: 7200         # 2小时有效期
  activity-timeout: -1  # 不启用活动超时
  is-concurrent: true   # 允许并发登录
  is-share: false       # 每次登录新建token
  token-style: uuid     # Token风格
```

## 🔧 开发指南

### API设计规范

- RESTful API设计
- 统一响应格式：`Result<T>`
- 分页响应格式：`PageResult<T>`
- 异常统一处理：`GlobalExceptionHandler`

### 数据层设计

- 使用MyBatis-Plus简化CRUD操作
- 实体类继承 `BaseEntity` 获取公共字段
- 逻辑删除字段 `deleted`
- 自动填充创建时间和更新时间

### 权限设计

- **系统管理员**: 可以访问所有功能 (`admin_type = 2`)
- **普通管理员**: 只能管理授权的应用 (`admin_type = 1`)
- **菜单权限**: 基于角色的权限控制
- **数据权限**: 基于用户的权限过滤

### 数据库初始化

项目启动时会自动：
1. 检查表是否存在，不存在则创建
2. 检查数据是否存在，不存在则初始化
3. 使用 `CREATE TABLE IF NOT EXISTS` 确保安全

## 📊 数据库设计

### 核心表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| login_users | 系统管理员 | id, login_account, name, admin_type |
| applications | 应用信息 | id, name, app_key, status |
| departments | 部门信息 | id, name, parent_id, level |
| normal_users | 普通用户 | id, work_no, name, department_id |
| app_menus | 应用菜单 | id, app_id, name, menu_type, parent_id |
| app_roles | 应用角色 | id, app_id, name, system_id |
| app_role_menus | 角色菜单权限 | role_id, menu_id |
| app_role_users | 角色用户分配 | role_id, user_work_no |
| app_role_departments | 角色部门分配 | role_id, department_id |
| login_user_apps | 管理员应用授权 | login_user_id, app_id |
| consumer_info | API消费者信息 | id, client_id, client_secret |

### 设计特点

- **无外键约束**: 关联关系在应用层维护，提高性能
- **逻辑删除**: 使用 `deleted` 字段进行软删除
- **自动初始化**: 启动时自动创建表和数据
- **字符集**: 统一使用UTF8MB4支持 emoji

## 🔗 API文档

### 主要接口

- **认证接口**: `/auth/login`, `/auth/current`, `/auth/logout`
- **管理员管理**: `/login-users/*`
- **应用管理**: `/applications/*`
- **菜单管理**: `/applications/{appId}/menus/*`
- **角色管理**: `/applications/{appId}/roles/*`
- **用户分配**: `/applications/{appId}/roles/{roleId}/users/*`
- **统计接口**: `/statistics/dashboard`

### 文档地址

- API文档: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- 在线测试: 启动服务后访问 `/swagger-ui.html`

## 🔒 安全特性

- **密码加密**: BCrypt算法加密存储
- **Token认证**: 基于Sa-Token的JWT认证
- **Session管理**: Redis存储会话信息
- **权限验证**: 方法级和URL级权限控制
- **跨域支持**: CORS配置支持前端调用
- **SQL注入防护**: Druid连接池+参数化查询

## 📦 部署说明

### JAR包部署

```bash
# 打包
mvn clean package

# 运行
java -jar target/permission-backend-1.0.0.jar
```

### Docker部署

```dockerfile
FROM openjdk:8-jre-alpine
COPY target/permission-backend-1.0.0.jar app.jar
EXPOSE 1105
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 生产环境配置

1. 使用 `application-prod.yml` 配置
2. 设置环境变量或外部配置文件
3. 配置Nginx反向代理
4. 设置SSL证书
5. 配置日志轮转

## 🔍 监控和调试

### Druid监控

启动服务后访问：`http://localhost:1105/druid/`
- 用户名: admin
- 密码: admin123

### 健康检查

```bash
# 应用健康检查
curl http://localhost:1105/one-permission/actuator/health

# 数据库连接检查
curl http://localhost:1105/one-permission/actuator/health/db
```

### 日志配置

- 开发环境: `application-dev.yml`
- 生产环境: `application-prod.yml`
- 日志级别: DEBUG (开发), INFO (生产)

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 编写代码和测试
4. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
5. 推送到分支 (`git push origin feature/AmazingFeature`)
6. 创建 Pull Request

## 📞 技术支持

- 项目地址: [https://github.com/kiduo09/one-permission](https://github.com/kiduo09/one-permission)
- 后端技术栈: Spring Boot + MyBatis-Plus + Sa-Token + Redis
- 前端项目: [../permission-front/README.md](../permission-front/README.md)
- 项目总述: [../README.md](../README.md)

## 📋 更新日志

### v1.0.0
- ✅ 完整的权限管理系统
- ✅ 支持多应用、多角色、多用户
- ✅ Redis集成存储
- ✅ 自动数据库初始化
- ✅ RESTful API设计
- ✅ Swagger API文档
- ✅ 统一异常处理
- ✅ 跨域支持

---

**one-permission 后端服务** - 为前端提供强大的权限管理API支持！
