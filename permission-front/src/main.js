import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import { message } from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './style.css'
import './style/message.css'
import './style/theme.css'
import App from './App.vue'
import { initTheme, updateCheckboxIndeterminateTheme } from './utils/theme'

// 将 message 挂载到 window 对象，方便 api.js 使用
window.message = message

// 初始化主题
initTheme()

// 监听主题变更事件，更新半选状态样式
window.addEventListener('theme-change', () => {
  updateCheckboxIndeterminateTheme()
})

const app = createApp(App)
app.use(Antd)
app.mount('#app')
