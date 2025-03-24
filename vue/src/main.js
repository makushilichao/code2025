import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/css/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

app.use(router)
app.use(ElementPlus,{
    locale: zhCn,
})
app.mount('#app')

// 注册element-plus图标,循环全局导入
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}