<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.username"
                      placeholder="请输入账号查询"
                      :prefix-icon="Search"></el-input>
            <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.name"
                      placeholder="请输入名称查询"
                      :prefix-icon="Search"></el-input>
            <el-button type="primary" @click="load">查 询</el-button>
            <el-button type="" @click="reset">重 置</el-button>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-button type="primary" @click="handleAdd">新 建</el-button>
            <el-button type="danger" @click="deleteBatch">批量删除</el-button>
            <el-button type="warning" @click="exportData">批量导出</el-button>
            <el-upload
                    style="display: inline-block; margin-left: 10px"
                    action="http://localhost:9999/user/import"
                    :show-file-list="false"
                    :on-success="handleImportSuccess"
            >
                <el-button type="success">批量导入</el-button>
            </el-upload>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe style="width: 100%"
                      :header-cell-style="{color:'#333', backgroundColor:'#ddd'}"
                      @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55"/>
                <el-table-column  label="头像" width="100">
                    <template #default="scope">
                        <el-image v-if="scope.row.avatar" :src="scope.row.avatar"
                                  style="width: 40px; height: 40px; border-radius: 50%; display: block"
                             :preview-src-list="[scope.row.avatar]"  :preview-teleported="true">
                        ></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="username" label="账号"/>
                <el-table-column prop="name" label="名称"/>
                <el-table-column prop="phone" label="电话"/>
                <el-table-column prop="email" label="邮箱"/>
                <el-table-column label="性别" prop="sex">
                    <template #default="scope">
                        <span>{{ scope.row.sex === 1 ? '男' : scope.row.sex === 2 ? '女' : '未知' }}</span>
                    </template>
                </el-table-column>
                <el-table-column width="100" label="编辑">
                    <template #default="scope">
                        <el-button type="primary" icon="Edit" circle @click="handleEdit(scope.row)"></el-button>
                        <el-button type="danger" icon="Delete" circle @click="deleteRow(scope.row.id)"></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="card" style="margin-bottom: 5px">
            <el-pagination
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :page-sizes="[5, 10, 20, 30]"
                    :total="data.total"
                    @current-change="load"
                    @size-change="load"
            />
        </div>

        <el-dialog title="用户信息" v-model="data.formVisible" width="35%" destroy-on-close="true">
            <el-form ref="formRef" :model="data.form" :rules="data.rules">
                <el-form-item label="账号" prop="username">
                    <el-input v-model="data.form.username" autocomplete="off" placeholder="请输入账号"
                              @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"
                              @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入电话"
                              @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="data.form.email" autocomplete="off" placeholder="请输入邮箱"
                              @keydown.enter="handleEnter"/>
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    <el-radio-group v-model="data.form.sex">
                        <el-radio value="1">男</el-radio>
                        <el-radio value="2">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item labal="头像" prop="avatar">
                    <el-upload
                            action="http://localhost:9999/files/upload"
                            :show-file-list="false"
                            :headers="{token: data.user.token}"
                            :on-success="handleFileSuccess"
                            list-type="picture-card"
                    >
                        <el-button type="primary">上传头像</el-button>
                    </el-upload>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取 消</el-button>
                    <el-button type="primary" @click="save">提 交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    name: null,
    username: null,
    total: 6,
    pageNum: 1,
    pageSize: 5,
    tableData: [],
    formVisible: false,
    form: {},
    rules: {
        username: [
            {required: true, message: '请输入账号', trigger: 'blur'}
        ],
        name: [
            {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        phone: [
            {required: true, message: '请输入手机号', trigger: 'blur'}
        ],
        email: [
            {required: true, message: '请输入邮箱', trigger: 'blur'}
        ]
    },
    rows: [],
    ids: []
})

const formRef = ref()

const load = () => {
    request.get('/user/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            name: data.name,
            username: data.username
        }
    }).then(res => {
        if (res.code === '200') {
            data.tableData = res.data.list
            data.total = res.data.total
        } else {
            ElMessage.error(res.msg)
        }
    })
}
load()

const reset = () => {
    data.name = null
    data.username = null
    load()
}

const handleAdd = () => {
    data.formVisible = true
    data.form = {}
}

const add = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/user/add', data.form).then(res => {
                if (res.code === '200') {
                    data.formVisible = false
                    ElMessage.success("添加成功！")
                    load()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}

const handleEdit = (row) => {
    data.formVisible = true
    data.form = JSON.parse(JSON.stringify(row))  //深度复制
}

const update = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.put('/user/update', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功！")
                    data.formVisible = false
                    load()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}

const save = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            data.form.id ? update() : add()
        }
    })
}

const deleteRow = (id) => {
    ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {type: 'warning'}).then(res => {
        request.delete('/user/delete/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success("删除成功！")
                load()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch(err => {
    })
}

const handleSelectionChange = (rows) => {
    data.rows = rows
    data.ids = data.rows.map(v => v.id)
}

const deleteBatch = () => {
    if (data.rows.length === 0) {
        ElMessage.warning("请选择要删除的数据！")
        return
    }
    ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {type: 'warning'}).then(res => {
        request.delete('/user/deleteBatch', {data: data.rows}).then(res => {
            if (res.code === '200') {
                ElMessage.success("批量删除成功！")
                load()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch(err => {
    })
}

const exportData = () => {
    //数组转为字符串
    let idsStr = data.ids.join(",")
    let url = `http://localhost:9999/user/export?username=${data.username === null ? '' : data.username}`
        + `&name=${data.name === null ? '' : data.name}`
        + `&ids=${idsStr}`
        + `&token=${data.user.token}`

    window.open(url);
}

const handleImportSuccess = (res) => {
    if (res === '200') {
        ElMessage.success("批量导入成功！")
        load()
    } else {
        ElMessage.error(res.msg)
    }
}

const handleEnter = () => {
    save()
}

const handleFileSuccess = (res) =>{
    data.form.avatar = res.data
}
</script>