<template>
    <div>
        <div class="card" style="margin-bottom: 5px" >
            <el-button type="primary" @click="handleAdd">插 入</el-button>
        </div>

        <div class="card" style="margin-bottom: 5px">
            <el-table :data="data.tableData" stripe style="width: 100%"
                      :header-cell-style="{color:'#333', backgroundColor:'#ddd'}"
            >
                <el-table-column label="轮播图">
                    <template #default="scope">
                        <el-image v-if="scope.row.img" :src="scope.row.img"
                                  style="width: 40px; height: 40px; border-radius: 5px; display: block"
                                  :preview-src-list="[scope.row.img]" :preview-teleported="true">
                            >
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="adminName" label="执行用户"/>
                <el-table-column prop="createTime" label="执行时间"/>
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

        <el-dialog title="轮播图信息" v-model="data.formVisible" destroy-on-close="true">
            <el-form ref="formRef" :model="data.form">
                <el-form-item labal="轮播图" prop="img">
                    <el-upload
                            action="http://localhost:9999/files/upload"
                            :show-file-list="false"
                            :headers="{token: data.user.token}"
                            :on-success="handleFileSuccess"
                            list-type="picture"
                    >
                        <el-button type="primary">上传图片</el-button>
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

        <el-dialog title="博客内容" v-model="data.viewVisible" width="60%" destroy-on-close>
            <div v-html="data.content" style="padding: 0 20px"></div>
        </el-dialog>
    </div>
</template>

<script setup>
import {Search} from "@element-plus/icons-vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import {onBeforeUnmount, reactive, ref, shallowRef} from "vue";
import request from "../../utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
    user: JSON.parse(localStorage.getItem('code_user') || '{}'),
    title: null,
    pageNum: 1,
    pageSize: 5,
    totle: 0,
    tableData: [],
    form: {},
    formVisible: false,
    rules: {
        title: [
            {required: true, message: '请输入博客标题', trigger: 'blur'},
            {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ],
        content: [
            {required: true, message: '请输入博客内容', trigger: 'blur'},
        ]
    },
    content: null,
    viewVisible: false,
    categoryData: [],
})

/*wangEditor初始化开始*/
const editorRef = shallowRef()
const mode = 'default'
const editorConfig = {MENU_CONF: {}}
//图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
    headers: {
        token: data.user.token
    },
    server: 'http://localhost:9999/files/wang/upload',
    fileName: 'file',
}
//组件销毁时，销毁编辑器实例
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor) {
        editor.destroy()
    }
})
//记录editor实例！
const handleCreated = (editor) => {
    editorRef.value = editor
}
/*wangeditor初始化结束*/

const formRef = ref()

const load = () => {
    request.get('/carousel/selectPage', {
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
            request.post('/carousel/add', data.form).then(res => {
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
    request.put('/carousel/update', data.form).then(res => {
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
        request.delete('/carousel/delete/' + id).then(res => {
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

</style>