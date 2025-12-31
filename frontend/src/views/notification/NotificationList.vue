<template>
  <component :is="isAdminRoute ? 'div' : 'CustomerLayout'">
    <div class="notification-list">
      <div class="page-header">
        <h1>{{ isAdminRoute ? '通知中心' : '我的通知' }}</h1>
        <div class="header-actions">
          <el-button v-if="unreadCount > 0" type="primary" @click="handleMarkAllRead">全部标记为已读</el-button>
        </div>
      </div>

      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilterChange">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="false">未读</el-radio-button>
          <el-radio-button :label="true">已读</el-radio-button>
        </el-radio-group>
      </div>

      <div v-loading="loading" class="notification-container">
        <el-empty v-if="!loading && notifications.length === 0" description="暂无通知" />
        <div v-else class="notification-items">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            :class="['notification-item', { 'unread': !notification.isRead }]"
            @click="handleNotificationClick(notification)"
          >
            <div class="notification-icon">
              <el-icon v-if="notification.type === 'ORDER_SHIPPED'" color="#67C23A" size="24">
                <Truck />
              </el-icon>
              <el-icon v-else color="#409EFF" size="24">
                <Bell />
              </el-icon>
            </div>
            <div class="notification-body">
              <div class="notification-title">
                <span>{{ notification.title }}</span>
                <el-badge v-if="!notification.isRead" is-dot class="unread-dot" />
              </div>
              <div class="notification-text">{{ notification.content }}</div>
              <div class="notification-time">{{ formatDate(notification.createdAt) }}</div>
            </div>
            <div class="notification-actions">
              <el-button
                v-if="!notification.isRead"
                link
                size="small"
                @click.stop="handleMarkRead(notification.id)"
              >
                标记已读
              </el-button>
            </div>
          </div>
        </div>

        <div v-if="total > 0" class="pagination-wrapper">
        <Pagination
          :total="total"
          :page="queryParams.page"
          :size="queryParams.size"
          @change="handlePageChange"
        />
        </div>
      </div>
    </div>
  </component>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell, Truck } from '@element-plus/icons-vue'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/notification'
import { showSuccess, showError } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()
const route = useRoute()

// 检测是否在管理端路由中
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

const loading = ref(false)
const notifications = ref([])
const total = ref(0)
const unreadCount = ref(0)
const filterType = ref(null)

const queryParams = reactive({
  page: 1,
  size: 20,
  isRead: null
})

async function loadNotifications() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.isRead !== null) {
      params.isRead = queryParams.isRead
    }

    const response = await getNotifications(params)
    if (response && response.code === 0) {
      notifications.value = response.data?.content || []
      total.value = response.data?.totalElements || 0
      unreadCount.value = response.data?.unreadCount || 0
    } else {
      notifications.value = []
      total.value = 0
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('加载通知列表失败:', error)
    // 如果是401或403，可能是未登录，不显示错误
    if (error.response?.status === 401 || error.response?.status === 403) {
      notifications.value = []
      total.value = 0
      unreadCount.value = 0
    } else {
      showError('加载通知列表失败')
    }
  } finally {
    loading.value = false
  }
}

function handleFilterChange() {
  queryParams.isRead = filterType.value
  queryParams.page = 1
  loadNotifications()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadNotifications()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function handleMarkRead(id) {
  try {
    const response = await markAsRead(id)
    if (response.code === 0) {
      showSuccess('已标记为已读')
      // 更新本地状态
      const notification = notifications.value.find(n => n.id === id)
      if (notification) {
        notification.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    showError('标记已读失败')
  }
}

async function handleMarkAllRead() {
  try {
    const response = await markAllAsRead()
    if (response.code === 0) {
      showSuccess('已全部标记为已读')
      unreadCount.value = 0
      notifications.value.forEach(n => {
        n.isRead = true
      })
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    showError('标记全部已读失败')
  }
}

function handleNotificationClick(notification) {
  // 标记为已读
  if (!notification.isRead) {
    handleMarkRead(notification.id)
  }
  
  // 跳转到相关页面
  if (notification.link) {
    router.push(notification.link)
  } else if (notification.orderNo) {
    // 根据当前路由判断跳转到管理端还是用户端
    if (isAdminRoute.value) {
      router.push(`/admin/orders/${notification.orderNo}`)
    } else {
      router.push(`/orders/${notification.orderNo}`)
    }
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notification-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 200px);
}

/* 管理端样式调整 */
:deep(.admin-main) .notification-list {
  padding: 20px;
  max-width: 100%;
  margin: 0;
  min-height: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
}

.filter-bar {
  background: white;
  padding: 15px 20px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.notification-container {
  background: white;
  border-radius: 4px;
  min-height: 400px;
  padding: 10px 0;
}

.notification-items {
  padding: 0;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.notification-item.unread:hover {
  background-color: #d9ecff;
}

.notification-icon {
  margin-right: 15px;
  flex-shrink: 0;
}

.notification-body {
  flex: 1;
  min-width: 0;
}

.notification-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.unread-dot {
  margin-left: 8px;
}

.notification-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-actions {
  flex-shrink: 0;
  margin-left: 15px;
}

.pagination-wrapper {
  padding: 20px;
  display: flex;
  justify-content: center;
}
</style>

