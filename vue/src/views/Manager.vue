<template>
    <div :class="{ 'dark-mode': isDark }">
        <!--头部组件-->
        <div class="header" style="height: 60px; display: flex;">
            <div style="width: 240px; display: flex; align-items: center; padding-left: 20px; background-color: #3a456b">
                <img style="width: 40px; height: 40px; border-radius: 50%" src="@/assets/image/logo.jpg" alt="">
                <span style="font-size: 20px; font-weight: bold; color: #f1f1f1; margin-left: 5px">IT论坛后台系统</span>
            </div>
            <div style="flex: 1; display: flex; align-items: center; padding-left: 20px">
                <el-breadcrumb :separator-icon="ArrowRight">
                    <el-breadcrumb-item :to="{ path: '/manager/home' }">首 页</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
                </el-breadcrumb>
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
                <a href="https://github.com/Code-2025/vue" target="_blank">
                    <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
                         style="width: 40px; height: 40px; margin-right: 10px">
                </a>
                <el-dropdown>
                    <div style="display: flex; align-items: center">
                        <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%"
                             :src="data.user?.avatar">
                        <img v-else style="width: 40px; height: 40px"
                             src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" alt="">
                        <span style="padding-left: 8px">{{ data.user?.name }}</span>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                            <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码
                            </el-dropdown-item>
                            <el-dropdown-item @click="loginOutToFront">返回前台</el-dropdown-item>
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
                <el-menu router :default-openeds="['1']" :default-active="router.currentRoute.value.path"
                         style="min-height: calc(100vh - 60px)">
                    <el-menu-item index="/manager/home">
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
                        <el-menu-item index="/manager/blog">博客信息</el-menu-item>
                        <el-menu-item index="/manager/blogCategory">博客分类</el-menu-item>
                    </el-sub-menu>
                    <el-sub-menu index="3">
                        <template #title>
                            <el-icon>
                                <Star/>
                            </el-icon>
                            <span>活动管理</span>
                        </template>
                        <el-menu-item index="/manager/activity">活动信息</el-menu-item>
                        <el-menu-item index="/manager/activityCategory">活动分类</el-menu-item>
                    </el-sub-menu>
                    <el-sub-menu index="4">
                        <template #title>
                            <el-icon>
                                <Setting/>
                            </el-icon>
                            <span>系统管理</span>
                        </template>
                        <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">系统公告</el-menu-item>
                        <el-menu-item index="/manager/notice" v-else>公告信息</el-menu-item>
                        <el-menu-item index="/manager/carousel">轮播图设置</el-menu-item>
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
    <footer class="footer">
        <p>版权所有 &copy; 2025 IT论坛系统</p>
    </footer>
</template>

<script setup>
import {reactive} from "vue";
import router from "@/router/index.js";
import {Sunrise, Sunset, ArrowRight} from '@element-plus/icons-vue'
import {useDark, useToggle} from '@vueuse/core'

const isDark = useDark()
const toggleDark = useToggle(isDark)

const value1 = reactive({value: true})

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}')
})

const loginOutToFront = () => {
    location.href = '/front'
}

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
    overflow-y: auto; /* 添加滚动条 */
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
    width: 8px; /* 滚动条宽度 */
}

.sidebar::-webkit-scrollbar-track {
    background-color: #f1f1f1; /* 滚动条轨道背景颜色 */
}

.sidebar::-webkit-scrollbar-thumb {
    background-color: #888; /* 滚动条滑块颜色 */
    border-radius: 4px; /* 滚动条滑块圆角 */
}

.sidebar::-webkit-scrollbar-thumb:hover {
    background-color: #555; /* 滚动条滑块悬停时的颜色 */
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

.dark-mode .footer {
    background-color: #333;
    color: #fff;
}

/* 确保文字在暗模式下可见 */
.dark-mode .header span,
.dark-mode .el-menu-item span,
.dark-mode .el-sub-menu__title span {
    color: #ccc;
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>