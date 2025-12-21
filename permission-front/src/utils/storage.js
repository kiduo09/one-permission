/**
 * 本地存储工具类
 * 用于安全地存储和读取敏感信息（如密码）
 */

const STORAGE_KEY_PREFIX = 'one_permission_'

/**
 * 简单的Base64编码（仅用于基本混淆，不是真正的加密）
 */
function encode(str) {
  try {
    return btoa(unescape(encodeURIComponent(str)))
  } catch (e) {
    console.error('编码失败:', e)
    return str
  }
}

/**
 * Base64解码
 */
function decode(str) {
  try {
    return decodeURIComponent(escape(atob(str)))
  } catch (e) {
    console.error('解码失败:', e)
    return str
  }
}

/**
 * 保存记住的登录信息
 */
export function saveRememberedLogin(loginAccount, password) {
  try {
    const data = {
      loginAccount: encode(loginAccount),
      password: encode(password),
      timestamp: Date.now()
    }
    localStorage.setItem(`${STORAGE_KEY_PREFIX}remembered_login`, JSON.stringify(data))
  } catch (e) {
    console.error('保存登录信息失败:', e)
  }
}

/**
 * 获取记住的登录信息
 */
export function getRememberedLogin() {
  try {
    const data = localStorage.getItem(`${STORAGE_KEY_PREFIX}remembered_login`)
    if (!data) {
      return null
    }
    
    const parsed = JSON.parse(data)
    
    // 检查是否过期（30天）
    const thirtyDays = 30 * 24 * 60 * 60 * 1000
    if (Date.now() - parsed.timestamp > thirtyDays) {
      clearRememberedLogin()
      return null
    }
    
    return {
      loginAccount: decode(parsed.loginAccount),
      password: decode(parsed.password)
    }
  } catch (e) {
    console.error('读取登录信息失败:', e)
    return null
  }
}

/**
 * 清除记住的登录信息
 */
export function clearRememberedLogin() {
  try {
    localStorage.removeItem(`${STORAGE_KEY_PREFIX}remembered_login`)
  } catch (e) {
    console.error('清除登录信息失败:', e)
  }
}

