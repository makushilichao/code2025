<template>
    <div class="card" style="width: 50%">
        <div class="card-header">
            <div class="card-title">个人信息</div>
        </div>
        <el-form ref="formRef" :model="data.user" label-width="80px" style="padding: 20px 30px 20px 0">
            <el-form-item label="账号" prop="username">
                <el-input v-model="data.user.username" autocomplete="off" placeholder="请输入账号"
                />
            </el-form-item>
            <el-form-item label="名称" prop="name">
                <el-input v-model="data.user.name" autocomplete="off" placeholder="请输入名称"
                />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
                <el-input v-model="data.user.phone" autocomplete="off" placeholder="请输入电话"
                />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="data.user.email" autocomplete="off" placeholder="请输入邮箱"
                />
            </el-form-item>
            <el-form-item labal="头像" prop="avatar">
                <el-upload
                        action="http://localhost:9999/files/upload"
                        :headers="{token: data.user.token}"
                        :on-success="handleFileSuccess"
                        list-type="picture-card"
                >
                    <el-button type="primary">上传头像</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
        <div style="text-align: center">
            <el-button type="primary" @click="update">保 存</el-button>
        </div>
    </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "../../utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
})

const handleFileSuccess = (res) => {
    data.user.avatar = res.data
}

const emit = defineEmits(['updateUser'])

const update = () => {
    let url
    if (data.user.role === 'ADMIN') {
        url = '/admin/update'
    } else if (data.user.role === 'USER') {
        url = '/user/update'
    }
    request.put(url, data.user).then(res => {
        if(res.code === '200'){
            ElMessage.success("修改成功！")
            localStorage.setItem('code_user', JSON.stringify(data.user))
            emit('updateUser')
            location.href = '/manager/home'
        }else{
            ElMessage.error(res.msg)
        }
    })
}
</script>

<style>

</style>