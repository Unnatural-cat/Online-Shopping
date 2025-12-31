<template>
  <el-container class="customer-layout">
    <!-- 顶部栏：蓝绿色分区 -->
    <el-header class="top-header">
      <div class="header-content">
        <!-- 左侧Logo区域 -->
        <div class="header-left">
          <div class="logo-section" @click="$router.push('/products')">
            <div class="logo-icon">
              <div class="logo-diamond">
                <div class="logo-dot"></div>
                <div class="logo-arrow">→</div>
              </div>
            </div>
            <h2 class="logo-text">在线购物 Pro</h2>
          </div>
        </div>
        
        <!-- 右侧功能区 -->
        <div class="header-right">
          <!-- 帮助图标 -->
          <el-tooltip content="帮助" placement="bottom">
            <div class="help-icon" @click="handleHelp">
              <el-icon><QuestionFilled /></el-icon>
            </div>
          </el-tooltip>
          
          <!-- 通知铃铛 -->
          <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge" v-if="userStore.isLoggedIn">
            <div class="notification-icon" @click="handleMenuClick('/notifications')">
              <el-icon><Bell /></el-icon>
            </div>
          </el-badge>
          
          <!-- 用户头像 -->
          <div class="user-avatar" v-if="userStore.isLoggedIn" @click="handleMenuClick('/profile')" title="个人中心">
            <el-icon><User /></el-icon>
          </div>
          
          <!-- 欢迎文字 -->
          <span class="welcome-text" v-if="userStore.isLoggedIn" @click="handleMenuClick('/profile')" title="个人中心">
            欢迎您,{{ userStore.userInfo?.nickname || '用户' }}
          </span>
          
          <!-- 退出登录 -->
          <div class="logout-section" v-if="userStore.isLoggedIn" @click="handleLogout">
            <el-icon class="logout-icon"><SwitchButton /></el-icon>
            <span class="logout-text">退出登录</span>
          </div>
          
          <!-- 未登录时的登录/注册按钮 -->
          <div v-else class="auth-buttons">
            <el-button link class="auth-btn" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" class="auth-btn-primary" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>
    
    <el-container class="main-container">
      <!-- 左侧边栏：导航菜单 -->
      <el-aside :width="sidebarWidth" class="customer-sidebar">
        <el-menu
          :default-active="activeMenu"
          :collapse="sidebarCollapsed"
          router
          class="customer-menu"
        >
          <el-menu-item index="/products">
            <el-icon><House /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <el-menu-item index="/cart" v-if="userStore.isLoggedIn">
            <el-icon><ShoppingCart /></el-icon>
            <template #title>
              <span class="menu-item-text">购物车</span>
              <el-badge :value="cartItemCount" :hidden="cartItemCount === 0" class="menu-badge" />
            </template>
          </el-menu-item>
          
          <el-menu-item index="/orders" v-if="userStore.isLoggedIn">
            <el-icon><Document /></el-icon>
            <template #title>我的订单</template>
          </el-menu-item>
          
          <el-menu-item index="/orders/statistics" v-if="userStore.isLoggedIn">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>订单统计</template>
          </el-menu-item>
          
          <el-menu-item index="/profile" v-if="userStore.isLoggedIn">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>
          
          <el-menu-item index="/admin" v-if="userStore.isAdmin">
            <el-icon><Setting /></el-icon>
            <template #title>管理后台</template>
          </el-menu-item>
        </el-menu>
        
        <!-- 折叠按钮 -->
        <div class="collapse-trigger" @click="toggleSidebar">
          <el-icon>
            <component :is="sidebarCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>
      </el-aside>
      
      <!-- 主内容区 -->
    <el-main class="main-content">
      <slot />
    </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  User, 
  ArrowDown, 
  Bell, 
  ShoppingCart, 
  House, 
  Document, 
  DataAnalysis,
  Setting,
  Expand,
  Fold,
  QuestionFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCart } from '@/api/cart'
import { getUnreadCount } from '@/api/notification'
import { showSuccess } from '@/utils/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const sidebarCollapsed = ref(false)
const cartItemCount = ref(0)
const notificationCount = ref(0)

const sidebarWidth = computed(() => sidebarCollapsed.value ? '64px' : '220px')

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/products')) return '/products'
  if (path.startsWith('/cart')) return '/cart'
  if (path.startsWith('/orders/statistics')) return '/orders/statistics'
  if (path.startsWith('/orders')) return '/orders'
  if (path.startsWith('/profile')) return '/profile'
  if (path.startsWith('/addresses')) return '/addresses'
  if (path.startsWith('/admin')) return '/admin'
  return path
})

async function loadCartCount() {
  if (!userStore.isLoggedIn) {
    cartItemCount.value = 0
    return
  }
  try {
    const response = await getCart()
    if (response.code === 0) {
      const items = response.data.items || []
      // 计算购物车中所有商品的总数量（不区分是否选中）
      cartItemCount.value = items.reduce((sum, item) => sum + item.quantity, 0)
    }
  } catch (error) {
    // 静默失败，可能是未登录或网络错误
    cartItemCount.value = 0
  }
}

async function loadNotificationCount() {
  if (!userStore.isLoggedIn) {
    notificationCount.value = 0
    return
  }
  try {
    const response = await getUnreadCount()
    if (response && response.code === 0) {
      notificationCount.value = response.data || 0
    } else {
      notificationCount.value = 0
    }
  } catch (error) {
    // 静默失败，可能是未登录或网络错误
    console.warn('获取通知数量失败:', error)
    notificationCount.value = 0
  }
}

function handleMenuSelect(key) {
  handleMenuClick(key)
}

function handleMenuClick(path) {
  if (path && path !== route.path) {
    console.log('跳转到:', path)
    router.push(path).catch(err => {
      // 忽略重复导航错误
      if (err.name !== 'NavigationDuplicated') {
        console.error('路由跳转失败:', err)
      }
    })
  }
}

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'addresses') {
    router.push('/addresses')
  } else if (command === 'admin') {
    router.push('/admin')
  } else if (command === 'logout') {
    handleLogout()
  }
}

function handleHelp() {
  showSuccess('帮助功能开发中')
}

function handleLogout() {
    userStore.logout()
    showSuccess('已退出登录')
    router.push('/products')
  }

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 监听登录状态变化，刷新购物车数量和通知数量
watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    loadCartCount()
    loadNotificationCount()
  } else {
    cartItemCount.value = 0
    notificationCount.value = 0
  }
})

onMounted(() => {
  loadCartCount()
  loadNotificationCount()
})

// 监听路由变化，刷新购物车数量和通知数量
watch(() => route.path, () => {
  loadCartCount()
  loadNotificationCount()
})
</script>

<style scoped>
.customer-layout {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 顶部栏 - 蓝绿色分区 */
.top-header {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  padding: 0;
  height: 64px;
  line-height: 64px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.top-header::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 30%;
  height: 100%;
  background: linear-gradient(135deg, #409eff 0%, #52b3ff 50%);
  z-index: 0;
}

.top-header::after {
  content: '';
  position: absolute;
  right: 0;
  top: 0;
  width: 70%;
  height: 100%;
  background: linear-gradient(135deg, #52b3ff 50%, #67c23a 100%);
  z-index: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  max-width: 100%;
  position: relative;
  z-index: 1;
}

/* 左侧Logo区域 */
.header-left {
  display: flex;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo-section:hover {
  opacity: 0.9;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-diamond {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #52b3ff 0%, #409eff 100%);
  transform: rotate(45deg);
  border-radius: 4px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

.logo-dot {
  width: 6px;
  height: 6px;
  background-color: #f56c6c;
  border-radius: 50%;
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

.logo-arrow {
  color: #f56c6c;
  font-size: 14px;
  font-weight: bold;
  transform: rotate(-45deg);
  position: relative;
  z-index: 2;
}

.logo-text {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.3px;
}

/* 帮助图标 */
.help-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.help-icon:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.help-icon .el-icon {
  font-size: 16px;
  color: #fff;
}

/* 右侧用户信息区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.header-right > * {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 通知图标容器 */
.notification-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.notification-icon:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.notification-icon .el-icon {
  font-size: 16px;
  color: #fff;
}

.notification-badge {
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  border-color: #f56c6c;
  font-weight: 600;
  top: -2px;
  right: -2px;
}

/* 用户头像 */
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.user-avatar:hover {
  background-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}

.user-avatar .el-icon {
  font-size: 18px;
  color: #fff;
}

/* 欢迎文字 */
.welcome-text {
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.welcome-text:hover {
  background-color: rgba(255, 255, 255, 0.15);
  text-decoration: underline;
}

/* 退出登录 */
.logout-section {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.logout-section:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.logout-icon {
  font-size: 16px;
  color: #fff;
}

.logout-text {
  color: #fff;
  font-size: 13px;
  font-weight: 500;
}

/* 未登录时的按钮 */
.auth-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.auth-btn {
  color: #fff !important;
  font-weight: 500;
}

.auth-btn:hover {
  color: rgba(255, 255, 255, 0.8) !important;
}

.auth-btn-primary {
  background-color: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
}

.auth-btn-primary:hover {
  background-color: rgba(255, 255, 255, 0.3) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
}

/* 主容器 */
.main-container {
  flex: 1;
  overflow: hidden;
  display: flex;
}

/* 左侧边栏 */
.customer-sidebar {
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.customer-menu {
  border: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0;
}

/* 折叠状态下的菜单容器 */
.customer-menu.el-menu--collapse {
  padding: 8px 0;
}

.customer-menu :deep(.el-menu-item),
.customer-menu :deep(.el-sub-menu__title) {
  height: 56px;
  line-height: 56px;
  color: #606266;
  font-size: 15px;
  padding-left: 20px !important;
  margin: 2px 8px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

/* 折叠状态下居中显示 */
.customer-menu.el-menu--collapse :deep(.el-menu-item),
.customer-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  padding-left: 0 !important;
  padding-right: 0 !important;
  justify-content: center;
  margin: 2px 0;
}

.customer-menu :deep(.el-menu-item .el-icon),
.customer-menu :deep(.el-sub-menu__title .el-icon) {
  font-size: 20px;
  width: 20px;
  margin-right: 10px;
}

/* 折叠状态下图标居中，移除右边距 */
.customer-menu.el-menu--collapse :deep(.el-menu-item .el-icon),
.customer-menu.el-menu--collapse :deep(.el-sub-menu__title .el-icon) {
  margin-right: 0;
  margin-left: 0;
}

.customer-menu :deep(.el-menu-item:hover),
.customer-menu :deep(.el-sub-menu__title:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

.customer-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
  border-right: 3px solid #409eff;
}

/* 折叠状态下激活项的样式调整 */
.customer-menu.el-menu--collapse :deep(.el-menu-item.is-active) {
  border-right: none;
  border-left: none;
}

.customer-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 56px !important;
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  margin: 2px 8px;
  border-radius: 6px;
}

/* 菜单项标题容器 - 使用flex布局让徽章靠右 */
.customer-menu :deep(.el-menu-item .el-menu-item__title) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 12px;
}

/* 折叠状态下标题容器居中 */
.customer-menu.el-menu--collapse :deep(.el-menu-item .el-menu-item__title),
.customer-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  justify-content: center;
  padding: 0;
}

/* 菜单项文本 */
.menu-item-text {
  flex: 1;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 徽章样式 - 定位到右侧，与文字对齐 */
.menu-badge {
  flex-shrink: 0;
  margin-left: auto;
  display: inline-flex;
  align-items: center;
}

.menu-badge :deep(.el-badge__content) {
  pointer-events: none;
  background-color: #f56c6c;
  border-color: #f56c6c;
  font-weight: 600;
  font-size: 12px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
  border-radius: 9px;
}

/* 折叠按钮 */
.collapse-trigger {
  height: 56px;
  line-height: 56px;
  text-align: center;
  border-top: 1px solid #e4e7ed;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
  flex-shrink: 0;
  margin-top: auto;
}

.collapse-trigger:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.collapse-trigger .el-icon {
  font-size: 18px;
}

/* 主内容区 */
.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 12px;
  }
  
  .header-right {
    gap: 12px;
  }
  
  .welcome-text {
    display: none;
  }
  
  .logout-text {
    display: none;
  }
  
  .customer-sidebar {
    position: absolute;
    z-index: 1000;
    height: 100%;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  }
}
</style>

