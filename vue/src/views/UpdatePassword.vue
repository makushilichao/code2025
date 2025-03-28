<template>
    <div class="password-change-container">
        <div class="card" style="width: 600px; margin: 20px auto; padding: 20px">
            <div class="card-header">
                <div class="card-title">修改密码</div>
            </div>
            <el-form ref="formRef" :rules="data.rules" :model="data.user" label-width="120px" class="password-form">
                <el-form-item prop="password" label="原密码" style="padding: 10px">
                    <el-input show-password v-model="data.user.password" autocomplete="off" prefix-icon="Lock" placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item prop="newPassword" label="新密码" style="padding: 10px">
                    <el-input show-password v-model="data.user.newPassword" autocomplete="off" prefix-icon="Lock" placeholder="请输入新密码" @input="validatePasswordStrength" />
                    <template #extra>
                        <div :class="['password-strength', passwordStrengthClass]">
                            {{ passwordStrengthText }}
                        </div>
                    </template>
                </el-form-item>
                <el-form-item prop="confirmPassword" label="确认密码" style="padding: 10px">
                    <el-input show-password v-model="data.user.confirmPassword" autocomplete="off" prefix-icon="Lock" placeholder="请再次输入密码" @input="validatePasswordMatch" />
                </el-form-item>
                <div class="button-container">
                    <el-button type="primary" @click="updatePassword">保 存</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import request from "../../utils/request.js";
import { ElMessage } from "element-plus";

// 定义验证密码强度的函数
const validatePasswordStrength = (rule, value, callback) => {
    let strength = 0;
    if (value.length >= 8) strength++;
    if (/[a-z]/.test(value)) strength++;
    if (/[A-Z]/.test(value)) strength++;
    if (/[0-9]/.test(value)) strength++;
    if (/[^a-zA-Z0-9]/.test(value)) strength++;

    passwordStrength.value = strength;

    if (strength < 3) {
        callback(new Error('密码强度太弱，请包含至少 8 个字符，以及大小写字母、数字和特殊字符中的至少两种'));
    } else {
        callback();
    }
};

// 定义验证确认密码一致性的函数
const validatePasswordMatch = (rule, value, callback) => {
    if (value!== data.user.newPassword) {
        callback(new Error('两次输入的密码不一致，请重新输入'));
    } else {
        callback();
    }
};

const formRef = ref(null);

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    rules: {
        password: [
            { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
            { required: true, message: '请输入新密码', trigger: ['blur', 'change'] },
            { validator: validatePasswordStrength, trigger: ['blur', 'change'] }
        ],
        confirmPassword: [
            { required: true, message: '请再次输入密码', trigger: ['blur', 'change'] },
            { validator: validatePasswordMatch, trigger: ['blur', 'change'] }
        ]
    }
});

const passwordStrength = ref(0);
const passwordStrengthText = computed(() => {
    if (passwordStrength.value === 0) return '密码强度：未输入';
    if (passwordStrength.value < 3) return '密码强度：弱';
    if (passwordStrength.value < 6) return '密码强度：中';
    return '密码强度：强';
});

const passwordStrengthClass = computed(() => {
    if (passwordStrength.value === 0) return '';
    if (passwordStrength.value < 3) return 'weak';
    if (passwordStrength.value < 6) return'medium';
    return'strong';
});

const updatePassword = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/updatePassword', data.user)
                .then(res => {
                    if (res.code === '200') {
                        ElMessage.success("修改成功！");
                        setTimeout(() => {
                            localStorage.removeItem('code_user');
                            location.href = '/login';
                        }, 1500);
                    } else {
                        ElMessage.error(res.msg);
                    }
                })
                .catch(error => {
                    console.error('网络请求出错:', error);
                    ElMessage.error('网络请求出错，请稍后重试');
                });
        }
    });
};
</script>

<style scoped>
.card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
            padding: 20px;
            border-bottom: 1px solid #eaeaea;
        }

.card-title {
            font-size: 20px;
            font-weight: bold;
            text-align: center;
        }

.password-form {
                padding: 20px;
            }

.button-container {
              text-align: center;
              margin-top: 20px;
          }

.password-strength {
                font-size: 12px;
                margin-top: 5px;
            }

.password-strength.weak {
                color: #ff4d4f;
            }

.password-strength.medium {
                color: #faad14;
            }

.password-strength.strong {
                color: #52c41a;
            }

html,
body {
    overflow-y: hidden;
}
</style>