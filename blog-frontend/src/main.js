import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import './style.css'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { $typst, loadFonts } from '@myriaddreamin/typst.ts'

import App from './App.vue'
import router from './router'

$typst.use({
  key: 'access-model',
  forRoles: ['compiler'],
  provides: [loadFonts([], { assets: ['text', 'cjk'] })],
})

const app = createApp(App)

// 注册 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
