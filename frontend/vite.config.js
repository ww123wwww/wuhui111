import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    strictPort: true,
    // 浏览器访问前端同源地址，再由 Vite 转给 Java 后端。
    proxy: { '/api': 'http://127.0.0.1:8080' }
  },
  preview: { proxy: { '/api': 'http://127.0.0.1:8080' } }
})
