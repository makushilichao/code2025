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

            <el-watermark :font="font" :content="data.blogData.userName">
                <div style="height: 500px" />
            </el-watermark>
        </div>

        <div class="article-content-container comment-section">
            <div class="comment-header">
                <h3>评论区</h3>
            </div>
            <div class="comment-container">
                <el-input v-model="data.comment" type="textarea" placeholder="请输入评论内容" style="margin-bottom: 20px"></el-input>
                <el-button type="primary" @click="handleComment" :loading="isCommentLoading">评论</el-button>
            </div>
            <div class="comment-list">
                <div v-for="(item, index) in data.commentData" :key="index" class="comment-item" :class="{ 'highlight-comment': item.userId === data.user.id }"
                     @mouseenter="highlightComment(index)" @mouseleave="unhighlightComment(index)">
                    <div class="comment-avatar">
                        <img v-if="item.avatar" :src="item.avatar" alt="avatar" style="width: 40px; height: 40px; border-radius: 50%">
                        <img v-else src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="avatar" style="width: 40px; height: 40px">
                    </div>
                    <div class="comment-content">
                        <div class="comment-author">{{ item.userName }}</div>
                        <div class="comment-text">{{ item.content }}</div>
                        <div class="comment-time">{{ item.createTime }}</div>
                        <el-button size="small" @click="handleDeleteComment(item.id)" v-if="item.userId === data.user.id">删除</el-button>
                        <el-button size="small" @click="showReplyBox(index)">回复</el-button>
                        <div v-if="showReply[index]" class="reply-container">
                            <el-input v-model="replyComments[index]" type="textarea" placeholder="请输入回复内容"></el-input>
                            <el-button type="success" size="small" @click="handleReply(index)">发送回复</el-button>
                        </div>
                    </div>
                    <div class="reply-count">{{ item.replies ? item.replies.length : 0 }}条回复</div>
                    <div v-if="item.replies && item.replies.length > 0" class="reply-list">
                        <div v-for="(reply, replyIndex) in item.replies" :key="replyIndex" class="reply-item">
                            <div class="reply-avatar">
                                <img v-if="reply.avatar" :src="reply.avatar" alt="reply-avatar" style="width: 30px; height: 30px; border-radius: 50%">
                                <img v-else src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="reply-avatar" style="width: 30px; height: 30px">
                            </div>
                            <div class="reply-content">
                                <div class="reply-author">@{{ reply.userId }}</div>
                                <div class="reply-text">{{ reply.content }}</div>
                                <div class="reply-time">{{ reply.createTime }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 底部 -->
        <footer class="footer">
            <p>版权所有 &copy; 2025 IT论坛系统</p>
        </footer>
        <el-backtop :right="100" :bottom="100" />
    </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue';
import router from "@/router/index.js";
import request from "../../utils/request.js";
import { ElMessage } from 'element-plus';

const isDark = ref(false);
const isCommentLoading = ref(false);
const highlightedCommentIndex = ref(-1);
const replyComments = ref({});
const showReply = ref({});

const font = reactive({
    color: 'rgba(0, 0, 0,.15)',
});

watch(
    isDark,
    () => {
        font.color = isDark.value
            ? 'rgba(255, 255, 255,.15)'
            : 'rgba(0, 0, 0,.15)';
    },
    {
        immediate: true,
    }
);

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    blogId: router.currentRoute.value.query.id,
    blogData: {},
    commentData: [],
    comment: ''
});

const loadBlogData = async () => {
    try {
        const res = await request.get('/blog/selectById/' + data.blogId);
        if (res.code === '200') {
            data.blogData = res.data;
        } else {
            ElMessage.error(res.msg);
            // 可添加重试逻辑
        }
    } catch (error) {
        ElMessage.error('请求文章数据失败，请稍后重试');
    }
};

const loadComments = async () => {
    try {
        const res = await request.get('/comment/selectPage', {
            params: {
                blogId: data.blogId
            }
        });
        if (res.code === '200') {
            console.log(res)
            data.commentData = res.data?.list;
            data.commentData.forEach((item, index) => {
                showReply.value[index] = false;
                replyComments.value[index] = '';
            });
        } else {
            ElMessage.error(res.msg);
            // 可添加重试逻辑
        }
    } catch (error) {
        ElMessage.error('请求评论数据失败，请稍后重试');
    }
};

const handleComment = async () => {
    if (!data.comment) {
        ElMessage.warning('请输入评论内容');
        return;
    }
    isCommentLoading.value = true;
    try {
        const res = await request.post('/comment/add', {
            blogId: data.blogId,
            content: data.comment,
            userId: data.user.id,
            name: data.user.name
        });
        if (res.code === '200') {
            ElMessage.success("评论成功！");
            data.comment = '';
            await loadComments();
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('评论失败，请稍后重试');
    } finally {
        isCommentLoading.value = false;
    }
};

const handleReply = async (index) => {
    const commentId = data.commentData[index].id;
    const replyContent = replyComments.value[index];
    const userId = data.user.id;
    if (!replyContent) {
        ElMessage.warning('请输入回复内容');
        return;
    }
    isCommentLoading.value = true;
    try {
        const res = await request.post('/comment/reply', {
            blogId: data.blogId,
            commentId: commentId,
            content: replyContent,
            userId: data.user.id,
            name: data.user.name,
            parentCommentId: data.commentData[index].parentCommentId,
        });
        if (res.code === '200') {
            ElMessage.success("回复成功！");
            replyComments.value[index] = '';
            showReply.value[index] = false;
            await loadComments();
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('回复失败，请稍后重试');
    } finally {
        isCommentLoading.value = false;
    }
};

const handleDeleteComment = async (id) => {
    try {
        const res = await request.delete('/comment/delete/' + id);
        if (res.code === '200') {
            ElMessage.success("删除评论成功！");
            await loadComments();
        } else {
            ElMessage.error(res.msg);
        }
    } catch (error) {
        ElMessage.error('删除评论失败，请稍后重试');
    }
};

const highlightComment = (index) => {
    highlightedCommentIndex.value = index;
};

const unhighlightComment = () => {
    highlightedCommentIndex.value = -1;
};

const showReplyBox = (index) => {
    showReply.value[index] = true;
};

loadBlogData();
loadComments();

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

/* 评论区样式 */
.comment-section {
    flex: 1;
    padding: 20px;
    background-color: white;
    margin: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.comment-header {
    border-bottom: 1px solid #e0e0e0;
    padding-bottom: 10px;
    margin-bottom: 20px;
}

.comment-container {
    margin-bottom: 20px;
}

.comment-list {
    padding: 0;
    list-style-type: none;
}

.comment-item {
    display: flex;
    margin-bottom: 20px;
    padding: 10px;
    border-radius: 5px;
    transition: background-color 0.3s ease;
    background-color: #f9f9f9;
}

.highlight-comment {
    background-color: #e8f4ff;
}

.comment-avatar {
    margin-right: 10px;
}

.comment-content {
    flex: 1;
}

.comment-author {
    font-weight: bold;
}

.comment-time {
    color: #777;
    font-size: 12px;
}

.reply-container {
    margin-top: 10px;
}

.reply-list {
    margin-left: 30px;
    padding: 0;
    list-style-type: none;
}

.reply-item {
    display: flex;
    margin-bottom: 10px;
    padding: 5px;
    border-radius: 5px;
    background-color: #f4f9ff;
}

.reply-avatar {
    margin-right: 10px;
}

.reply-content {
    flex: 1;
}

.reply-author {
    font-weight: bold;
}

.reply-time {
    color: #777;
    font-size: 12px;
}
</style>