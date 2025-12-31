/**
 * 主题管理工具
 */

// 主题配置
export const themes = {
  light: {
    name: '浅色主题',
    primaryColor: '#667eea',
    algorithm: 'default'
  },
  dark: {
    name: '深色主题',
    primaryColor: '#667eea',
    algorithm: 'darkAlgorithm'
  },
  purple: {
    name: '紫色主题',
    primaryColor: '#722ed1',
    algorithm: 'default'
  },
  blue: {
    name: '蓝色主题',
    primaryColor: '#1890ff',
    algorithm: 'default'
  },
  green: {
    name: '绿色主题',
    primaryColor: '#52c41a',
    algorithm: 'default'
  }
}

// 获取当前主题
export function getCurrentTheme() {
  const saved = localStorage.getItem('app-theme')
  return saved && themes[saved] ? saved : 'light'
}

// 设置主题
export function setTheme(themeName) {
  if (!themes[themeName]) {
    return
  }
  
  localStorage.setItem('app-theme', themeName)
  
  // 更新 HTML 的 data-theme 属性
  document.documentElement.setAttribute('data-theme', themeName)
  
  // 触发主题变更事件
  window.dispatchEvent(new CustomEvent('theme-change', { detail: { theme: themeName } }))
}

// 初始化主题
export function initTheme() {
  const currentTheme = getCurrentTheme()
  setTheme(currentTheme)
  updateCheckboxIndeterminateTheme()
  return currentTheme
}

// 更新半选状态复选框的主题色
export function updateCheckboxIndeterminateTheme() {
  const currentTheme = getCurrentTheme()
  const themeInfo = themes[currentTheme]
  const primaryColor = themeInfo.primaryColor
  
  // 动态设置半选状态的主题色
  const style = document.createElement('style')
  style.id = 'checkbox-indeterminate-theme'
  
  // 移除旧的样式
  const oldStyle = document.getElementById('checkbox-indeterminate-theme')
  if (oldStyle) {
    oldStyle.remove()
  }
  
  // 添加新的样式
  style.textContent = `
    .ant-tree .ant-tree-checkbox-indeterminate .ant-tree-checkbox-inner,
    .ant-tree-checkbox-indeterminate .ant-tree-checkbox-inner {
      background-color: ${primaryColor} !important;
      border-color: ${primaryColor} !important;
    }
    .ant-tree-checkbox-indeterminate:hover .ant-tree-checkbox-inner,
    .ant-tree .ant-tree-checkbox-indeterminate:hover .ant-tree-checkbox-inner {
      background-color: ${primaryColor} !important;
      border-color: ${primaryColor} !important;
      opacity: 0.8;
    }
  `
  document.head.appendChild(style)
}

