<template>
  <div class="layout">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/products')">
          <h2>在线购物系统</h2>
        </div>
        <div class="nav-menu">
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            router
            class="header-menu"
          >
            <el-menu-item index="/products">商品</el-menu-item>
            <el-menu-item index="/cart" v-if="userStore.isLoggedIn">
              购物车
            </el-menu-item>
            <el-menu-item index="/orders" v-if="userStore.isLoggedIn">
              我的订单
            </el-menu-item>
            <el-menu-item index="/notifications" v-if="userStore.isLoggedIn">
              通知
            </el-menu-item>
          </el-menu>
        </div>
        <div class="user-menu">
          <el-dropdown v-if="userStore.isLoggedIn" @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userStore.userInfo?.nickname || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="addresses">收货地址</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin">管理后台</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <div v-else class="auth-buttons">
            <el-button @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { showSuccess } from '@/utils/message'
import { getNotifications } from '@/api/notification'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/products')) return '/products'
  if (path.startsWith('/cart')) return '/cart'
  if (path.startsWith('/orders')) return '/orders'
  if (path.startsWith('/admin')) return '/admin'
  return path
})

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'addresses') {
    router.push('/addresses')
  } else if (command === 'admin') {
    router.push('/admin')
  } else if (command === 'logout') {
    userStore.logout()
    showSuccess('已退出登录')
    router.push('/products')
  }
}

async function loadUnreadCount() {
  if (!userStore.isLoggedIn) {
    unreadCount.value = 0
    return
  }
  try {
    const response = await getNotifications({ page: 1, size: 1 })
    if (response.code === 0) {
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    // 静默失败，不影响页面展示
    console.error('加载未读通知数失败:', error)
  }
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    loadUnreadCount()
    // 每30秒刷新一次未读数量
    setInterval(loadUnreadCount, 30000)
  }
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px !important;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo h2 {
  margin: 0;
  font-size: 20px;
  color: #409eff;
}

.nav-menu {
  flex: 1;
}

.header-menu {
  border-bottom: none;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #606266;
  padding: 5px 10px;
  border-radius: 4px;
}

.user-info:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.notification-badge {
  display: inline-block;
}

.main-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
}
</style>

