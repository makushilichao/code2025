<template>
    <div>
        <div class="card" style="margin-bottom: 5px">
            <el-input clearable @clear="load" style="width: 260px; margin-right: 5px" v-model="data.title"
                      placeholder="请输入标题查询"
                      :prefix-icon="Search" @keydown.enter="handleEnter"></el-input>
            <el-button type="primary" @click="load">查 询</el-button>
            <el-button type="" @click="reset">重 置</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px" >
            <el-button type="primary" @click="handleAdd">新 建</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe style="width: 100%"
                      :header-cell-style="{color:'#333', backgroundColor:'#ddd'}"
            >
                <el-table-column label="封面" width="100">
                    <template #default="scope">
                        <el-image v-if="scope.row.img" :src="scope.row.img"
                                  style="width: 40px; height: 40px; border-radius: 5px; display: block"
                                  :preview-src-list="[scope.row.img]" :preview-teleported="true">
                            >
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="title" label="文章标题"/>
                <el-table-column prop="categoryTitle" label="文章类别"/>
                <el-table-column prop="content" label="文章内容">
                    <template v-slot="scope">
                        <el-button type="text" @click="viewContent(scope.row.content)">点击详情</el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="userName" label="发布用户"/>
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

        <el-dialog title="文章信息" v-model="data.formVisible" width="60%" destroy-on-close="true">
            <el-form ref="formRef" :model="data.form">
                <el-form-item labal="文章封面" prop="img">
                    <el-upload
                        action="http://localhost:9999/files/upload"
                        :show-file-list="false"
                        :headers="{token: data.user.token}"
                        :on-success="handleFileSuccess"
                        list-type="picture"
                    >
                        <el-button type="primary">上传封面</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="文章标题" prop="title">
                    <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入文章标题"
                              @keydown.enter="handleEnter($event)"
                    />
                </el-form-item>
                <el-form-item label="文章分类" prop="categoryId">
                    <el-select
                        v-model="data.form.categoryId"
                        placeholder="请选择文章类别"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="item in data.categoryData"
                            :key="item.id"
                            :label="item.title"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="文章内容" prop="content">
                    <div style="border: 1px solid #ccc; width: 100%">
                        <Toolbar
                            style="border-bottom: 1px solid #ccc;"
                            :mode="mode"
                            :editor="editorRef"
                        />
                        <Editor
                            style="height: 200px"
                            overflow-y="hidden;"
                            v-model="data.form.content"
                            :mode="mode"
                            :defaultConfig="editorConfig"
                            @onCreated="handleCreated"
                        />
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取 消</el-button>
                    <el-button type="primary" @click="save">提 交</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="文章内容" v-model="data.viewVisible" width="60%" destroy-on-close>
            <!-- 修改此处，添加样式 -->
            <div v-html="data.content" style="padding: 0 20px; white-space: normal;"></div>
        </el-dialog>
    </div>
</template>

<script setup>
import {Search} from "@element-plus/icons-vue";
import {reactive, ref, shallowRef, onBeforeUnmount} from "vue";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Editor, Toolbar} from '@wangeditor/editor-for-vue';
import '@wangeditor/editor/dist/css/style.css';

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
            {required: true, message: '请输入文章标题', trigger: 'blur'},
            {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ],
        content: [
            {required: true, message: '请输入文章内容', trigger: 'blur'},
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
    request.get('/blog/selectPage', {
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
    data.form = {}
    data.formVisible = true
}

const add = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            request.post('/blog/add', data.form).then(res => {
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
    request.put('/blog/update', data.form).then(res => {
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
        request.delete('/blog/delete/' + id).then(res => {
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