<template>
    <div class="card" style="width: 50%">
        <div class="card-header">
            <div class="card-title">修改密码</div>
        </div>
        <el-form ref="formRef" :rules="data.rules" :model="data.user" label-width="80px"
                 style="padding: 20px 30px 20px 0">
            <el-form-item prop="password" size="large" label="原密码">
                <el-input show-password v-model="data.user.password" autocomplete="off" prefix-icon="Lock"
                          size="default" placeholder="请输入原密码"/>
            </el-form-item>
            <el-form-item prop="newPassword" size="large" label="新密码">
                <el-input show-password v-model="data.user.newPassword" autocomplete="off" prefix-icon="Lock"
                          size="default" placeholder="请输入新密码"/>
            </el-form-item>
            <el-form-item prop="confirmPassword" size="large" label="确认密码">
                <el-input show-password v-model="data.user.confirmPassword" autocomplete="off" prefix-icon="Lock"
                          size="default" placeholder="请再次输入密码"/>
            </el-form-item>
            <div style="text-align: center">
                <el-button type="primary" @click="updatePassword">保 存</el-button>
            </div>
        </el-form>
    </div>
</template>
<script setup>
import {reactive, ref} from "vue";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    rules: {
        password: [
            {required: true, message: '请输入原密码', trigger: 'blur'},
        ],
        newPassword: [
            {required: true, message: '请输入新密码', trigger: ['blur', 'change']},
        ],
        confirmPassword: [
            {required: true, message: '请再次输入密码', trigger: ['blur', 'change']},
        ]
    }
})

const formRef = ref()

const updatePassword = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/updatePassword', data.user).then(res => {
                console.log(res)
                if (res.code === '200') {
                    ElMessage.success("修改成功！")
                    setInterval(() => {
                        localStorage.removeItem('code_user')
                        location.href = '/login'
                    }, 500)
                } else {
                    console.log(res.msg)
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
</script>
<style>

</style>