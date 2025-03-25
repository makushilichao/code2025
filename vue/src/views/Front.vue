<template>
    <div id="app">
        <!-- 头部导航栏 -->
        <header class="header">
            <div class="logo">IT论坛系统</div>
            <nav class="nav">
                <ul>
                    <li><a href="#">首页</a></li>
                    <li><a href="#">热门文章</a></li>
                    <li><a href="#">最新文章</a></li>
                    <li><a href="#">分类</a></li>
                </ul>
            </nav>
            <div style="width: fit-content; display: flex; align-items: center; padding-right: 20px">
                <div class="search" style="padding: 5px; margin-right: 10px">
                    <input type="text" placeholder="搜索文章">
                    <button>搜索</button>
                </div>
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
                            <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码</el-dropdown-item>
                            <el-dropdown-item @click="loginOutToManager">前往后台</el-dropdown-item>
                            <el-dropdown-item @click="loginOut">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </header>

        <!-- 轮播图 -->
        <div class="slider">
            <img src="https://dummyimage.com/1200x400/000/fff" alt="幻灯片1">
            <img src="https://dummyimage.com/1200x400/000/fff" alt="幻灯片2">
            <img src="https://dummyimage.com/1200x400/000/fff" alt="幻灯片3">
        </div>

        <!-- 文章列表 -->
        <div class="article-list">
            <article v-for="(article, index) in articles" :key="index">
                <h2>{{ article.title }}</h2>
                <p class="meta">
                    <span>{{ article.author }}</span>
                    <span>{{ article.date }}</span>
                    <span>{{ article.views }} 阅读</span>
                </p>
                <p class="summary">{{ article.summary }}</p>
                <a href="#" class="read-more">阅读全文</a>
            </article>
        </div>

        <!-- 侧边栏 -->
        <aside class="sidebar">
            <div class="widget">
                <h3>热门文章</h3>
                <ul>
                    <li v-for="(article, index) in hotArticles" :key="index">
                        <a href="#">{{ article.title }}</a>
                    </li>
                </ul>
            </div>
            <div class="widget">
                <h3>分类</h3>
                <ul>
                    <li><a href="#">技术</a></li>
                    <li><a href="#">生活</a></li>
                    <li><a href="#">娱乐</a></li>
                </ul>
            </div>
        </aside>

        <!-- 底部 -->
        <footer class="footer">
            <p>版权所有 &copy; 2025 IT论坛系统</p>
        </footer>
    </div>
</template>

<script setup>
import {reactive, ref} from 'vue';
import router from "@/router/index.js";


const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}')
})
// 模拟文章数据
const articles = ref([
    {
        title: '文章标题1',
        author: '作者1',
        date: '2025-03-25',
        views: 100,
        summary: '这是文章的摘要内容1。'
    },
    {
        title: '文章标题2',
        author: '作者2',
        date: '2025-03-24',
        views: 200,
        summary: '这是文章的摘要内容2。'
    }
]);

const loginOutToManager = () => {
    location.href = '/manager/home'
}

const loginOut = () => {
    localStorage.removeItem('code_user')
    location.href = '/login'
}

// 模拟热门文章数据
const hotArticles = ref([
    {
        title: '热门文章标题1'
    },
    {
        title: '热门文章标题2'
    }
]);
</script>

<style scoped>
/* 全局样式 */
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
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
}

.logo {
    font-size: 24px;
}

.nav ul {
    list-style-type: none;
    margin-left: 200px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.nav ul li {
    margin-right: 20px;
}

.nav ul li a {
    color: white;
    text-decoration: none;
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
}

.slider {
    width: 100%;
    height: 400px;
    overflow: hidden;
}

.slider img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 文章列表样式 */
.article-list {
    flex: 1;
    padding: 20px;
}

article {
    margin-bottom: 20px;
    border-bottom: 1px solid #ccc;
    padding-bottom: 20px;
}

.meta {
    color: #777;
    font-size: 14px;
}

.read-more {
    color: #007bff;
    text-decoration: none;
}

/* 侧边栏样式 */
.sidebar {
    width: 300px;
    padding: 20px;
    background-color: #f4f4f4;
}

.widget {
    margin-bottom: 20px;
}

.widget h3 {
    margin-top: 0;
}

.widget ul {
    list-style-type: none;
    padding: 0;
}

.widget ul li {
    margin-bottom: 10px;
}

.widget ul li a {
    color: #333;
    text-decoration: none;
}

</style>