<template>
  <a-config-provider :theme="themeConfig">
    <div class="layout" :data-theme="currentTheme">
      <header class="topbar">
      <div class="brand">
        <span class="brand-icon">🐙</span>
        <span class="brand-title">one-permission</span>
      </div>
      <div class="top-actions">
        <div class="top-info">
          <span class="page-title">{{ currentPageTitle }}</span>
          <span class="page-sub">{{ currentPageSubtitle }}</span>
        </div>
        <div class="top-user">
          <!-- GitHub Stars 按钮 -->
          <a href="https://github.com/kiduo09/one-permission" target="_blank" class="github-stars-btn" title="在 GitHub 上关注项目">
            <svg class="github-icon" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
            <span class="stars-count">{{ githubStars }}</span>
            <span class="stars-label">Star</span>
          </a>
          <!-- 主题切换按钮 -->
          <a-dropdown :trigger="['click']" placement="bottomRight">
            <button class="theme-toggle-btn" @click.prevent>
              <svg class="theme-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="5" stroke="currentColor" stroke-width="2"/>
                <path d="M12 1v3M12 20v3M4.22 4.22l2.12 2.12M17.66 17.66l2.12 2.12M1 12h3M20 12h3M4.22 19.78l2.12-2.12M17.66 6.34l2.12-2.12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
            <template #overlay>
              <a-menu @click="handleThemeChange" :selected-keys="[currentTheme]">
                <a-menu-item key="light">
                  <span>🌞 浅色主题</span>
                </a-menu-item>
                <a-menu-item key="dark">
                  <span>🌙 深色主题</span>
                </a-menu-item>
                <a-menu-item key="purple">
                  <span>💜 紫色主题</span>
                </a-menu-item>
                <a-menu-item key="blue">
                  <span>💙 蓝色主题</span>
                </a-menu-item>
                <a-menu-item key="green">
                  <span>💚 绿色主题</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
          <svg class="bell-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <!-- 用户头像下拉菜单 -->
          <a-dropdown :trigger="['click']" placement="bottomRight">
            <div class="user-avatar-dropdown">
              <div class="user-avatar">{{ currentUserAvatar }}</div>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="handleLogout">
                  <svg class="logout-icon-small" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M16 17L21 12L16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>
    </header>

    <div class="body">
      <aside class="sider">
        <a-menu
          mode="inline"
          :selected-keys="selectedMenuKeys"
          :open-keys="openKeys"
          @openChange="handleOpenChange"
          :style="{ border: 'none', background: 'transparent' }"
          @click="handleMenuClick"
        >
          <!-- 首页 -->
          <a-menu-item key="home">
            <template #icon>
              <HomeOutlined />
            </template>
            <span>首页</span>
          </a-menu-item>

          <!-- Dashboard - 仅系统管理员可见 -->
          <a-sub-menu v-if="isSystemAdmin" key="dashboard">
            <template #icon>
              <DashboardOutlined />
            </template>
            <template #title>Dashboard</template>
            <a-menu-item key="dashboard-analysis">
              <span>分析页</span>
            </a-menu-item>
          </a-sub-menu>

          <!-- 管理员管理 - 仅系统管理员可见 -->
          <a-sub-menu v-if="isSystemAdmin" key="adminManagement">
            <template #icon>
              <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </template>
            <template #title>管理员管理</template>
            <a-menu-item key="loginUsers">
              <span>管理员列表</span>
            </a-menu-item>
            <a-menu-item key="consumerManagement">
              <span>消费者管理</span>
            </a-menu-item>
          </a-sub-menu>

          <!-- 应用管理 - 所有管理员可见 -->
          <a-sub-menu key="appManagement">
            <template #icon>
              <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2"/>
                <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2"/>
                <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2"/>
                <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2"/>
              </svg>
            </template>
            <template #title>应用管理</template>
            <a-menu-item key="applications">
              <span>应用管理</span>
            </a-menu-item>
            <a-menu-item key="normalUsers">
              <span>普通用户管理</span>
            </a-menu-item>
            <a-menu-item key="appMenus">
              <span>应用菜单管理</span>
            </a-menu-item>
            <a-menu-item key="appRoles">
              <span>应用角色管理</span>
            </a-menu-item>
          </a-sub-menu>

          <!-- 系统管理 - 仅系统管理员可见 -->
          <a-sub-menu v-if="isSystemAdmin" key="systemManagement">
            <template #icon>
              <svg class="menu-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </template>
            <template #title>系统管理</template>
            <a-menu-item key="systemRoles">
              <span>系统角色管理</span>
            </a-menu-item>
            <a-menu-item key="systemMenus">
              <span>系统菜单管理</span>
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </aside>

      <main class="content">
        <!-- 面包屑导航 -->
        <a-breadcrumb v-if="activeTab" style="margin-bottom: 12px;">
          <a-breadcrumb-item>
            <span class="breadcrumb-link" @click="goHome">首页</span>
          </a-breadcrumb-item>
          <a-breadcrumb-item v-if="activeTab">
            <span>{{ activeTab.group }}</span>
          </a-breadcrumb-item>
          <a-breadcrumb-item>
            <span>{{ currentPageTitle }}</span>
          </a-breadcrumb-item>
        </a-breadcrumb>

        <!-- Tab导航 -->
        <a-tabs
          v-if="tabs.length > 0"
          v-model:activeKey="activeTabId"
          type="editable-card"
          hide-add
          @edit="handleTabEdit"
          @change="switchTab"
          style="margin-bottom: 0;"
          class="page-tabs"
        >
          <a-tab-pane
            v-for="tab in tabs"
            :key="tab.id"
            :tab="tab.title"
            :closable="true"
          >
          </a-tab-pane>
        </a-tabs>

        <!-- Tab内容 -->
        <template v-if="activeTab">
          <component 
            :is="activeTabComponent" 
            v-bind="activeTab.props"
            @open-role-users="handleOpenRoleUsers"
            @close="handleComponentClose"
          />
        </template>
        <template v-else>
          <div class="welcome-screen">
            <div class="welcome-content">
              <div class="welcome-avatar">🐙</div>
              <h2>欢迎使用one-permission</h2>
              <p>请从左侧菜单选择功能模块，或点击 <strong>首页</strong> 查看接口调用说明文档</p>
            </div>
          </div>
        </template>
      </main>
    </div>

    <!-- 退出登录确认弹窗 -->
    <a-modal
      v-model:open="showLogoutConfirm"
      title="确认退出"
      ok-text="确定"
      cancel-text="取消"
      @ok="confirmLogout"
      @cancel="cancelLogout"
    >
      <p>确定要退出登录吗？</p>
    </a-modal>
    </div>
  </a-config-provider>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { theme, message } from 'ant-design-vue'
import { DashboardOutlined, HomeOutlined } from '@ant-design/icons-vue'
import LoginUsersView from './views/LoginUsersView.vue'
import ConsumerManagementView from './views/ConsumerManagementView.vue'
import ApplicationsView from './views/ApplicationsView.vue'
import NormalUsersView from './views/NormalUsersView.vue'
import AppMenusView from './views/AppMenusView.vue'
import AppRolesView from './views/AppRolesView.vue'
import SystemRolesView from './views/SystemRolesView.vue'
import SystemMenusView from './views/SystemMenusView.vue'
import RoleUsersView from './views/RoleUsersView.vue'
import DashboardView from './views/DashboardView.vue'
import ApiDocumentationView from './views/ApiDocumentationView.vue'
import { getCurrentTheme, setTheme, themes, updateCheckboxIndeterminateTheme } from '@/utils/theme'

const emit = defineEmits(['logout'])

// 主题管理
const currentTheme = ref(getCurrentTheme())
const themeConfig = computed(() => {
  const themeInfo = themes[currentTheme.value]
  return {
    algorithm: currentTheme.value === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      colorPrimary: themeInfo.primaryColor
    }
  }
})

const handleThemeChange = ({ key }) => {
  currentTheme.value = key
  setTheme(key)
  updateCheckboxIndeterminateTheme()
}

// GitHub Stars 数量
const githubStars = ref(0)

// 加载 GitHub Stars 数量
const loadGitHubStars = async () => {
  try {
    const response = await fetch('https://api.github.com/repos/kiduo09/one-permission')
    if (response.ok) {
      const data = await response.json()
      githubStars.value = data.stargazers_count || 0
    }
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error('获取 GitHub Stars 失败:', error)
    }
  }
}

// 监听主题变更事件和加载用户信息
onMounted(() => {
  window.addEventListener('theme-change', (event) => {
    currentTheme.value = event.detail.theme
  })
  loadCurrentUser()
  loadGitHubStars()
})

// Tab管理
const tabs = ref([])
const activeTabId = ref(null)

// 菜单展开状态
const openKeys = ref([])

const handleOpenChange = (keys) => {
  openKeys.value = keys
}

// 组件映射
const componentMap = {
  'home': ApiDocumentationView,
  'dashboard-analysis': DashboardView,
  loginUsers: LoginUsersView,
  consumerManagement: ConsumerManagementView,
  normalUsers: NormalUsersView,
  applications: ApplicationsView,
  appMenus: AppMenusView,
  appRoles: AppRolesView,
  systemRoles: SystemRolesView,
  systemMenus: SystemMenusView,
  roleUsers: RoleUsersView
}

// 页面标题和分组映射
const pageConfig = {
  'home': { title: '获取用户应用菜单权限接口调用说明文档', group: '首页' },
  'dashboard-analysis': { title: '分析页', group: 'Dashboard' },
  loginUsers: { title: '管理员列表', group: '管理员管理' },
  consumerManagement: { title: '消费者管理', group: '管理员管理' },
  normalUsers: { title: '普通用户管理', group: '应用管理' },
  applications: { title: '应用管理', group: '应用管理' },
  appMenus: { title: '应用菜单管理', group: '应用管理' },
  appRoles: { title: '应用角色管理', group: '应用管理' },
  systemRoles: { title: '系统角色管理', group: '系统管理' },
  systemMenus: { title: '系统菜单管理', group: '系统管理' },
  roleUsers: { title: '分配用户', group: '应用管理' }
}

const activeTab = computed(() => {
  return tabs.value.find(t => t.id === activeTabId.value)
})

const activeTabComponent = computed(() => {
  if (activeTab.value?.type) {
    return componentMap[activeTab.value.type] || null
  }
  return null
})

const isTabActive = (type) => {
  return activeTab.value?.type === type
}

// 菜单选中状态
const selectedMenuKeys = computed(() => {
  if (activeTab.value?.type) {
    return [activeTab.value.type]
  }
  return []
})

// 菜单点击处理
const handleMenuClick = ({ key }) => {
  openTab(key)
}

const currentPageTitle = computed(() => {
  if (activeTab.value) {
    return activeTab.value.title
  }
  return '首页'
})

const currentPageSubtitle = computed(() => {
  return ''
})

const openTab = (type) => {
  // 权限检查：普通管理员只能访问应用管理相关页面和首页
  if (!isSystemAdmin.value) {
    const allowedTypes = ['home', 'applications', 'normalUsers', 'appMenus', 'appRoles']
    if (!allowedTypes.includes(type)) {
      message.warning('您没有权限访问此页面')
      return
    }
  }
  
  // 检查是否已存在相同的tab
  const existingTab = tabs.value.find(t => t.type === type)
  
  if (existingTab) {
    // 如果已存在，切换到该tab
    switchTab(existingTab.id)
  } else {
    // 创建新tab
    const config = pageConfig[type]
    const newTab = {
      id: `tab-${type}-${Date.now()}`,
      type: type,
      title: config.title,
      group: config.group,
      props: {}
    }
    tabs.value.push(newTab)
    activeTabId.value = newTab.id
  }
}

const handleOpenRoleUsers = (data) => {
  // 检查是否已存在相同的tab
  const existingTab = tabs.value.find(t => 
    t.type === 'roleUsers' && 
    t.props.roleId === data.roleId &&
    t.props.appId === data.appId
  )
  
  if (existingTab) {
    // 如果已存在，切换到该tab
    switchTab(existingTab.id)
  } else {
    // 创建新tab
    const newTab = {
      id: `tab-roleUsers-${Date.now()}`,
      type: 'roleUsers',
      title: `分配用户 - ${data.roleName}`,
      group: '应用管理',
      props: {
        appId: data.appId,
        roleId: data.roleId,
        roleName: data.roleName,
        appName: data.appName
      }
    }
    tabs.value.push(newTab)
    activeTabId.value = newTab.id
  }
}

const switchTab = (tabId) => {
  activeTabId.value = tabId
}

const handleTabEdit = (targetKey, action) => {
  if (action === 'remove') {
    closeTab(targetKey)
  }
}

const closeTab = (tabId) => {
  const index = tabs.value.findIndex(t => t.id === tabId)
  if (index > -1) {
    tabs.value.splice(index, 1)
    
    // 如果关闭的是当前激活的tab
    if (activeTabId.value === tabId) {
      if (tabs.value.length > 0) {
        // 切换到最后一个tab
        activeTabId.value = tabs.value[tabs.value.length - 1].id
      } else {
        // 没有tab了，清空激活状态
        activeTabId.value = null
      }
    }
  }
}

const goHome = () => {
  activeTabId.value = null
}

// 处理组件关闭事件
const handleComponentClose = () => {
  if (activeTabId.value) {
    closeTab(activeTabId.value)
  }
}

// 当前用户信息
const currentUser = ref({
  name: '管理员',
  email: '',
  adminType: 1
})

const currentUserAvatar = computed(() => {
  if (currentUser.value.name) {
    return currentUser.value.name.charAt(0)
  }
  return '管'
})

// 判断是否为系统管理员
const isSystemAdmin = computed(() => {
  return currentUser.value.adminType === 2
})

// 加载当前用户信息
const loadCurrentUser = async () => {
  try {
    const { authApi } = await import('../utils/api')
    const response = await authApi.getCurrentUser()
    if (response && response.data) {
      currentUser.value = response.data
    }
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error('获取用户信息失败:', error)
    }
  }
}


// 退出登录确认弹窗
const showLogoutConfirm = ref(false)

const handleLogout = async () => {
  showLogoutConfirm.value = true
}

const confirmLogout = async () => {
  try {
    // 调用后端退出接口
    const { authApi } = await import('../utils/api')
    await authApi.logout()
  } catch (error) {
    if (import.meta.env.DEV) {
      console.error('退出登录失败:', error)
    }
    message.error('退出登录失败，请稍后重试')
  } finally {
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 清除所有Tab
    tabs.value = []
    activeTabId.value = null
    // 触发退出事件
    emit('logout')
    showLogoutConfirm.value = false
  }
}

const cancelLogout = () => {
  showLogoutConfirm.value = false
}
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #f0f4f8 0%, #e0e7ff 100%);
  color: #1e293b;
}

.topbar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 面包屑样式优化 */
.breadcrumb-link {
  color: #667eea;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* Tab样式优化 */
:deep(.ant-tabs) {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 12px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 0;
}

:deep(.page-tabs) {
  margin-bottom: 0 !important;
}

:deep(.ant-tabs-nav) {
  margin: 0;
}

:deep(.ant-tabs-tab) {
  padding: 8px 16px;
  margin-right: 4px;
  border: 1px solid transparent;
  border-radius: 6px 6px 0 0;
  transition: all 0.2s;
}

:deep(.ant-tabs-tab:hover) {
  color: #667eea;
}

:deep(.ant-tabs-tab-active) {
  border-color: #e5e7eb;
  border-bottom-color: #ffffff;
  background: #ffffff;
}

:deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: #667eea;
  font-weight: 500;
}

/* 只隐藏页面级别的 tabs 内容，因为内容通过 component 动态渲染 */
.page-tabs :deep(.ant-tabs-content-holder) {
  display: none;
}

:deep(.ant-tabs-tab-remove) {
  margin-left: 8px;
  color: #94a3b8;
  transition: color 0.2s;
}

:deep(.ant-tabs-tab-remove:hover) {
  color: #ef4444;
}

.welcome-screen {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.welcome-content {
  text-align: center;
  color: #64748b;
}

.welcome-avatar {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
  border: 2px solid rgba(102, 126, 234, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  transition: all 0.3s ease;
}

.welcome-avatar:hover {
  transform: scale(1.05);
  border-color: rgba(102, 126, 234, 0.5);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.welcome-content h2 {
  font-size: 20px;
  margin-bottom: 12px;
  color: #1e293b;
  font-weight: 600;
}

.welcome-content p {
  font-size: 14px;
  color: #64748b;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #1e293b;
}

.brand-icon {
  font-size: 20px;
}

.brand-title {
  font-size: 16px;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.top-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
}

.page-title {
  font-weight: 600;
  color: #1e293b;
}

.page-sub {
  font-size: 13px;
  color: #64748b;
}

.top-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.github-stars-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: 1px solid var(--border-color, #e5e7eb);
  border-radius: 8px;
  padding: 6px 12px;
  cursor: pointer;
  text-decoration: none;
  color: var(--text-secondary, #64748b);
  transition: all 0.2s;
  font-size: 13px;
}

.github-stars-btn:hover {
  background: var(--bg-secondary, #f1f5f9);
  border-color: var(--border-color, #cbd5e1);
  color: var(--text-primary, #1e293b);
}

.github-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.stars-count {
  font-weight: 600;
  color: inherit;
}

.stars-label {
  color: inherit;
}

.theme-toggle-btn {
  background: transparent;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  color: #64748b;
}

.theme-toggle-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #1e293b;
}

.theme-icon {
  width: 18px;
  height: 18px;
}

.bell-icon {
  width: 20px;
  height: 20px;
  color: #64748b;
  cursor: pointer;
  transition: color 0.2s;
}

.bell-icon:hover {
  color: #1e293b;
}

.user-avatar-dropdown {
  cursor: pointer;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: grid;
  place-items: center;
  color: #fff;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: transform 0.2s;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.logout-icon-small {
  width: 14px;
  height: 14px;
  margin-right: 8px;
  vertical-align: middle;
}

.body {
  display: flex;
  flex: 1;
  min-height: 0;
}

.sider {
  width: 208px;
  background: #fff;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.menu-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: currentColor;
}

/* Ant Design Menu 样式覆盖 - Ant Design Pro 风格 */
.sider :deep(.ant-menu) {
  background: #fff;
  border-right: none;
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-item),
.sider :deep(.ant-menu-submenu-title) {
  margin: 0;
  height: 40px;
  line-height: 40px;
  padding: 0 16px !important;
  border-radius: 0;
  display: flex;
  align-items: center;
  color: rgba(0, 0, 0, 0.85);
  transition: all 0.3s;
}

.sider :deep(.ant-menu-item:hover),
.sider :deep(.ant-menu-submenu-title:hover) {
  background: #f5f5f5;
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-item-selected) {
  background: #e6f7ff !important;
  color: #1890ff !important;
  font-weight: 500;
  position: relative;
  border-radius: 0;
}

.sider :deep(.ant-menu-item-selected::after) {
  display: none;
}

.sider :deep(.ant-menu-item-icon) {
  margin-right: 12px;
  display: flex;
  align-items: center;
  font-size: 16px;
  min-width: 16px;
}

.sider :deep(.ant-menu-submenu-title) {
  padding-left: 16px !important;
}

.sider :deep(.ant-menu-submenu-selected > .ant-menu-submenu-title) {
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-submenu-arrow) {
  color: rgba(0, 0, 0, 0.45);
  right: 16px;
}

.sider :deep(.ant-menu-submenu-title:hover .ant-menu-submenu-arrow) {
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-submenu-selected > .ant-menu-submenu-title .ant-menu-submenu-arrow) {
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-sub) {
  background: #fafafa;
}

.sider :deep(.ant-menu-sub .ant-menu-item) {
  padding-left: 48px !important;
  height: 40px;
  line-height: 40px;
  margin: 0;
  background: transparent;
  border-radius: 0;
}

.sider :deep(.ant-menu-sub .ant-menu-item:hover) {
  background: #f5f5f5;
  color: rgba(0, 0, 0, 0.85);
}

.sider :deep(.ant-menu-sub .ant-menu-item-selected) {
  background: #e6f7ff !important;
  color: #1890ff !important;
  border-radius: 0;
  position: relative;
  margin-right: 0;
}

.sider :deep(.ant-menu-sub .ant-menu-item-selected::after) {
  display: none;
}

.sider :deep(.ant-menu-sub .ant-menu-item-icon) {
  margin-right: 8px;
}


.content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: auto;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  background: #101c31;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 10px;
}

.search {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #0c1527;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  min-width: 320px;
  transition: border-color 0.2s;
}

.search:focus-within {
  border-color: rgba(45, 140, 240, 0.3);
}

.search-icon {
  width: 16px;
  height: 16px;
  color: #6f829f;
  flex-shrink: 0;
}

.search input {
  background: transparent;
  border: none;
  outline: none;
  color: #e5edff;
  width: 100%;
  font-size: 13px;
}

.search input:focus {
  color: #e5edff;
}

.search input::placeholder {
  color: #6f829f;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

select {
  background: #0c1527;
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: #e5edff;
  padding: 10px 12px;
  border-radius: 8px;
  outline: none;
  cursor: pointer;
  transition: border-color 0.2s;
  font-size: 13px;
}

select:hover {
  border-color: rgba(255, 255, 255, 0.1);
}

select:focus {
  border-color: rgba(45, 140, 240, 0.3);
}

button {
  cursor: pointer;
}

.reset {
  background: #16243a;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #e5edff;
  padding: 10px 14px;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 13px;
}

.reset:hover {
  background: #1a2d47;
  border-color: rgba(255, 255, 255, 0.12);
}

.primary {
  background: #2d8cf0;
  border: none;
  color: #fff;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.2s;
  font-size: 13px;
  font-weight: 500;
}

.primary:hover {
  background: #1d7ad8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(45, 140, 240, 0.3);
}

.primary:active {
  transform: translateY(0);
}

.table-card {
  background: #0f1a2c;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  overflow: hidden;
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns: 0.6fr 1fr 1.2fr 1.5fr 1.8fr 0.8fr 1.2fr 1.5fr 1fr;
  align-items: center;
  padding: 16px 20px;
}

.table-head {
  background: #121e33;
  color: #9fb3d1;
  font-size: 13px;
  font-weight: 500;
}

.table-row {
  border-top: 1px solid rgba(255, 255, 255, 0.03);
  transition: background 0.2s;
}

.table-row:hover {
  background: rgba(255, 255, 255, 0.02);
}

.td,
.th {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.td {
  color: #d7e1f2;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #e5edff;
  font-size: 14px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  margin-right: 6px;
  font-weight: 500;
  white-space: nowrap;
}

.status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 46px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.status.ok {
  background: #1fa84d;
  color: #fff;
}

.status.disabled {
  background: #555b6b;
  color: #e5edff;
}

.w-action {
  justify-content: flex-start;
  align-items: center;
  gap: 4px;
}

.action-btn {
  background: none;
  border: none;
  color: #4aa3ff;
  cursor: pointer;
  padding: 0;
  font-size: 13px;
  line-height: 1;
}

.action-btn:hover {
  color: #6bb5ff;
  text-decoration: underline;
}

.action-btn.danger {
  color: #ff6b6b;
}

.action-btn.danger:hover {
  color: #ff6b6b;
  text-decoration: underline;
}

.divider {
  color: #4a5568;
  font-size: 12px;
  margin: 0 2px;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #9fb3d1;
  font-size: 13px;
  padding: 4px 2px 0;
}

.pager {
  display: flex;
  gap: 6px;
}

.pager-btn {
  background: #121e33;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #e5edff;
  padding: 6px 12px;
  border-radius: 6px;
  min-width: 36px;
  transition: all 0.2s;
  font-size: 13px;
}

.pager-btn:hover:not(:disabled):not(.active) {
  background: #16243a;
  border-color: rgba(255, 255, 255, 0.12);
}

.pager-btn.active {
  background: #2d8cf0;
  border-color: #2d8cf0;
  color: #fff;
}

.pager-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: grid;
  place-items: center;
  z-index: 1000;
  padding: 16px;
}

.modal-content {
  background: #0f1a2c;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  width: 480px;
  max-width: 90vw;
  color: #e5edff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

.modal-content.small {
  width: 420px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.modal-title {
  font-weight: 600;
}

.close {
  background: none;
  border: none;
  color: #9fb3d1;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-row label {
  font-size: 13px;
  color: #9fb3d1;
}

.required {
  color: #ff6b6b;
  margin-left: 2px;
}

.form-row input,
.form-row select,
.form-row textarea {
  background: #0c1527;
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: #e5edff;
  padding: 10px 12px;
  border-radius: 8px;
  outline: none;
  font-family: inherit;
  resize: vertical;
}

.form-row textarea {
  min-height: 60px;
}

.form-row input:focus,
.form-row select:focus,
.form-row textarea:focus {
  border-color: rgba(45, 140, 240, 0.3);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 16px 16px;
}

.ghost {
  background: #16243a;
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #e5edff;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.danger {
  background: transparent;
  border: none;
  color: #ff6b6b;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.detail .label {
  color: #9fb3d1;
  margin-right: 6px;
}

@media (max-width: 1100px) {
  .layout {
    flex-direction: column;
  }

  .body {
    flex-direction: column;
  }

  .sider {
    width: 100%;
    flex-direction: row;
    align-items: center;
    gap: 10px;
  }

  .sider-footer {
    display: none;
  }
}
</style>

