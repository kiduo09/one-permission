# ONE-PERMISSION 权限管理系统

<div align="right">

[![GitHub stars](https://img.shields.io/github/stars/kiduo09/one-permission.svg?style=social&label=Star)](https://github.com/kiduo09/one-permission)
[![GitHub forks](https://img.shields.io/github/forks/kiduo09/one-permission.svg?style=social&label=Fork)](https://github.com/kiduo09/one-permission/fork)
[![GitHub watchers](https://img.shields.io/github/watchers/kiduo09/one-permission.svg?style=social&label=Watch)](https://github.com/kiduo09/one-permission)

</div>

## 📖 项目介绍

ONE-PERMISSION 是一个基于 Vue3 + Spring Boot 的企业级权限管理系统，提供完整的应用权限管理、用户授权、角色管理、菜单管理等功能。系统采用前后端分离架构，支持多应用、多角色、多用户的精细化权限控制。

### 🎯 项目特色

**🚀 开箱即用**: 配置数据库连接后启动项目，系统会自动创建所有表结构并初始化测试数据，无需手动执行SQL脚本！

### ✨ 核心特性

- 🔐 **多层级权限管理**：支持系统级和应用级权限管理
- 👥 **灵活的用户授权**：支持按用户和按部门两种授权方式
- 🎯 **精细化权限控制**：菜单、按钮级别的权限控制
- 📊 **数据统计分析**：Dashboard 数据看板，实时统计系统数据
- 🎨 **现代化UI设计**：基于 Ant Design Vue，支持5种主题切换
- 🔒 **安全可靠**：密码加密存储，Token 认证，权限验证
- ⚡ **自动初始化**：启动时自动创建表结构和测试数据

## 🛠️ 技术栈

### 前端技术

- **框架**：Vue 3.2+ (Composition API)
- **构建工具**：Vite 4.0+
- **UI组件库**：Ant Design Vue 4.2+
- **图标库**：@ant-design/icons-vue
- **状态管理**：Vue 3 Composition API (ref/reactive)
- **HTTP客户端**：原生 Fetch API
- **主题切换**：Ant Design ConfigProvider + CSS Variables

### 后端技术

- **框架**：Spring Boot 2.7.18
- **Java版本**：JDK 1.8+ (兼容性优化)
- **ORM框架**：MyBatis-Plus 3.5.3.1
- **数据库**：MySQL 5.7+
- **连接池**：Druid 1.2.18
- **权限认证**：Sa-Token 1.37.0
- **密码加密**：Spring Security BCrypt
- **工具类**：Hutool 5.8.22
- **JSON处理**：FastJSON2 2.0.43
- **自动初始化**：CommandLineRunner + SQL脚本自动执行

### 数据库

- **数据库**：MySQL 5.7+
- **字符集**：UTF8MB4
- **设计特点**：不使用外键约束，关联关系在应用层维护

## 📁 项目结构

```
zhangyu-permission-project/
├── permission-front/          # 前端项目
│   ├── src/
│   │   ├── components/        # Vue组件
│   │   │   ├── views/         # 页面组件
│   │   │   └── common/        # 公共组件
│   │   ├── utils/             # 工具类
│   │   ├── style/             # 样式文件
│   │   └── main.js            # 入口文件
│   ├── public/                # 静态资源
│   ├── package.json           # 依赖配置
│   └── vite.config.js         # Vite配置
│
├── permission-backend/        # 后端项目
│   ├── src/main/java/
│   │   └── com/zhangyu/permission/
│   │       ├── controller/    # 控制器层
│   │       ├── service/       # 服务层
│   │       ├── mapper/        # 数据访问层
│   │       ├── entity/        # 实体类
│   │       ├── dto/           # 数据传输对象
│   │       ├── vo/            # 视图对象
│   │       ├── config/        # 配置类
│   │       └── common/        # 公共类
│   ├── src/main/resources/
│   │   ├── sql/              # SQL脚本 (仅保留 init_database.sql)
│   │   │   ├── init_database.sql    # 完整数据库初始化脚本
│   │   │   └── README_数据库初始化.md  # 数据库文档
│   │   └── application.yml   # 配置文件
│   └── pom.xml               # Maven配置
```

## ✨ 功能模块

### 1. Dashboard 分析页（仅系统管理员）

- 📊 **应用数量统计**：统计系统中正常状态的应用数量
- 👥 **登录用户统计**：统计正常状态的登录用户数量
- 🌐 **在线用户统计**：实时统计当前在线用户数量
- 🔐 **权限过滤**：系统管理员查看所有数据，普通管理员查看授权数据

### 2. 管理员管理（仅系统管理员）

#### 2.1 管理员列表

- ✅ **CRUD操作**：创建、查询、修改、删除管理员
- 🔍 **多条件搜索**：支持按登录账户、姓名、状态筛选
- 📄 **分页显示**：支持分页查询和每页数量设置
- 🏷️ **管理员类型**：
  - 普通管理员（admin_type = 1）
  - 系统管理员（admin_type = 2）
- 🔗 **应用授权**：为管理员分配可访问的应用
- 🔒 **状态管理**：支持启用/禁用管理员账户

### 3. 应用管理

#### 3.1 应用管理

- ✅ **CRUD操作**：创建、查询、修改、删除应用
- 🔍 **多条件搜索**：支持按应用名称、应用Key、状态筛选
- 📄 **分页显示**：支持分页查询
- 🔒 **状态管理**：支持启用/禁用应用
- 🔐 **权限控制**：普通管理员只能看到授权的应用

#### 3.2 普通用户管理

- 👁️ **查看功能**：查看普通用户详细信息
- 🔍 **多条件搜索**：支持按工号、姓名、部门、状态筛选
- 📄 **分页显示**：支持分页查询
- 📋 **详细信息**：显示用户基本信息、部门信息等

#### 3.3 应用菜单管理

- ✅ **CRUD操作**：创建、查询、修改、删除应用菜单
- 🌳 **树形结构**：支持多级菜单，可展开/折叠
- 🏷️ **菜单类型**：
  - 目录（Directory）
  - 菜单（Menu）
  - 按钮（Button）
- 🔍 **多条件搜索**：支持按菜单名称、菜单Key、状态筛选
- 📋 **菜单属性**：
  - 上级菜单（树形选择）
  - 菜单类型（目录/菜单/按钮）
  - 菜单图标
  - 显示排序
  - 是否外链
  - 路由地址
  - 组件路径
  - 权限标识
  - 显示状态
  - 菜单状态
  - 内嵌URL
- 🔐 **权限控制**：普通管理员只能管理授权应用的菜单

#### 3.4 应用角色管理

- ✅ **CRUD操作**：创建、查询、修改、删除应用角色
- 🔍 **多条件搜索**：支持按系统ID、角色名称、状态筛选
- 📄 **分页显示**：支持分页查询
- 👥 **用户分配**：为角色分配用户（支持按用户和按部门两种方式）
- 📋 **角色属性**：
  - 系统ID（自动关联应用）
  - 所属应用
  - 角色名称
  - 显示顺序
  - 状态
- 🔐 **权限控制**：普通管理员只能管理授权应用的角色

#### 3.5 角色用户分配

- 👥 **用户分配**：
  - 按用户分配：从用户列表中选择用户加入角色
  - 按部门分配：从部门树中选择部门，部门下所有用户自动加入角色
- 📋 **用户列表**：显示已分配用户信息（应用系统、角色、工号、AD域账号、用户、手机、状态）
- 🏢 **部门列表**：显示已分配部门信息（应用系统、角色、部门名称、用户数量、创建时间）
- 🔍 **多条件搜索**：支持按AD域账号、工号、手机号筛选
- ✅ **批量操作**：支持批量取消授权
- 🔐 **权限过滤**：已分配的用户不会出现在可选列表中

### 4. 系统管理（仅系统管理员）

#### 4.1 系统角色管理

- ✅ **CRUD操作**：创建、查询、修改、删除系统角色
- 🔍 **多条件搜索**：支持按角色名称、状态筛选
- 📄 **分页显示**：支持分页查询
- 📋 **角色属性**：
  - 角色名称
  - 显示顺序
  - 状态

#### 4.2 系统菜单管理

- ✅ **CRUD操作**：创建、查询、修改、删除系统菜单
- 🔍 **多条件搜索**：支持按菜单名称、菜单ID、状态筛选
- 📄 **分页显示**：支持分页查询
- 📋 **菜单属性**：
  - 菜单名称
  - 菜单ID
  - 显示排序
  - 路由地址
  - 组件路径
  - 状态

## 🔐 权限控制

### 管理员类型

1. **系统管理员（admin_type = 2）**
   - 可以访问所有功能模块
   - 可以查看所有应用和数据
   - 不进行权限过滤

2. **普通管理员（admin_type = 1）**
   - 只能访问"应用管理"模块及其子功能
   - 只能查看授权的应用和数据
   - 进行权限过滤

### 菜单权限

- **系统管理员**：显示所有菜单（Dashboard、管理员管理、应用管理、系统管理）
- **普通管理员**：只显示"应用管理"菜单及其子菜单

### 数据权限

- **应用下拉框**：系统管理员显示所有应用，普通管理员只显示授权的应用
- **统计数据**：系统管理员统计所有数据，普通管理员统计授权数据
- **列表数据**：系统管理员查看所有数据，普通管理员查看授权数据

## 🚀 快速开始

### 环境要求

- **JDK**：1.8+
- **Node.js**：16+
- **MySQL**：5.7+
- **Maven**：3.6+

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE permission_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **自动初始化** (推荐)：
   - 项目启动时会自动创建所有表结构和初始化测试数据
   - 无需手动执行SQL脚本

3. **手动初始化** (可选)：
```bash
# 手动执行完整初始化脚本
mysql -u root -p permission_db < permission-backend/src/main/resources/sql/init_database.sql
```

### 后端启动

1. 修改数据库配置（`permission-backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/permission_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

2. 启动后端服务：
```bash
cd permission-backend
mvn clean compile  # 编译项目
mvn spring-boot:run  # 启动服务
```

**🚀 重要说明**：
- 首次启动时会自动创建所有数据库表和初始化测试数据
- 如需重新初始化，可删除数据库表后重启服务
- 自动初始化使用 `CREATE TABLE IF NOT EXISTS` 和 `INSERT IGNORE` 确保安全重复执行

后端服务默认运行在：`http://localhost:1105/one-permission`

### 前端启动

1. 安装依赖：
```bash
cd permission-front
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

**🔧 前端配置说明**：
- 自动代理 `/one-permission` 请求到后端服务
- 支持热重载开发
- 默认端口：`5173`

前端服务默认运行在：`http://localhost:5173`

### 默认账号

- **登录账户**：admin
- **密码**：123456
- **管理员类型**：系统管理员

### 🗄️ 自动初始化数据

项目启动时会自动创建以下数据：

- **管理员账户**：admin (系统管理员)
- **测试应用**：CRM系统、ERP系统
- **部门结构**：研发中心、产品中心、运营中心等6个顶级部门
- **普通用户**：55个测试用户（基于水浒传人物）
- **菜单结构**：完整的应用菜单树
- **角色配置**：应用角色及权限分配

**💡 提示**：所有初始化数据都可以通过管理界面进行修改和扩展。

## 📝 API 文档

### 认证相关

- `POST /one-permission/auth/login` - 用户登录
- `GET /one-permission/auth/current` - 获取当前用户信息
- `POST /one-permission/auth/logout` - 退出登录

### 管理员管理

- `GET /one-permission/login-users` - 分页查询管理员列表
- `GET /one-permission/login-users/{id}` - 查询管理员详情
- `POST /one-permission/login-users` - 创建管理员
- `PUT /one-permission/login-users/{id}` - 更新管理员
- `DELETE /one-permission/login-users/{id}` - 删除管理员
- `POST /one-permission/login-users/{id}/apps` - 分配应用

### 应用管理

- `GET /one-permission/applications` - 分页查询应用列表
- `GET /one-permission/applications/all` - 查询所有应用（下拉选择）
- `GET /one-permission/applications/{id}` - 查询应用详情
- `POST /one-permission/applications` - 创建应用
- `PUT /one-permission/applications/{id}` - 更新应用
- `DELETE /one-permission/applications/{id}` - 删除应用

### 应用菜单管理

- `GET /one-permission/applications/{appId}/menus/tree` - 查询菜单树
- `GET /one-permission/applications/{appId}/menus` - 分页查询菜单列表
- `GET /one-permission/applications/{appId}/menus/{id}` - 查询菜单详情
- `POST /one-permission/applications/{appId}/menus` - 创建菜单
- `PUT /one-permission/applications/{appId}/menus/{id}` - 更新菜单
- `DELETE /one-permission/applications/{appId}/menus/{id}` - 删除菜单

### 应用角色管理

- `GET /one-permission/applications/{appId}/roles` - 分页查询角色列表
- `GET /one-permission/applications/{appId}/roles/{id}` - 查询角色详情
- `POST /one-permission/applications/{appId}/roles` - 创建角色
- `PUT /one-permission/applications/{appId}/roles/{id}` - 更新角色
- `DELETE /one-permission/applications/{appId}/roles/{id}` - 删除角色

### 角色用户分配

- `GET /one-permission/applications/{appId}/roles/{roleId}/users` - 查询角色用户列表
- `POST /one-permission/applications/{appId}/roles/{roleId}/users` - 分配用户
- `DELETE /one-permission/applications/{appId}/roles/{roleId}/users/batch` - 批量取消授权
- `DELETE /one-permission/applications/{appId}/roles/{roleId}/users/{userId}` - 取消授权
- `GET /one-permission/applications/{appId}/roles/{roleId}/available-users` - 查询可选用户

### 角色部门分配

- `GET /one-permission/applications/{appId}/roles/{roleId}/departments` - 查询角色部门列表
- `POST /one-permission/applications/{appId}/roles/{roleId}/departments` - 分配部门
- `DELETE /one-permission/applications/{appId}/roles/{roleId}/departments/batch` - 批量取消授权
- `DELETE /one-permission/applications/{appId}/roles/{roleId}/departments/{departmentId}` - 取消授权

### 统计信息

- `GET /one-permission/statistics/dashboard` - 获取Dashboard统计数据

## 🎨 主题切换

系统支持5种主题：

- 🌞 **浅色主题**：默认浅色主题
- 🌙 **深色主题**：深色模式
- 💜 **紫色主题**：紫色渐变主题
- 💙 **蓝色主题**：蓝色渐变主题
- 💚 **绿色主题**：绿色渐变主题

主题设置会自动保存到本地存储，刷新页面后保持。

## 📦 构建部署

### 前端构建

```bash
cd permission-front
npm run build
```

构建产物在 `dist` 目录。

### 后端打包

```bash
cd permission-backend
mvn clean package
```

打包产物在 `target` 目录。

## 🔧 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 1105  # 服务端口
  servlet:
    context-path: /one-permission  # API上下文路径

spring:
  datasource:  # 数据库配置
    url: jdbc:mysql://localhost:3306/permission_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password

sa-token:  # Token认证配置
  token-name: satoken
  timeout: 7200  # 2小时有效期
  activity-timeout: -1  # 不启用活动超时
```

### 前端配置

- **API代理**：已配置 `/one-permission` 自动代理到后端
- **主题系统**：支持5种主题(浅色/深色/紫色/蓝色/绿色)
- **开发端口**：`5173`
- **生产构建**：`npm run build` 生成静态文件

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 👥 贡献者

- 张宇

## 📞 联系方式

- **项目地址**：[https://github.com/kiduo09/one-permission](https://github.com/kiduo09/one-permission)
- **问题反馈**：[提交 Issue](https://github.com/kiduo09/one-permission/issues)
- **Pull Request**：[提交 PR](https://github.com/kiduo09/one-permission/pulls)

如有问题或建议，请提交 Issue 或联系项目维护者。

---

**🎉 ONE-PERMISSION** - 开箱即用的企业级权限管理系统，让权限管理更简单、更高效！

**✨ 特色功能**：
- 🚀 一键启动，自动初始化
- 🎨 现代化UI，支持多主题
- 🔐 完整的权限管理体系
- 📊 实时数据统计分析

