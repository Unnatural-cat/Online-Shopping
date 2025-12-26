import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register } from '@/api/auth'
import { getProfile } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/token'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  
  const isAdmin = computed(() => {
    return userInfo.value?.role === 'ADMIN'
  })

  // 登录
  async function loginAction(loginData) {
    try {
      const response = await login(loginData)
      if (response.code === 0) {
        const newToken = response.data.token
        setToken(newToken)
        token.value = newToken
        await fetchUserInfo()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 注册
  async function registerAction(registerData) {
    try {
      const response = await register(registerData)
      if (response.code === 0) {
        return { success: true, message: '注册成功，请登录' }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const response = await getProfile()
      if (response.code === 0) {
        userInfo.value = response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果是401或403，说明token无效，清除token
      if (error.response?.status === 401 || error.response?.status === 403) {
        removeToken()
        token.value = null
        userInfo.value = null
      }
      throw error
    }
  }

  // 登出
  function logout() {
    removeToken()
    token.value = null
    userInfo.value = null
  }

  // 初始化时如果有token，获取用户信息（静默失败，不阻塞应用启动）
  if (token.value) {
    fetchUserInfo().catch(() => {
      // 静默失败，不影响应用启动
    })
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout
  }
})

