<template>
    <div class="bg">
        <div style="width: 450px; height: 370px; background-color: rgba(255, 255, 255, 0.5); border-radius: 10px; box-shadow: 0 0 8px rgba(0, 0, 0, .12); padding: 50px; margin-bottom: 150px">
            <el-form status-icon ref="formRef" :model="data.form" :rules="data.rules">
                <div style="align-items: center; margin-bottom: 20px; text-align: center; font-size: 20px; font-weight: bold">
                    欢 迎 登 录
                </div>
                <el-form-item prop="username" size="large">
                    <el-input v-model="data.form.username" autocomplete="off" prefix-icon="User" size="default"
                              placeholder="请输入账号" @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item prop="password" size="large">
                    <el-input show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock"
                              size="default" placeholder="请输入密码" @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item prop="role">
                    <el-select style="width: 100%" v-model="data.form.role" placeholder="请选择角色" size="default">
                        <el-option label="管理员" value="ADMIN"></el-option>
                        <el-option label="普通用户" value="USER"></el-option>
                    </el-select>
                </el-form-item>
                <el-button style="width: 100%" type="primary" @click="login">登 录</el-button>
                <div style="text-align: right; margin-top: 10px">
                    还没有账号？<a href="/register">立即注册</a>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const formRef = ref()
const data = reactive({
    form: {
        username: '',
        password: '',
        role: ''
    },
    rules: {
        username: [
            {required: true, message: '请输入账号', trigger: 'blur'},
            {min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur'}
        ],
        password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: ['blur', 'change']}
        ]
    }
})

const login = () => {
    console.log("当前角色:", data.form.role); // 调试输出角色
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/login', data.form).then(res => {
                console.log("请求返回:", res);
                if (res.code === '200') {
                    localStorage.setItem("code_user", JSON.stringify(res.data || {}));
                    localStorage.setItem('isLoggedIn', 'true');
                    ElMessage.success("登录成功！");
                    router.push('/manager/home');
                } else {
                    ElMessage.error(res.msg);
                }
            }).catch(err => {
                console.error("请求失败:", err);
                ElMessage.error("请求失败，请重试");
            });
        }
    });
}

const handleEnter = () => {
    login()
}
</script>

<style scoped>
.bg {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    overflow: hidden;
    background-image: url("@/assets/image/login.jpg");
    background-size: cover;
}
</style>