<template>
  <div class="admin-order-statistics" v-loading="loading">
    <el-card>
      <template #header>
        <h3>订单统计</h3>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon total">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">订单总数</div>
                <div class="stat-value">{{ statistics.totalOrders }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon amount">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">总销售额</div>
                <div class="stat-value">¥{{ statistics.totalAmount.toFixed(2) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon completed">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">已完成订单</div>
                <div class="stat-value">{{ statistics.completedOrders }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon avg">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-label">平均订单金额</div>
                <div class="stat-value">¥{{ statistics.averageAmount.toFixed(2) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 订单状态分布 -->
      <el-card class="section-card">
        <template #header>
          <div class="section-header">
            <h3>订单状态分布</h3>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleDateChange"
              class="date-picker-compact"
              style="width: 280px;"
            />
          </div>
        </template>
        <div class="status-distribution">
          <el-empty v-if="statusDistribution.length === 0" description="暂无数据" />
          <div v-else>
            <div
              v-for="(item, index) in statusDistribution"
              :key="index"
              class="status-item"
            >
              <div class="status-header">
                <el-tag :type="getStatusType(item.status)" size="large">
                  {{ getStatusText(item.status) }}
                </el-tag>
                <span class="status-count">{{ item.count }} 单</span>
              </div>
              <el-progress
                :percentage="item.percentage"
                :color="getStatusColor(item.status)"
                :show-text="false"
              />
            </div>
          </div>
        </div>
      </el-card>

      <!-- 订单趋势 -->
      <el-card class="section-card">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <h3>订单趋势</h3>
          </div>
        </template>
        <div class="trend-chart">
          <el-empty v-if="trendData.length === 0" description="暂无数据" />
          <div v-else class="trend-list">
            <div
              v-for="(item, index) in trendData"
              :key="index"
              class="trend-item"
            >
              <div class="trend-date">{{ item.date }}</div>
              <div class="trend-bar">
                <div
                  class="trend-bar-fill"
                  :style="{ width: item.percentage + '%' }"
                ></div>
              </div>
              <div class="trend-value">{{ item.count }} 单</div>
            </div>
          </div>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAdminOrders } from '@/api/admin/order'
import { Document, Money, CircleCheck, TrendCharts } from '@element-plus/icons-vue'

const loading = ref(false)
const dateRange = ref(null)
const allOrders = ref([])

const statistics = reactive({
  totalOrders: 0,
  totalAmount: 0,
  completedOrders: 0,
  averageAmount: 0
})

const statusDistribution = ref([])
const trendData = ref([])

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

function getStatusColor(status) {
  const colorMap = {
    CREATED: '#e6a23c',
    PAID: '#409eff',
    SHIPPED: '#67c23a',
    COMPLETED: '#67c23a',
    CANCELLED: '#909399',
    REFUNDED: '#f56c6c'
  }
  return colorMap[status] || '#409eff'
}

async function loadAllOrders() {
  loading.value = true
  try {
    // 获取所有订单（不限制数量）
    let page = 1
    const pageSize = 100
    let hasMore = true
    const orders = []

    while (hasMore) {
      const params = {
        page,
        size: pageSize
      }
      
      const response = await getAdminOrders(params)
      
      if (response.code === 0 && response.data) {
        const currentOrders = response.data.content || []
        orders.push(...currentOrders)
        
        const totalElements = response.data.totalElements || 0
        hasMore = currentOrders.length === pageSize && orders.length < totalElements
        page++
      } else {
        hasMore = false
      }
    }

    // 应用日期筛选
    let filteredOrders = orders
    if (dateRange.value && dateRange.value.length === 2) {
      const startDate = new Date(dateRange.value[0])
      const endDate = new Date(dateRange.value[1])
      endDate.setHours(23, 59, 59, 999)
      
      filteredOrders = orders.filter(order => {
        const orderDate = new Date(order.createdAt)
        return orderDate >= startDate && orderDate <= endDate
      })
    }

    allOrders.value = filteredOrders
    calculateStatistics()
  } catch (error) {
    console.error('加载订单数据失败:', error)
  } finally {
    loading.value = false
  }
}

function calculateStatistics() {
  const orders = allOrders.value

  // 基础统计
  statistics.totalOrders = orders.length
  statistics.totalAmount = orders.reduce((sum, order) => sum + Number(order.totalAmount || 0), 0)
  statistics.completedOrders = orders.filter(order => order.status === 'COMPLETED').length
  statistics.averageAmount = statistics.totalOrders > 0 
    ? statistics.totalAmount / statistics.totalOrders 
    : 0

  // 状态分布
  const statusCount = {}
  orders.forEach(order => {
    statusCount[order.status] = (statusCount[order.status] || 0) + 1
  })

  statusDistribution.value = Object.entries(statusCount).map(([status, count]) => ({
    status,
    count,
    percentage: statistics.totalOrders > 0 ? (count / statistics.totalOrders) * 100 : 0
  })).sort((a, b) => b.count - a.count)

  // 订单趋势（按日期统计）
  const dateCount = {}
  orders.forEach(order => {
    const date = new Date(order.createdAt).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
    dateCount[date] = (dateCount[date] || 0) + 1
  })

  const sortedDates = Object.keys(dateCount).sort((a, b) => {
    return new Date(a) - new Date(b)
  })

  const maxCount = Math.max(...Object.values(dateCount), 1)

  trendData.value = sortedDates.map(date => ({
    date,
    count: dateCount[date],
    percentage: (dateCount[date] / maxCount) * 100
  }))
}

function handleDateChange() {
  loadAllOrders()
}

onMounted(() => {
  loadAllOrders()
})
</script>

<style scoped>
.admin-order-statistics {
  height: 100%;
  width: 100%;
}

.admin-order-statistics :deep(.el-card) {
  width: 100%;
}

.admin-order-statistics :deep(.el-card__body) {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
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
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.avg {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
  font-weight: bold;
  color: #303133;
}

.section-card {
  margin-bottom: 20px;
}

.section-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  gap: 800px;
}

.section-header h3 {
  margin: 0;
  flex-shrink: 0;
}

.date-picker-compact {
  width: 280px !important;
  max-width: 280px !important;
  flex-shrink: 0;
  margin-left: auto;
}

.date-picker-compact :deep(.el-input__wrapper) {
  width: 100% !important;
  max-width: 100% !important;
}

.date-picker-compact :deep(.el-range-input) {
  width: auto !important;
  min-width: 0 !important;
  flex: 1 1 auto !important;
}

.date-picker-compact :deep(.el-range-separator) {
  width: auto !important;
  padding: 0 8px !important;
  flex-shrink: 0;
}

/* 确保日期选择器整体宽度限制 */
.date-picker-compact :deep(.el-date-editor) {
  width: 280px !important;
  max-width: 280px !important;
}

.status-distribution {
  padding: 10px 0;
}

.status-item {
  margin-bottom: 20px;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.status-count {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.trend-chart {
  padding: 10px 0;
}

.trend-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.trend-date {
  width: 100px;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}

.trend-bar {
  flex: 1;
  height: 24px;
  background-color: #f0f2f5;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.trend-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
  transition: width 0.3s ease;
  border-radius: 12px;
}

.trend-value {
  width: 60px;
  text-align: right;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .trend-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .trend-bar {
    width: 100%;
  }

  .trend-value {
    width: auto;
    text-align: left;
  }
}
</style>

