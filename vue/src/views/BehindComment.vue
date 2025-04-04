<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.blogTitle"
                      placeholder="请输入标题查询"
                      :prefix-icon="Search" @keydown.enter="handleEnter"></el-input>
            <el-button type="primary" @click="load">查 询</el-button>
            <el-button type="" @click="reset">重 置</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe style="width: 100%"
                      :header-cell-style="{color:'#333', backgroundColor:'#ddd'}"
            >
                <el-table-column  label="头像" width="100">
                    <template #default="scope">
                        <el-image v-if="scope.row.avatar" :src="scope.row.avatar"
                                  style="width: 40px; height: 40px; border-radius: 50%; display: block"
                                  :preview-src-list="[scope.row.avatar]"  :preview-teleported="true">
                            ></el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="评论用户"/>
                <el-table-column prop="blogTitle" label="博客标题"/>
                <el-table-column prop="content" label="评论内容">
                    <template v-slot="scope">
                        <el-button type="text" @click="viewContent(scope.row.content)">点击详情</el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间"/>
                <el-table-column width="100" label="编辑" v-if="data.user.role === 'ADMIN'">
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

        <el-dialog title="评论内容" v-model="data.viewVisible" width="40%" destroy-on-close>

            <div v-html="data.content" style="padding: 0 20px; white-space: normal;"></div>
        </el-dialog>
    </div>
</template>

<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive, ref, shallowRef, onBeforeUnmount} from "vue";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import '@wangeditor/editor/dist/css/style.css';

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    blogTitle: null,
    pageNum: 1,
    pageSize: 5,
    total: 0,
    tableData: [],
    form: {},
    formVisible: false,
    rules: {
        blogTitle: [
            {required: true, message: '请输入评论标题', trigger: 'blur'},
            {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ],
        content: [
            {required: true, message: '请输入评论内容', trigger: 'blur'},
        ]
    },
    content: null,
    viewVisible: false,
    categoryData: [],
})

const editorRef = shallowRef()
const mode = 'default'
const editorConfig = {MENU_CONF: {}}

editorConfig.MENU_CONF['uploadImage'] = {
    headers: {
        token: data.user.token
    },
    server: 'http://localhost:9999/files/wang/upload',
    fileName: 'file',
}

onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor) {
        editor.destroy()
    }
})

const handleCreated = (editor) => {
    editorRef.value = editor
}

const formRef = ref()

const load = () => {
    request.get('/comment/selectPage', {
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            blogTitle: data.blogTitle,
            avatar: data.avatar
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
    data.blogTitle = null
    load()
}

const handleAdd = () => {
    data.form = {}
    data.formVisible = true
}

const add = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/comment/add', data.form).then(res => {
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

const update = () => {
    request.put('/comment/update', data.form).then(res => {
        if (res.code === '200') {
            ElMessage.success("更新成功！")
            data.formVisible = false
            load()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const save = () => {
    data.form.id ? update() : add()
}

const handleEdit = (row) => {
    data.form = JSON.parse(JSON.stringify(row))
    data.formVisible = true
}

const deleteRow = (id) => {
    ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {type: 'warning'}).then(res => {
        request.delete('/comment/delete/' + id).then(res => {
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

const handleFileSuccess = (res) => {
    data.form.img = res.data
}

const viewContent = (content) => {
    data.content = content
    data.viewVisible = true
}

const handleCategory = () => {
    request.get('/category/selectAll').then(res => {
        if (res.code === '200') {
            data.categoryData = res.data
        } else {
            ElMessage.error(res.msg)
        }
    })
}
handleCategory()
</script>

<style>
/* 可以添加更多样式来美化评论详情显示 */
.el-dialog__content div[v-html] {
    line-height: 1.6;
    font-size: 14px;
    color: #333;
}

.el-dialog__content div[v-html] p {
    margin-bottom: 10px;
}

.el-dialog__content div[v-html] h1,
.el-dialog__content div[v-html] h2,
.el-dialog__content div[v-html] h3,
.el-dialog__content div[v-html] h4,
.el-dialog__content div[v-html] h5,
.el-dialog__content div[v-html] h6 {
    margin-bottom: 10px;
    color: #222;
}

.el-dialog__content div[v-html] img {
    max-width: 100%;
    height: auto;
    margin-bottom: 10px;
}
</style>