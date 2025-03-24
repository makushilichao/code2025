import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const request = axios.create({
    baseURL: 'http://localhost:9999',
    timeout: 30000
})

// 请求拦截器
// 在发送请求之前做些什么
request.interceptors.request.use(config => {
    config.headers['contentType'] = 'application/json;charset=utf-8';
    let user = JSON.parse(localStorage.getItem('code_user') || '{}');
    config.headers['token'] = user.token;
    return config
}, error => {
    return Promise.reject(error)
});

// 响应拦截器
// 对响应数据做点什么
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === '401') {
            ElMessage.error(res.msg)
            router.push('/login')
        } else {
            return res;
        }
    }, error => {
        if (error.response.status === 404) {
            ElMessage.error('未找到请求接口')
        } else if (error.response.status === 500) {
            ElMessage.error('服务器错误')
        } else {
            console.error(error.message)
        }
        return Promise.reject(error)
    }
)

export default request