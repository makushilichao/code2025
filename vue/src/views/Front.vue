<template>
    <div id="app">
        <header class="header">
            <div class="logo">IT论坛系统</div>
            <nav class="nav">
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
                        <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%"
                             :src="data.user?.avatar">
                        <img v-else style="width: 40px; height: 40px"
                             src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="">
                        <span style="padding-left: 8px; color: #ffffff">{{ data.user?.name }}</span>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="router.push('/manager/person')">个人信息</el-dropdown-item>
                            <el-dropdown-item @click="router.push('/manager/updatePassword')">修改密码
                            </el-dropdown-item>
                            <el-dropdown-item @click="loginOutToManager">前往后台</el-dropdown-item>
                            <el-dropdown-item @click="loginOut">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </header>

        <div class="slider">
            <el-carousel :interval="4000" type="card" height="300px">
                <el-carousel-item v-for="item in data.carouselData" :key="item.id">
                    <img :src="item.img" alt="轮播图" style="width: 100%; height: 100%; object-fit: cover;">
                </el-carousel-item>
            </el-carousel>
        </div>

        <div style="padding-left: 20px; flex: 1; margin-bottom: -50px">
            <h2>精选文章</h2>
            <el-divider style="margin-top: -10px; width: 75.5%" content="精选文章">
                <el-icon style="font-size: 20px">
                    <StarFilled/>
                </el-icon>
            </el-divider>
        </div>

        <!-- 新增父容器 -->
        <div class="article-and-sidebar">
            <div class="article-list">
                <article v-for="blog in data.blogData" :key="blog.id" class="article-item">
                    <div class="article-content">
                        <h3><a @click="navTo('/front/author?id=' + blog.userId)" style="cursor: pointer">{{ blog.userName }}</a></h3>
                        <h2><a @click="navTo('/front/content?id=' + blog.id)" style="cursor: pointer">{{ blog.title }}</a></h2>
                        <p class="meta">
                            <span>{{ truncateContent(removeHtmlTags(blog.content)) }}</span>
                        </p>
                        <p class="summary">{{ blog.summary }}</p>
                    </div>
                    <img class="article-image" :src="blog.img" alt="文章图片">
                </article>
            </div>

            <!-- 侧边栏 -->
            <aside class="sidebar">
                <div class="widget">
                    <h3>热门文章</h3>
                    <ul>
                        <li v-for="(blog, index) in hotArticles" :key="index">
                            <a href="#">{{ blog.title }}</a>
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
        </div>

        <!-- 底部 -->
        <footer class="footer">
            <p>版权所有 &copy; 2025 IT论坛系统</p>
        </footer>
        <el-backtop :right="100" :bottom="100"/>
    </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue';
import router from "@/router/index.js";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";
import {StarFilled} from '@element-plus/icons-vue'

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    carouselData: [],
    blogData: [],
})

const getCarousel = async () => {
    try {
        const res = await request.get('/carousel/selectAll');
        if (res.code === '200') {
            data.carouselData = res.data.slice(0, 5);
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('获取轮播图数据失败');
    }
};

onMounted(async () => {
    await getCarousel();
});

const blogs = () => {
    request.get('/blog/selectAll').then(res => {
        console.log(res)
        if (res.code === '200') {
            data.blogData = res.data;
        } else {
            ElMessage.error(res.msg);
        }
    }).catch(error => {
        ElMessage.error('获取文章数据失败');
    })
}

blogs()

const navTo = (url) => {
    location.href = url
}

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

// 截断文章内容
const truncateContent = (content) => {
    const maxLength = 100;
    if (content.length > maxLength) {
        return content.slice(0, maxLength) + '...';
    }
    return content;
}

// 去除 HTML 标签
const removeHtmlTags = (html) => {
    return html.replace(/<[^>]*>/g, '');
}
</script>

<style scoped>
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
    margin-top: 60px;
}

.slider img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 文章列表与侧边栏父容器样式 */
.article-and-sidebar {
    display: flex;
    gap: 20px; /* 设置间隔 */
    padding: 20px;
}

/* 文章列表样式 */
.article-list {
    flex: 1;
}

.article-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    border-bottom: 1px solid #ccc;
    padding-bottom: 20px;
}

.article-content {
    flex: 1;
    max-width: calc(100% - 120px); /* 预留图片宽度 */
}

.article-image {
    width: 100px;
    height: 100px;
    object-fit: cover;
    align-self: flex-start;
    margin-top: 25px;
}

.meta {
    color: #777;
    font-size: 14px;
    max-height: 60px; /* 限制高度 */
    overflow: hidden;
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

/* 取消 el-dropdown 悬停时的白色边框 */
.el-dropdown .el-dropdown__trigger:hover {
    border: none;
    outline: none;
}

.el-carousel__item h3 {
    color: #475669;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
    text-align: center;
}

.el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
    background-color: #d3dce6;
}
</style>