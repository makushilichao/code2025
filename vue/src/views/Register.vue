<template>
    <div class="bg">
        <div style="width: 450px; height: 370px; background-color: rgba(255, 255, 255, 0.5); border-radius: 10px; box-shadow: 0 0 8px rgba(0, 0, 0, .12); padding: 50px; margin-bottom: 150px">
            <el-form status-icon ref="formRef" :model="data.form" :rules="data.rules">
                <div style="align-items: center; margin-bottom: 20px; text-align: center; font-size: 20px; font-weight: bold">
                    注 册 账 号
                </div>
                <el-form-item prop="username" size="large">
                    <el-input v-model="data.form.username" autocomplete="off" prefix-icon="User" size="default"
                              placeholder="请输入账号" @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item prop="password" size="large">
                    <el-input show-password v-model="data.form.password" autocomplete="off" prefix-icon="Lock"
                              size="default" placeholder="请输入密码" @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item prop="confirmPassword" size="large">
                    <el-input show-password v-model="data.form.confirmPassword" autocomplete="off" prefix-icon="Lock"
                              size="default" placeholder="请再次输入密码" @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-button style="width: 100%" type="primary" @click="register">注 册</el-button>
                <div style="text-align: right; margin-top: 10px">
                    已有账号？<a href="/login">登录</a>
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

const validatePass = (rule, value, callback) => {
    if (value !== data.form.password) {
        callback(new Error("两次输入不一致！"))
    } else {
        callback()
    }
}

const formRef = ref()
const data = reactive({
    form: {
        username: '',
        password: '',
        confirmPassword: ''
    },
    rules: {
        username: [
            {required: true, message: '请输入账号', trigger: 'blur'},
            {min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur'}
        ],
        password: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {min: 5, max: 15, message: '长度在 5 到 15 个字符', trigger: 'blur'}
        ],
        confirmPassword: [
            {required: true, message: '请再次输入密码', trigger: ['blur', 'change']},
            {validator: validatePass, trigger: ['blur', 'change']}
        ]
    }
})

const register = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/register', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("注册成功！")
                    router.push('/login')
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}

const handleEnter = () => {
    register()
}

</script>

<style scoped>
.bg {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    overflow: hidden;
    background-image: url("@/assets/image/register.jpg");
    background-size: cover;
}
</style>