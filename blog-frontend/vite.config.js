import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import wasm from 'vite-plugin-wasm'
import path from 'path'

// Vite 配置
export default defineConfig({
  plugins: [vue(), wasm()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  optimizeDeps: {
    exclude: ['@myriaddreamin/typst-ts-renderer', '@myriaddreamin/typst-ts-web-compiler']
  },
  ssr: {
    noExternal: ['@myriaddreamin/typst-ts-renderer', '@myriaddreamin/typst-ts-web-compiler']
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: false,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
