<template>
  <div class="notification-list" v-loading="loading">
    <div class="notification-container">
      <div class="notification-header">
        <h1>通知中心</h1>
        <div class="header-actions">
          <el-button
            v-if="unreadCount > 0"
            @click="handleMarkAllRead"
            :loading="markingAll"
          >
            全部标记为已读
          </el-button>
        </div>
      </div>
      <div class="notification-content">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="all">
            <template #label>
              <span>全部</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="未读" name="unread">
            <template #label>
              <span>未读 <el-badge :value="unreadCount" :hidden="unreadCount === 0" /></span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="已读" name="read">
            <template #label>
              <span>已读</span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <div v-if="notifications.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无通知" />
        </div>

        <div v-else class="notification-items">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': !notification.isRead }"
            @click="handleItemClick(notification)"
          >
            <div class="notification-icon">
              <el-icon v-if="notification.type === 'ORDER_SHIPPED'">
                <Truck />
              </el-icon>
              <el-icon v-else>
                <Bell />
              </el-icon>
            </div>
            <div class="notification-content-item">
              <div class="notification-title">
                <span>{{ notification.title }}</span>
                <el-tag v-if="!notification.isRead" type="danger" size="small">未读</el-tag>
              </div>
              <div class="notification-text">{{ notification.content }}</div>
              <div class="notification-time">{{ formatDate(notification.createdAt) }}</div>
            </div>
            <div class="notification-actions">
              <el-button
                v-if="!notification.isRead"
                link
                type="primary"
                @click.stop="handleMarkRead(notification.id)"
              >
                标记已读
              </el-button>
              <el-button
                v-if="notification.relatedOrderNo && notification.type === 'ORDER_SHIPPED'"
                link
                type="primary"
                @click.stop="handleViewOrder(notification)"
              >
                查看订单
              </el-button>
            </div>
          </div>
        </div>

        <div v-if="notifications.length > 0" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Truck } from '@element-plus/icons-vue'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/notification'
import { showSuccess, showError } from '@/utils/message'

const router = useRouter()

const loading = ref(false)
const markingAll = ref(false)
const activeTab = ref('all')
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const unreadCount = ref(0)

const isReadFilter = computed(() => {
  if (activeTab.value === 'unread') return false
  if (activeTab.value === 'read') return true
  return null
})

async function loadNotifications() {
  loading.value = true
  try {
    const response = await getNotifications({
      page: currentPage.value,
      size: pageSize.value,
      isRead: isReadFilter.value
    })
    if (response.code === 0) {
      notifications.value = response.data.items || []
      total.value = response.data.total || 0
      unreadCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    showError('加载通知失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

async function handleMarkRead(id) {
  try {
    await markAsRead(id)
    showSuccess('已标记为已读')
    loadNotifications()
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

async function handleMarkAllRead() {
  markingAll.value = true
  try {
    await markAllAsRead()
    showSuccess('已全部标记为已读')
    loadNotifications()
  } catch (error) {
    console.error('全部标记已读失败:', error)
  } finally {
    markingAll.value = false
  }
}

function handleItemClick(notification) {
  if (!notification.isRead) {
    handleMarkRead(notification.id)
  }
  if (notification.relatedId && notification.type === 'ORDER_SHIPPED') {
    handleViewOrder(notification)
  }
}

function handleViewOrder(notification) {
  // 根据通知类型跳转到相应页面
  if (notification.type === 'ORDER_SHIPPED' && notification.relatedOrderNo) {
    router.push(`/orders/${notification.relatedOrderNo}`)
  } else {
    router.push('/orders')
  }
}

function handleTabChange() {
  currentPage.value = 1
  loadNotifications()
}

function handleSizeChange() {
  currentPage.value = 1
  loadNotifications()
}

function handlePageChange() {
  loadNotifications()
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
  min-height: 100vh;
  background-color: #f5f5f5;
}

.notification-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.notification-header h1 {
  margin: 0;
  font-size: 24px;
}

.notification-content {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.empty-state {
  padding: 40px 0;
}

.notification-items {
  margin-top: 20px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
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
  font-size: 24px;
  color: #409eff;
}

.notification-content-item {
  flex: 1;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 16px;
}

.notification-text {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

