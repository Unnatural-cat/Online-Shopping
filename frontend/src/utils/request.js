import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './token'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是0，说明有错误
    if (res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: Token过期或无效
      if (res.code === 401 || res.code === 1002) {
        removeToken()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '请求失败'
    let shouldShowMessage = true
    
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 401:
          message = '未授权，请重新登录'
          removeToken()
          // 如果不在登录页，才跳转
          if (router.currentRoute.value.name !== 'Login') {
            router.push('/login')
          }
          break
        case 403:
          message = '拒绝访问，权限不足'
          // 如果是访问管理端API，清除token并跳转到登录页
          if (error.config?.url?.includes('/admin')) {
            removeToken()
            // 如果当前在管理端路由，跳转到登录页
            if (router.currentRoute.value.path.startsWith('/admin')) {
              router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
            }
          }
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = error.response.data?.message || '服务器错误'
          // 记录详细错误信息用于调试
          console.error('服务器错误:', error.response.data)
          break
        default:
          message = error.response.data?.message || `请求失败 (${status})`
      }
    } else if (error.request) {
      message = '网络错误，请检查网络连接'
    } else {
      message = error.message || '请求失败'
      // 如果是取消请求，不显示错误
      if (error.message === 'canceled' || error.code === 'ERR_CANCELED') {
        shouldShowMessage = false
      }
    }
    
    if (shouldShowMessage) {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

export default service

