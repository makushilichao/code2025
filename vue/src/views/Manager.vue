<template>
    <div :class="{ 'dark-mode': isDark }">
        <!--头部组件-->
        <div class="header" style="height: 60px; display: flex;">
            <div style="width: 240px; display: flex; align-items: center; padding-left: 20px; background-color: #3a456b">
                <img style="width: 40px; height: 40px; border-radius: 50%" src="@/assets/image/logo.jpg" alt="">
                <span style="font-size: 20px; font-weight: bold; color: #f1f1f1; margin-left: 5px">代码港湾</span>
            </div>
            <div style="flex: 1; display: flex; align-items: center; padding-left: 20px; border-bottom: 1px solid #ddd">
                <span style="margin-right: 5px; cursor: pointer" @click="router.push('/manager/home')">首页</span> /
                <span style="margin-left: 8px">{{ router.currentRoute.value.meta.name }}</span>
            </div>
            <div style="flex: 1; border-bottom: 1px solid #ddd"></div>
            <div style="width: fit-content; display: flex; align-items: center; padding-right: 20px; border-bottom: 1px solid #ddd">
                <el-switch
                    v-model="value1"
                    :active-action-icon="Sunset"
                    :inactive-action-icon="Sunrise"
                    @change="toggleDark"
                    style="margin-right: 10px"
                />
                <el-icon style="margin-right: 10px; font-size: 30px; color: #3efa2d"><ChromeFilled /></el-icon>
                <el-dropdown>
                    <div style="display: flex; align-items: center">
                        <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%"
                             :src="data.user?.avatar">
                        <img v-else style="width: 40px; height: 40px"
                             src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="">
                        <span style="padding-left: 8px">{{ data.user?.name }}</span>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                            <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码
                            </el-dropdown-item>
                            <el-dropdown-item @click="loginOut">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>

            </div>
        </div>
        <!--头部区域结束-->

        <!--下方区域开始-->
        <div class="main-content" style="display: flex">
            <!--菜单区域开始-->
            <div class="sidebar" style="width: 240px; box-shadow: 0 0 8px rgba(0, 0, 0, .12)">
                <el-menu router :default-openeds="['1', '2']" :default-active="router.currentRoute.value.path"
                         style="min-height: calc(100vh - 60px)">
                    <el-menu-item index="/manager/home" >
                        <el-icon>
                            <House/>
                        </el-icon>
                        <span>首 页</span></el-menu-item>
                    <el-sub-menu index="1" v-if="data.user.role === 'ADMIN'">
                        <template #title>
                            <el-icon>
                                <User/>
                            </el-icon>
                            <span>用户管理</span>
                        </template>
                        <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
                        <el-menu-item index="/manager/user">用户信息</el-menu-item>
                    </el-sub-menu>
                    <el-sub-menu index="2">
                        <template #title>
                            <el-icon>
                                <Message/>
                            </el-icon>
                            <span>信息管理</span>
                        </template>
                        <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">系统公告</el-menu-item>
                        <el-menu-item index="/manager/notice" v-else>公告信息</el-menu-item>
                        <el-menu-item index="/manager/blog">博客信息</el-menu-item>
                        <el-menu-item index="/manager/category">博客分类</el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </div>
            <!--菜单区域结束-->

            <!--数据区域开始-->
            <div :style="{ flex: 1, width: 0, padding: '10px', marginTop: '60px', marginLeft: '240px' }">
                <RouterView @updateUser="updateUser"/>
            </div>
            <!--数据区域结束-->
        </div>
        <!--下方区域结束-->
    </div>
</template>

<script setup>
import {reactive} from "vue";
import router from "@/router/index.js";
import {Sunrise, Sunset} from '@element-plus/icons-vue'
import {useDark, useToggle} from '@vueuse/core'

const isDark = useDark()
const toggleDark = useToggle(isDark)

const value1 = reactive({ value: true })

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}')
})

const loginOut = () => {
    localStorage.removeItem('code_user')
    location.href = '/login'
}

const updateUser = () => {
    data.user = JSON.parse(localStorage.getItem('code_user') || '{}')
}
</script>

<style>
.el-menu {
    border: none;
    background-color: #3a456b;
}

.el-sub-menu__title {
    background-color: #3a456b;
    color: #ddd;
}

.el-menu-item {
    color: #ddd;
    height: 50px;
}

.el-menu .is-active {
    color: #fff;
    background-color: #537bee;
}

.el-sub-menu__title:hover {
    background-color: #3a456b;

}

.el-menu-item:not(.is-active):hover {
    background-color: #7a9fff;
    color: #333;
}

.el-dropdown {
    cursor: pointer;
}

.el-tooltip__trigger {
    outline: none;
}

.el-menu--inline .el-menu-item {
    padding-left: 48px !important;
}

/* 固定头部 */
.header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 1000;
    background-color: #fff;
}

/* 固定侧边栏 */
.sidebar {
    position: fixed;
    top: 60px;
    left: 0;
    height: calc(100vh - 60px);
    z-index: 999;
}

/* 光模式样式 */
body {
    background-color: #f2f4ff;
    color: #333;
}

/* 暗模式样式 */
.dark-mode {
    background-color: #222;
    color: #fff;
}

.dark-mode .header {
    background-color: #333;
    border-bottom: 1px solid #555;
}

.dark-mode .header div {
    border-bottom: 1px solid #555;
}

.dark-mode .sidebar {
    background-color: #333;
}

.dark-mode .el-menu {
    background-color: #333;
}

.dark-mode .el-sub-menu__title {
    background-color: #333;
    color: #ccc;
}

.dark-mode .el-menu-item {
    color: #ccc;
}

.dark-mode .el-menu .is-active {
    background-color: #555;
}

.dark-mode .el-menu-item:not(.is-active):hover {
    background-color: #444;
    color: #fff;
}

.dark-mode .el-dropdown-menu {
    background-color: #333;
    border: 1px solid #555;
}

.dark-mode .el-dropdown-item {
    color: #ccc;
}

.dark-mode .el-dropdown-item:hover {
    background-color: #444;
    color: #fff;
}
</style>