<template>
  <el-container class="admin-layout">
    <!-- 顶部导航栏 -->
    <el-header class="top-header">
      <div class="header-content">
        <!-- 左侧Logo区域 -->
        <div class="header-left">
          <div class="logo-section" @click="router.push('/admin/products')">
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
          
          <!-- 用户头像 -->
          <div class="user-avatar" @click="router.push('/admin/profile')" title="个人中心">
            <el-icon><User /></el-icon>
          </div>
          
          <!-- 欢迎文字 -->
          <span class="welcome-text" @click="router.push('/admin/profile')" title="个人中心">欢迎您,{{ userStore.userInfo?.nickname || '管理员' }}</span>
          
          <!-- 退出登录 -->
          <div class="logout-section" @click="handleLogout">
            <el-icon class="logout-icon"><SwitchButton /></el-icon>
            <span class="logout-text">退出登录</span>
          </div>
        </div>
      </div>
    </el-header>
    
    <el-container class="main-container">
      <!-- 左侧边栏 -->
      <el-aside :width="sidebarWidth" class="admin-sidebar">
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        router
        class="admin-menu"
      >
        <!-- 商品管理 - 单独菜单项 -->
        <el-menu-item index="/admin/products">
            <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        
        <!-- 订单列表 - 单独菜单项 -->
        <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
          <template #title>订单列表</template>
        </el-menu-item>
        
        <!-- 订单统计 - 单独菜单项 -->
        <el-menu-item index="/admin/orders/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>订单统计</template>
        </el-menu-item>
        
        <!-- 统计报表 - 单独菜单项 -->
        <el-menu-item index="/admin/reports">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>统计报表</template>
        </el-menu-item>
        
        <!-- 用户管理 - 单独菜单项 -->
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        
        <!-- 个人中心 - 单独菜单项 -->
        <el-menu-item index="/admin/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
      
      <!-- 折叠按钮 -->
      <div class="collapse-trigger" @click="toggleSidebar">
        <el-icon>
          <component :is="sidebarCollapsed ? Expand : Fold" />
        </el-icon>
      </div>
    </el-aside>
      
      <!-- 主内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Goods, 
  Document, 
  DataAnalysis, 
  QuestionFilled, 
  User,
  SwitchButton,
  Expand,
  Fold
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { showSuccess } from '@/utils/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const sidebarCollapsed = ref(false)

const sidebarWidth = computed(() => sidebarCollapsed.value ? '64px' : '220px')

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/admin/products')) return '/admin/products'
  if (path.startsWith('/admin/orders/statistics')) return '/admin/orders/statistics'
  if (path.startsWith('/admin/orders')) return '/admin/orders'
  if (path.startsWith('/admin/reports')) return '/admin/reports'
  if (path.startsWith('/admin/users')) return '/admin/users'
  if (path.startsWith('/admin/profile')) return '/admin/profile'
  return path
})

function handleHelp() {
  // 帮助功能
  showSuccess('帮助功能开发中')
}

function handleLogout() {
    userStore.logout()
    showSuccess('已退出登录')
    router.push('/login')
  }

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
  }

</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 - 蓝绿色分区 */
.top-header {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  padding: 0;
  height: 64px;
  line-height: 64px;
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
  margin-right: 40px;
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

/* 右侧用户信息区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: 20px;
  flex-shrink: 0;
}

.header-right > * {
  display: flex;
  align-items: center;
  justify-content: center;
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

/* 主容器 */
.main-container {
  flex: 1;
  overflow: hidden;
  display: flex;
}

/* 左侧边栏 */
.admin-sidebar {
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.admin-menu {
  border: none;
  flex: 1;
  overflow-y: auto;
  padding-bottom: 0;
  margin-bottom: 0;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  height: 56px;
  line-height: 56px;
  color: #606266;
  font-size: 16px;
  padding-left: 20px !important;
}

.admin-menu :deep(.el-menu-item .el-icon),
.admin-menu :deep(.el-sub-menu__title .el-icon) {
  font-size: 20px;
  width: 20px;
  margin-right: 10px;
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background-color: #f5f7fa;
  color: #409eff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
  border-right: 3px solid #409eff;
}

.admin-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #409eff;
}

.admin-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 56px !important;
  height: 50px;
  line-height: 50px;
  font-size: 15px;
}

/* 菜单项标题容器 - 使用flex布局让徽章靠右 */
.admin-menu :deep(.el-menu-item .el-menu-item__title) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 12px;
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
  font-size: 20px;
}

/* 折叠状态下的菜单样式 */
.admin-menu.el-menu--collapse {
  width: 64px;
}

.admin-menu.el-menu--collapse :deep(.el-menu-item),
.admin-menu.el-menu--collapse :deep(.el-sub-menu__title) {
  padding-left: 20px !important;
  text-align: center;
}

.admin-menu.el-menu--collapse :deep(.el-menu-item .el-icon),
.admin-menu.el-menu--collapse :deep(.el-sub-menu__title .el-icon) {
  margin-right: 0;
}

.admin-menu.el-menu--collapse :deep(.el-menu-item.is-active) {
  border-right: none;
  border-bottom: 3px solid #409eff;
}

/* 主内容区 */
.admin-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .welcome-text {
    display: none;
  }
  
  .top-nav-menu {
    margin: 0 10px;
  }
}
</style>

