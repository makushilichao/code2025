<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.title"
                      placeholder="请输入标题查询"
                      :prefix-icon="Search" @keydown.enter="handleEnter"></el-input>
            <el-button type="primary" @click="load">查 询</el-button>
            <el-button type="" @click="reset">重 置</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px" v-if="data.user.role === 'ADMIN'">
            <el-button type="primary" @click="handleAdd">新 建</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe style="width: 100%"
                      :header-cell-style="{color:'#333', backgroundColor:'#ddd'}"
            >
                <el-table-column prop="title" label="分类标题"/>
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

        <el-dialog title="活动信息" v-model="data.formVisible" width="35%" destroy-on-close="true">
            <el-form ref="formRef" :model="data.form" :rules="data.rules">
                <el-form-item label="分类标题" prop="title">
                    <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入分类标题"
                              @keydown.enter="handleEnter($event)"/>
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
import {Search} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    title: null,
    pageNum: 1,
    pageSize: 5,
    total: 0,
    tableData: [],
    form: {},
    formVisible: false,
    rules: {
        title: [
            {required: true, message: '请输入分类标题', trigger: 'blur'},
            {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ]
    }
})
const formRef = ref()

const load = () => {
    request.get('category2/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            title: data.title
        }
    }).then(res => {
        if (res.code === '200') {
            data.tableData = res.data?.list
            data.total = res.data?.total
        } else {
            ElMessage.error(res.msg)
        }
    })
}

load()

const reset = () => {
    data.title = null
    load()
}

const handleAdd = () => {
    data.formVisible = true
    data.form = {}
}

const add = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/category2/add', data.form).then(res => {
                if (res.code === '200') {
                    data.formVisible = false
                    ElMessage.success("添加成功！");
                    load();
                } else {
                    ElMessage.error(res.msg);
                }
            });
        } else {
            console.log('表单验证失败');
        }
    });
}

const update = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.put('/category2/update', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("更新成功！")
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
    data.form.id ? update() : add();
}

const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row))
    data.formVisible = true
}

const deleteRow = (id) => {
    ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {type: 'warning'}).then(res => {
        request.delete('/category2/delete/' + id).then(res => {
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

const handleEnter = (e) => {
    e.preventDefault()
    save()
}
</script>

<style>

</style>