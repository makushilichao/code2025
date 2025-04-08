<template>
    <div class="card" style="width: 100%; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); border-radius: 8px; padding: 20px;">
        <el-page-header @back="onBack">
            <template #content>
                <div class="flex items-center">
                    <img v-if="data.user?.avatar" style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;"
                         :src="data.user?.avatar">
                    <img v-else style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;"
                         src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55b3png.png" alt="">
                    <span class="text-large font-600 mr-3" style="margin: -2px 10px"> {{data.user?.username}} </span>
                    <el-tag type="success">安全</el-tag>
                </div>
            </template>
            <template #extra>
                <div class="flex items-center">
                    <el-button type="primary" class="ml-2" @click="show = !show">编辑</el-button>
                </div>
            </template>
        </el-page-header>

        <el-descriptions title="个人管理" border style="margin-top: 20px;">
            <el-descriptions-item
                :rowspan="2"
                :width="140"
                label="个人照片"
                align="center"
            >
                <img v-if="data.user?.avatar" style="width: 100px; height: 100px; object-fit: cover;"
                     :src="data.user?.avatar">
                <img v-else style="width: 40px; height: 40px; object-fit: cover;"
                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55b3png.png" alt="">
            </el-descriptions-item>
            <el-descriptions-item label="用户名">{{data.user?.name}}</el-descriptions-item>
            <el-descriptions-item label="电话号码">{{data.user?.phone}}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{data.user.email}}</el-descriptions-item>
            <el-descriptions-item label="职业">{{data.user.occupation}}</el-descriptions-item>
            <el-descriptions-item label="联系地址">
                {{data.user.address}}
            </el-descriptions-item>
        </el-descriptions>

        <el-form ref="formRef" :model="editData" :rules="rules" label-width="80px" style="padding: 20px 30px 20px 0" v-show="show">
            <el-form-item label="账号" prop="username">
                <el-input v-model="editData.username" autocomplete="off" placeholder="请输入账号"
                />
            </el-form-item>
            <el-form-item label="名称" prop="name">
                <el-input v-model="editData.name" autocomplete="off" placeholder="请输入名称"
                />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
                <el-input v-model="editData.phone" autocomplete="off" placeholder="请输入电话"
                />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="editData.email" autocomplete="off" placeholder="请输入邮箱"
                />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="editData.sex"> <!-- 修改为绑定 editData.sex -->
                    <el-radio value="1">男</el-radio>
                    <el-radio value="2">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="职业" prop="occupation">
                <el-input v-model="editData.occupation" autocomplete="off" placeholder="请输入职业"
                />
            </el-form-item>
            <el-form-item label="联系地址" prop="address">
                <el-input v-model="editData.address" autocomplete="off" placeholder="请输入联系地址"
                />
            </el-form-item>
            <el-form-item label="头像" prop="avatar">
                <el-upload
                    action="http://localhost:9999/files/upload"
                    :headers="{token: editData.token}"
                    :on-success="handleFileSuccess"
                    list-type="picture"
                >
                    <el-button type="primary">上传头像</el-button>
                </el-upload>
            </el-form-item>
            <div style="text-align: center; margin-top: 20px;">
                <el-button type="primary" @click="submitForm">保 存</el-button>
                <el-button @click="cancelEdit">取 消</el-button>
            </div>
        </el-form>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { defineEmits } from "vue";
import request from "../../utils/request.js";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";

const show = ref(false);
const formRef = ref(null);
const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
});
let editData = reactive({
    ...data.user,
    sex: data.user.sex || '1' // 初始化 sex 属性
});

const rules = reactive({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ],
    phone: [
        { required: true, message: '请输入电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    ],
    occupation: [
        { required: true, message: '请输入职业', trigger: 'blur' },
    ],
    address: [
        { required: true, message: '请输入联系地址', trigger: 'blur' },
    ],
    sex: [
        { required: true, message: '请选择性别', trigger: 'change' } // 添加 sex 验证规则
    ]
});

const onBack = () => {
    router.back();
};

const handleFileSuccess = (res) => {
    editData.avatar = res.data;
};

const emit = defineEmits(['updateUser']);

const submitForm = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            let url;
            if (data.user.role === 'ADMIN') {
                url = '/admin/update';
            } else if (data.user.role === 'USER') {
                url = '/user/update';
            }
            request.put(url, editData)
                .then((res) => {
                    if (res.code === '200') {
                        ElMessage.success("修改成功！");
                        // 更新原始数据
                        data.user = {...editData };
                        localStorage.setItem('code_user', JSON.stringify(data.user));
                        emit('updateUser');
                        show.value = false;
                    } else {
                        ElMessage.error(res.msg);
                    }
                })
                .catch((error) => {
                    ElMessage.error('网络请求失败，请稍后重试');
                    console.error('请求出错:', error);
                });
        } else {
            ElMessage.error('请填写完整且正确的信息');
        }
    });
};

const cancelEdit = () => {
    editData = {...data.user };
    show.value = false;
};
</script>

<style scoped>
.card {
    background-color: #fff;
    margin: 20px auto;
    max-width: 800px;
}

.el-descriptions {
    font-size: 14px;
}

.el-form-item {
    margin-bottom: 15px;
}

.el-upload {
    display: block;
}
</style>