<script setup>
import { ref, onMounted } from 'vue'
import Login from './components/Login.vue'
import Dashboard from './components/Dashboard.vue'
import { authApi } from './utils/api'
import { message } from 'ant-design-vue'

// 登录状态管理
const isLoggedIn = ref(false)
const loading = ref(true) // 初始加载状态

// 检查登录状态
const checkLoginStatus = async () => {
  const token = localStorage.getItem('token')
  
  if (!token) {
    // 没有token，显示登录页
    isLoggedIn.value = false
    loading.value = false
    return
  }
  
  try {
    // 尝试获取当前用户信息，验证token是否有效
    const response = await authApi.getCurrentUser()
    if (response && response.code === 200 && response.data) {
      // token有效，显示Dashboard
      isLoggedIn.value = true
    } else {
      // token无效，清除并显示登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      isLoggedIn.value = false
    }
  } catch (error) {
    // token无效或已过期，清除并显示登录页
    if (import.meta.env.DEV) {
      console.error('验证登录状态失败:', error)
    }
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    isLoggedIn.value = false
  } finally {
    loading.value = false
  }
}

// 页面加载时检查登录状态
onMounted(() => {
  checkLoginStatus()
})

// 登录成功
const handleLoginSuccess = () => {
  isLoggedIn.value = true
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
}
</script>

<template>
  <div v-if="loading" class="loading-container">
    <div class="loading-spinner"></div>
    <p>加载中...</p>
  </div>
  <template v-else>
    <Login v-if="!isLoggedIn" @login-success="handleLoginSuccess" />
    <Dashboard v-else @logout="handleLogout" />
  </template>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #f0f4f8 0%, #e0e7ff 100%);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-container p {
  margin-top: 16px;
  color: #64748b;
  font-size: 14px;
}
</style>
