<template>
  <div class="admin-user-detail" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>用户详情</h3>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <div v-if="userDetail" class="user-detail-content">
        <!-- 用户基本信息 -->
        <div class="section">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userDetail.nickname || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userDetail.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userDetail.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag :type="userDetail.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ userDetail.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userDetail.status === 'NORMAL' ? 'success' : 'danger'">
                {{ userDetail.status === 'NORMAL' ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(userDetail.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDate(userDetail.updatedAt) }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider />

        <!-- 统计信息 -->
        <div class="section">
          <h3>统计信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon total">
                    <el-icon><Document /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-label">订单总数</div>
                    <div class="stat-value">{{ userDetail.orderCount || 0 }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon completed">
                    <el-icon><CircleCheck /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-label">已完成订单</div>
                    <div class="stat-value">{{ userDetail.completedOrderCount || 0 }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="stat-card">
                <div class="stat-content">
                  <div class="stat-icon amount">
                    <el-icon><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-label">总消费金额</div>
                    <div class="stat-value">¥{{ ((userDetail.totalSpent || 0) / 100).toFixed(2) }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <!-- 购买记录 -->
        <div class="section">
          <h3>购买记录</h3>
          <el-table :data="userDetail.orders" border stripe style="width: 100%" v-if="userDetail.orders && userDetail.orders.length > 0">
            <el-table-column prop="orderNo" label="订单号" min-width="180" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="订单金额" width="120" align="center">
              <template #default="{ row }">
                ¥{{ row.totalAmount.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="下单时间" min-width="180" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleViewOrder(row.orderNo)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无购买记录" />
        </div>

        <el-divider />

        <!-- 浏览记录 -->
        <div class="section">
          <h3>浏览记录（基于购买商品推断）</h3>
          <div v-if="userDetail.browseRecords && userDetail.browseRecords.length > 0" class="browse-records">
            <el-row :gutter="20">
              <el-col :span="6" v-for="record in userDetail.browseRecords" :key="record.productId" style="margin-bottom: 20px;">
                <el-card shadow="hover" class="browse-record-card">
                  <div class="record-image">
                    <el-image
                      :src="record.productImageUrl || 'https://via.placeholder.com/150x150?text=No+Image'"
                      fit="cover"
                      style="width: 100%; height: 150px;"
                    />
                  </div>
                  <div class="record-info">
                    <h4 class="record-name">{{ record.productName }}</h4>
                    <p class="record-meta">
                      <span>浏览次数：{{ record.viewCount }}</span>
                      <br />
                      <span>最后浏览：{{ formatDate(record.lastViewTime) }}</span>
                    </p>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <el-empty v-else description="暂无浏览记录" />
        </div>
      </div>
      <el-empty v-else-if="!loading" description="用户不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, CircleCheck, Money } from '@element-plus/icons-vue'
import { getAdminUserDetail } from '@/api/admin/user'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const userDetail = ref(null)

function formatDate(dateStr) {
  if (!dateStr) return ''
  // 处理ISO格式的日期字符串，移除毫秒部分
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr
  
  // 格式化为：YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

function getStatusText(status) {
  const statusMap = {
    CREATED: '待支付',
    PAID: '待发货',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return statusMap[status] || status
}

function getStatusType(status) {
  const typeMap = {
    CREATED: 'warning',
    PAID: 'info',
    SHIPPED: 'success',
    COMPLETED: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return typeMap[status] || ''
}

function handleViewOrder(orderNo) {
  router.push(`/admin/orders/${orderNo}`)
}

async function loadUserDetail() {
  const userId = route.params.userId
  if (!userId) {
    router.push('/admin/users')
    return
  }

  loading.value = true
  try {
    const response = await getAdminUserDetail(userId)
    if (response.code === 0) {
      userDetail.value = response.data
    } else {
      router.push('/admin/users')
    }
  } catch (error) {
    console.error('加载用户详情失败:', error)
    router.push('/admin/users')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserDetail()
})
</script>

<style scoped>
.admin-user-detail {
  padding: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h3 {
  margin: 0;
}

.user-detail-content {
  padding: 20px 0;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #303133;
}

.browse-records {
  margin-top: 20px;
}

.browse-record-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.browse-record-card:hover {
  transform: translateY(-5px);
}

.record-image {
  margin-bottom: 10px;
}

.record-info {
  padding: 10px 0;
}

.record-name {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-meta {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
}

.stat-card {
  height: 100%;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #f56c6c 0%, #ff7875 100%);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
</style>

