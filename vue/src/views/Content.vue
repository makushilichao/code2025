<template>
    <div id="app">
        <header class="header">
            <div class="logo">IT论坛系统</div>
            <nav class="nav" style="margin-left: 180px">
                <ul>
                    <li style="padding-right: 10px"><a href="/front">首页</a></li>
                    <li style="padding-right: 10px"><a href="#">热门文章</a></li>
                    <li style="padding-right: 10px"><a href="#">最新文章</a></li>
                    <li style="padding-right: 10px"><a href="#">分类</a></li>
                </ul>
            </nav>
            <div style="width: fit-content; display: flex; align-items: center; padding-right: 20px">
                <div class="search" style="padding: 5px; margin-right: 10px">
                    <input type="text" placeholder="搜索文章">
                    <button>搜索</button>
                </div>
                <el-dropdown style="cursor: pointer;">
                    <div style="display: flex; align-items: center">
                        <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%" :src="data.user?.avatar">
                        <img v-else style="width: 40px; height: 40px" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="">
                        <span style="padding-left: 8px; color: #ffffff">{{ data.user?.name }}</span>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                            <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
                            <el-dropdown-item @click="loginOutToManager">前往后台</el-dropdown-item>
                            <el-dropdown-item @click="loginOut">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </header>

        <!-- 文章内容展示区域 -->
        <div class="article-content-container">
            <h2 class="article-title" style="text-align: center">{{ data.blogData.title }}</h2>
            <p class="article-meta" style="margin-bottom: 20px; text-align: center">
                <span>作者: {{ data.blogData.userName }}</span>
                <span>文章类别: {{ data.blogData.categoryTitle }}</span>
                <span>发布时间: {{ data.blogData.createTime }}</span>
            </p>
            <div class="article-body" style="margin-top: 50px" v-html="data.blogData.content"></div>
        </div>

        <!-- 底部 -->
        <footer class="footer">
            <p>版权所有 &copy; 2025 IT论坛系统</p>
        </footer>
        <el-backtop :right="100" :bottom="100" />
    </div>
</template>

<script setup>
import { reactive } from 'vue';
import router from "@/router/index.js";
import request from "../../utils/request.js";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    blogId: router.currentRoute.value.query.id,
    blogData: {}
});

const load = () => {
    request.get('/blog/selectById/' + data.blogId).then(res => {
        console.log(res)
        if (res.code === '200') {
            data.blogData = res.data
        } else {
            ElMessage.error(res.msg)
        }
    })
}

load()

const loginOutToManager = () => {
    location.href = '/manager/home';
};

const loginOut = () => {
    localStorage.removeItem('code_user');
    location.href = '/login';
};
</script>

<style scoped>
/* 全局样式 */
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f4f4f4;
}

#app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

/* 头部导航栏样式 */
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #333;
    color: white;
    padding: 10px 20px;
    position: fixed;
    width: 100%;
    z-index: 1000;
}

.logo {
    font-size: 24px;
    font-weight: bold;
}

.nav ul {
    list-style-type: none;
    margin: 0;
    padding: 0;
    display: flex;
    align-items: center;
}

.nav ul li {
    margin-right: 20px;
}

.nav ul li a {
    color: white;
    text-decoration: none;
    transition: color 0.3s ease;
}

.nav ul li a:hover {
    color: #007bff;
}

.search input {
    padding: 5px;
    border: none;
    border-radius: 3px;
}

.search button {
    padding: 5px 10px;
    background-color: #555;
    color: white;
    border: none;
    border-radius: 3px;
    margin-left: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.search button:hover {
    background-color: #007bff;
}

/* 文章内容展示区域样式 */
.article-content-container {
    flex: 1;
    padding: 20px;
    background-color: white;
    margin: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.article-title {
    font-size: 28px;
    margin-bottom: 10px;
}

.article-meta {
    color: #777;
    font-size: 14px;
    margin-bottom: 20px;
}

.article-meta span {
    margin-right: 20px;
}

.article-body {
    line-height: 1.6;
    font-size: 16px;
}

.article-body p {
    margin-bottom: 15px;
}
</style>