/**
 * 消息提示工具类 - 简化版本
 * 使用DOM操作直接创建消息提示
 */

let messageInstances = []
let messageId = 0

/**
 * 创建消息元素
 */
function createMessageElement(message, type, duration) {
  const id = `message-${messageId++}`
  
  // 创建消息容器
  const messageEl = document.createElement('div')
  messageEl.id = id
  messageEl.className = `message-container message-${type}`
  
  // 创建内容
  const content = document.createElement('div')
  content.className = 'message-content'
  
  // 创建图标
  const icon = document.createElement('div')
  icon.className = 'message-icon'
  icon.innerHTML = getIconSvg(type)
  
  // 创建文本
  const text = document.createElement('span')
  text.className = 'message-text'
  text.textContent = message
  
  content.appendChild(icon)
  content.appendChild(text)
  messageEl.appendChild(content)
  
  // 添加到页面
  const container = getMessageContainer()
  container.appendChild(messageEl)
  
  // 触发动画
  setTimeout(() => {
    messageEl.classList.add('message-show')
  }, 10)
  
  // 自动关闭
  if (duration > 0) {
    setTimeout(() => {
      closeMessage(id)
    }, duration)
  }
  
  messageInstances.push({ id, element: messageEl })
  
  return id
}

/**
 * 获取消息容器
 */
function getMessageContainer() {
  let container = document.getElementById('message-container')
  if (!container) {
    container = document.createElement('div')
    container.id = 'message-container'
    container.style.cssText = `
      position: fixed;
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 10000;
      pointer-events: none;
      display: flex;
      flex-direction: column;
      gap: 12px;
      align-items: center;
    `
    document.body.appendChild(container)
  }
  return container
}

/**
 * 获取图标SVG
 */
function getIconSvg(type) {
  const icons = {
    success: `<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="width: 20px; height: 20px;">
      <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </svg>`,
    error: `<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="width: 20px; height: 20px;">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <line x1="12" y1="8" x2="12" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      <line x1="12" y1="16" x2="12.01" y2="16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
    warning: `<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="width: 20px; height: 20px;">
      <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="12" y1="9" x2="12" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      <line x1="12" y1="17" x2="12.01" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
    info: `<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="width: 20px; height: 20px;">
      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
      <line x1="12" y1="16" x2="12" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      <line x1="12" y1="8" x2="12.01" y2="8" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
    </svg>`
  }
  return icons[type] || icons.info
}

/**
 * 关闭消息
 */
function closeMessage(id) {
  const instance = messageInstances.find(item => item.id === id)
  if (instance) {
    instance.element.classList.remove('message-show')
    instance.element.classList.add('message-hide')
    setTimeout(() => {
      instance.element.remove()
      messageInstances = messageInstances.filter(item => item.id !== id)
      
      // 如果没有消息了，移除容器
      const container = document.getElementById('message-container')
      if (container && container.children.length === 0) {
        container.remove()
      }
    }, 300)
  }
}

/**
 * 显示消息
 */
function showMessage(message, type = 'info', duration = 3000) {
  return createMessageElement(message, type, duration)
}

/**
 * 成功提示
 */
export function success(message, duration = 3000) {
  return showMessage(message, 'success', duration)
}

/**
 * 错误提示
 */
export function error(message, duration = 4000) {
  return showMessage(message, 'error', duration)
}

/**
 * 警告提示
 */
export function warning(message, duration = 3000) {
  return showMessage(message, 'warning', duration)
}

/**
 * 信息提示
 */
export function info(message, duration = 3000) {
  return showMessage(message, 'info', duration)
}

export default {
  success,
  error,
  warning,
  info
}
