import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    host: '0.0.0.0', // 允许外部访问
    port: 5173, // 指定端口
    strictPort: false, // 如果端口被占用，尝试下一个可用端口
    proxy: {
      '/one-permission': {
        target: 'http://127.0.0.1:1105',
        changeOrigin: true,
        secure: false
      }
    }
  },
})
