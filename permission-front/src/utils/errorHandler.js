import { message } from 'ant-design-vue'

/**
 * 统一错误处理函数
 * @param {Error|string} error - 错误对象或错误消息
 * @param {string} defaultMessage - 默认错误消息
 * @param {boolean} logError - 是否在控制台输出错误（默认true）
 */
export function handleError(error, defaultMessage = '操作失败', logError = true) {
  let errorMessage = defaultMessage
  
  if (error) {
    if (typeof error === 'string') {
      errorMessage = error
    } else if (error.message) {
      errorMessage = error.message
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    }
  }
  
  // 在控制台记录错误（开发环境）
  if (logError && import.meta.env.DEV) {
    console.error('错误详情:', error)
  }
  
  // 显示错误消息给用户
  message.error(errorMessage)
  
  return errorMessage
}

/**
 * 统一成功消息函数
 * @param {string} msg - 成功消息
 */
export function handleSuccess(msg = '操作成功') {
  message.success(msg)
}

