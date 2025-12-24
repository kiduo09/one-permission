# ONE-PERMISSION 前端项目

基于 Vue 3 + Vite 的现代化前端应用，为权限管理系统提供用户界面。

## 🛠️ 技术栈

- **框架**: Vue 3.2+ (Composition API)
- **构建工具**: Vite 4.0+
- **UI组件库**: Ant Design Vue 4.2+
- **图标库**: @ant-design/icons-vue
- **状态管理**: Vue 3 Composition API (ref/reactive)
- **HTTP客户端**: 原生 Fetch API
- **主题切换**: Ant Design ConfigProvider + CSS Variables

## 📁 项目结构

```
permission-front/
├── src/
│   ├── components/           # Vue组件
│   │   ├── views/           # 页面组件
│   │   │   ├── LoginUsersView.vue     # 管理员管理
│   │   │   ├── ApplicationsView.vue   # 应用管理
│   │   │   ├── NormalUsersView.vue    # 普通用户管理
│   │   │   ├── AppMenusView.vue       # 应用菜单管理
│   │   │   ├── AppRolesView.vue       # 应用角色管理
│   │   │   ├── RoleUsersView.vue      # 角色用户分配
│   │   │   ├── DashboardView.vue      # 数据统计页面
│   │   │   └── ApiDocumentationView.vue # API文档页面
│   │   └── common/          # 公共组件
│   │       ├── Message.vue            # 消息提示组件
│   │       └── MessageSimple.vue      # 简单消息组件
│   ├── utils/               # 工具类
│   │   ├── api.js           # API请求封装
│   │   ├── theme.js         # 主题切换工具
│   │   ├── message.js       # 消息提示工具
│   │   └── storage.js       # 本地存储工具
│   ├── style/               # 样式文件
│   │   ├── theme.css        # 主题样式
│   │   └── message.css      # 消息样式
│   ├── App.vue              # 根组件
│   ├── main.js              # 入口文件
│   └── style.css            # 全局样式
├── public/                  # 静态资源
├── package.json             # 依赖配置
├── vite.config.js           # Vite配置
└── README.md                # 项目说明
```

## 🚀 快速开始

### 环境要求

- Node.js 16+
- npm 或 yarn

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发环境运行

```bash
npm run dev
# 或
yarn dev
```

服务将在 `http://localhost:5173` 启动

### 生产环境构建

```bash
npm run build
# 或
yarn build
```

构建产物在 `dist` 目录

### 预览构建结果

```bash
npm run preview
# 或
yarn preview
```

## ⚙️ 配置说明

### API配置

API基础路径在 `src/utils/api.js` 中配置：

```javascript
const API_BASE_URL = '/one-permission';
```

### 代理配置

Vite代理配置在 `vite.config.js` 中：

```javascript
export default {
  server: {
    proxy: {
      '/one-permission': {
        target: 'http://localhost:1105',
        changeOrigin: true
      }
    }
  }
}
```

### 主题配置

主题相关配置在 `src/utils/theme.js` 中，支持5种主题：
- 浅色主题 (默认)
- 深色主题
- 紫色主题
- 蓝色主题
- 绿色主题

## 🎨 界面特性

- **响应式设计**: 支持各种屏幕尺寸
- **现代化UI**: 基于 Ant Design Vue
- **主题切换**: 支持5种主题实时切换
- **国际化**: 支持中英文切换 (预留接口)
- **权限控制**: 基于后端权限动态显示菜单

## 🔧 开发指南

### 组件开发

- 使用 Vue 3 Composition API
- 遵循 `<script setup>` 语法
- 使用 TypeScript 进行类型检查 (可选)

### API调用

```javascript
import { loginApi, userApi } from '@/utils/api';

// 登录
const login = async (credentials) => {
  try {
    const response = await loginApi.login(credentials);
    return response;
  } catch (error) {
    console.error('Login failed:', error);
  }
};
```

### 主题切换

```javascript
import { setTheme, getCurrentTheme } from '@/utils/theme';

// 设置主题
setTheme('dark');

// 获取当前主题
const currentTheme = getCurrentTheme();
```

## 📦 依赖说明

### 核心依赖

- `vue`: Vue 3 框架
- `ant-design-vue`: UI组件库
- `@ant-design/icons-vue`: 图标库

### 开发依赖

- `@vitejs/plugin-vue`: Vite Vue插件
- `vite`: 构建工具
- `eslint`: 代码检查

## 🔗 相关链接

- [Vue 3 官方文档](https://v3.vuejs.org/)
- [Vite 官方文档](https://vitejs.dev/)
- [Ant Design Vue 文档](https://www.antdv.com/)
- [后端项目](../permission-backend/README.md)
- [项目总述](../README.md)

## 👥 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📞 联系方式

- 项目地址: [https://github.com/kiduo09/one-permission](https://github.com/kiduo09/one-permission)
