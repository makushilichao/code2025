import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite' //自动导入vue中的组件
import Components from 'unplugin-vue-components/vite' //自动导入ui组件
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers' //对应组件库引入

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        //elementplus按需导入
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            //配置elementplus采用sass样式配置
            resolvers: [ElementPlusResolver({importStyle: "sass"})],
        }),

    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        },
    },
})
