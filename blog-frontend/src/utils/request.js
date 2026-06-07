import axios from 'axios'
import { ElMessage } from 'element-plus'

// 请求实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('blog_token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(response => {
  const res = response.data
  if (res && res.code && res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    // 未登录或token失效
    if (res.code === 401 || res.code === 1003) {
      localStorage.removeItem('blog_token')
      localStorage.removeItem('blog_user')
      if (location.hash.includes('/admin')) {
        location.hash = '/admin/login'
      }
    }
    return Promise.reject(new Error(res.message || 'Error'))
  }
  // 统一解包: 页面侧直接 res 就能拿到业务数据, 不再写 res.data
  return res ? res.data : null
}, error => {
  ElMessage.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default request
