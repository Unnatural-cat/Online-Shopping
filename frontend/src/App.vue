<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 应用启动时，如果有token，尝试获取用户信息
onMounted(() => {
  if (userStore.token) {
    userStore.fetchUserInfo().catch(err => {
      // 如果获取用户信息失败（如token过期），清除token
      console.error('获取用户信息失败:', err)
      if (err.response?.status === 401 || err.response?.status === 403) {
        userStore.logout()
      }
    })
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}
</style>
