<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="login-left">
      <div class="brand-content">
        <h1 class="brand-title">one-permission</h1>
        <p class="brand-subtitle">one-permission</p>
      </div>
      <div class="copyright">
        Copyright © 2018-2025 zhangyu.com All Rights Reserved 章鱼
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-right">
      <div class="login-form-container">
        <h2 class="login-title">管理员登录</h2>
        
        <form class="login-form" @submit.prevent="handleLogin">
          <!-- 用户名输入框 -->
          <div class="form-item">
            <label class="form-label">用户名</label>
            <div class="input-wrapper">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 12C14.7614 12 17 9.76142 17 7C17 4.23858 14.7614 2 12 2C9.23858 2 7 4.23858 7 7C7 9.76142 9.23858 12 12 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M20.59 22C20.59 18.13 16.74 15 12 15C7.26 15 3.41 18.13 3.41 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <input
                v-model="formData.loginAccount"
                type="text"
                class="form-input"
                placeholder="请输入登录账户"
                required
              />
            </div>
          </div>

          <!-- 密码输入框 -->
          <div class="form-item">
            <label class="form-label">密码</label>
            <div class="input-wrapper">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <input
                v-model="formData.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请输入密码"
                required
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
              >
                <svg v-if="showPassword" class="eye-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
                <svg v-else class="eye-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.94 17.94C16.2306 19.243 14.1491 19.9649 12 20C5 20 1 12 1 12C2.24389 9.68192 3.96914 7.65663 6.06 6.06M9.9 4.24C10.5883 4.0789 11.2931 3.99836 12 4C19 4 23 12 23 12C22.393 13.1356 21.6691 14.2048 20.84 15.19M14.12 14.12C13.8454 14.4148 13.5141 14.6512 13.1462 14.8151C12.7782 14.9791 12.3809 15.0673 11.98 15.0744C11.5771 15.0815 11.1777 15.0074 10.8056 14.8565C10.4335 14.7056 10.0962 14.4811 9.81282 14.1977C9.52943 13.9143 9.30493 13.577 9.15403 13.2049C9.00312 12.8328 8.92906 12.4334 8.93615 12.0305C8.94324 11.6276 9.03144 11.2303 9.19541 10.8623C9.35938 10.4944 9.59579 10.1631 9.89063 9.88843" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M1 1L23 23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>

          <!-- 记住密码 -->
          <div class="remember-password">
            <label class="remember-checkbox">
              <input 
                type="checkbox" 
                v-model="rememberPassword"
              />
              <span>记住密码</span>
            </label>
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <!-- 登录按钮 -->
          <button type="submit" class="login-button" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { authApi } from '../utils/api'
import { message } from 'ant-design-vue'
import { saveRememberedLogin, getRememberedLogin, clearRememberedLogin } from '../utils/storage'

const emit = defineEmits(['login-success'])

const formData = ref({
  loginAccount: '',
  password: ''
})

const showPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const rememberPassword = ref(false)

// 页面加载时，尝试读取记住的登录信息
onMounted(() => {
  const remembered = getRememberedLogin()
  if (remembered) {
    formData.value.loginAccount = remembered.loginAccount
    formData.value.password = remembered.password
    rememberPassword.value = true
  }
})

const handleLogin = async () => {
  // 重置错误信息
  errorMessage.value = ''
  
  // 基本验证
  if (!formData.value.loginAccount || !formData.value.password) {
    errorMessage.value = '请输入登录账户和密码'
    return
  }
  
  loading.value = true
  
  try {
    // 调用登录接口
    const response = await authApi.login(formData.value.loginAccount, formData.value.password)
    
    if (response.code === 200 && response.data) {
      // 保存token到localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token)
      }
      
      // 保存用户信息到localStorage
      if (response.data.userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo))
      }
      
      // 如果勾选了记住密码，保存登录信息
      if (rememberPassword.value) {
        saveRememberedLogin(formData.value.loginAccount, formData.value.password)
      } else {
        // 如果没有勾选记住密码，清除之前保存的信息
        clearRememberedLogin()
      }
      
      // 登录成功，触发事件
      emit('login-success')
    } else {
      errorMessage.value = response.message || '登录失败，请重试'
      message.error(response.message || '登录失败，请重试')
    }
  } catch (err) {
    if (import.meta.env.DEV) {
      console.error('登录错误:', err)
    }
    errorMessage.value = err.message || '登录失败，请检查网络连接'
    message.error(err.message || '登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* 左侧品牌区域 */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 2rem;
}

.brand-content {
  text-align: center;
  color: white;
}

.brand-title {
  font-size: 2.5rem;
  font-weight: 600;
  margin: 0 0 1rem 0;
  color: white;
}

.brand-subtitle {
  font-size: 1.5rem;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
}

.copyright {
  position: absolute;
  bottom: 2rem;
  left: 2rem;
  color: white;
  font-size: 0.875rem;
}

/* 右侧登录表单区域 */
.login-right {
  flex: 1;
  background: white;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.login-form-container {
  width: 100%;
  max-width: 400px;
}

.login-title {
  font-size: 1.875rem;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
  margin: 0 0 2.5rem 0;
}

.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 1rem;
  width: 1.25rem;
  height: 1.25rem;
  color: #94a3b8;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 3rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.875rem;
  color: #1e293b;
  background: white;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input::placeholder {
  color: #94a3b8;
}

.password-toggle {
  position: absolute;
  right: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.eye-icon {
  width: 1.25rem;
  height: 1.25rem;
  color: #94a3b8;
  transition: color 0.3s;
}

.password-toggle:hover .eye-icon {
  color: #667eea;
}

.login-button {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 0.5rem;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.2);
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.remember-password {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
}

.remember-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 0.875rem;
  color: #64748b;
  user-select: none;
}

.remember-checkbox input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin-right: 0.5rem;
  cursor: pointer;
  accent-color: #667eea;
}

.remember-checkbox span {
  cursor: pointer;
}

.error-message {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
  text-align: center;
  padding: 0.5rem;
  background: #fef2f2;
  border-radius: 6px;
  border: 1px solid #fecaca;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-left {
    min-height: 40vh;
  }

  .brand-title {
    font-size: 1.75rem;
  }

  .brand-subtitle {
    font-size: 1.25rem;
  }

  .copyright {
    position: relative;
    bottom: auto;
    left: auto;
    margin-top: 2rem;
    text-align: center;
  }
}
</style>

