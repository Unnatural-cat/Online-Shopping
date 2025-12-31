<template>
  <CustomerLayout>
    <div class="order-detail page-container" v-loading="loading">
      <div class="page-header">
        <h1>订单详情</h1>
        <el-button @click="$router.push('/orders')">返回订单列表</el-button>
      </div>
      <el-card v-if="order" class="card-container">
          <div class="order-header">
            <div class="order-info">
              <p>订单号：{{ order.orderNo }}</p>
              <p>下单时间：{{ formatDate(order.createdAt) }}</p>
              <p v-if="order.paidAt">支付时间：{{ formatDate(order.paidAt) }}</p>
              <p v-if="order.shippedAt">发货时间：{{ formatDate(order.shippedAt) }}</p>
            </div>
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <el-divider />

          <div class="section">
            <h3>收货信息</h3>
            <p>收货人：{{ order.receiverName }}</p>
            <p>联系电话：{{ order.receiverPhone }}</p>
            <p>收货地址：{{ order.receiverAddress }}</p>
          </div>

          <el-divider />

          <div class="section">
            <h3>商品信息</h3>
            <div class="table-container">
              <el-table :data="order.items" border stripe class="order-items-table">
              <el-table-column label="商品" min-width="400">
                <template #default="{ row }">
                  <div class="product-cell">
                    <img
                      :src="row.productImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
                      :alt="row.productName"
                      class="product-image"
                    />
                    <div class="product-info">
                      <h4 class="product-name">{{ row.productName }}</h4>
                      <p class="product-price">¥{{ row.productPrice }}</p>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="120" align="center">
                <template #default="{ row }">
                  <div class="cell-content-center">
                    <span class="quantity-text">{{ row.quantity }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="小计" width="140" align="center">
                <template #default="{ row }">
                  <div class="cell-content-center">
                    <span class="subtotal">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            </div>
          </div>

          <el-divider />

          <div class="section">
            <div class="order-summary">
              <div class="summary-row">
                <span>商品总价：</span>
                <span>¥{{ order.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="summary-row total">
                <span>应付总额：</span>
                <span class="total-price">¥{{ order.payAmount.toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <div v-if="order.statusLogs && order.statusLogs.length > 0" class="section">
            <el-divider />
            <h3>订单状态记录</h3>
            <el-timeline>
              <el-timeline-item
                v-for="log in order.statusLogs"
                :key="log.id"
                :timestamp="formatDate(log.createdAt)"
              >
                <p>{{ log.remark || `${getStatusText(log.fromStatus)} → ${getStatusText(log.toStatus)}` }}</p>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div class="order-actions">
            <el-button
              v-if="order.status === 'CREATED'"
              @click="handleCancel"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 'CREATED'"
              type="primary"
              @click="$router.push(`/payment/${order.orderNo}`)"
            >
              去支付
            </el-button>
            <el-button
              v-if="order.status === 'SHIPPED'"
              type="success"
              @click="handleComplete"
            >
              完成订单
            </el-button>
          </div>
        </el-card>
        <el-empty v-else-if="!loading" description="订单不存在" />
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder, completeOrder } from '@/api/order'
import { showSuccess, showError, confirm } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref(null)

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
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

async function loadOrderDetail() {
  const orderNo = route.params.orderNo
  if (!orderNo) {
    router.push('/orders')
    return
  }

  loading.value = true
  try {
    const response = await getOrderDetail(orderNo)
    if (response.code === 0) {
      order.value = response.data
    } else {
      showError('订单不存在')
      router.push('/orders')
    }
  } catch (error) {
    showError('加载订单详情失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

async function handleCancel() {
  try {
    await confirm('确定要取消这个订单吗？')
    await cancelOrder(order.value.orderNo)
    showSuccess('订单已取消')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
    }
  }
}

async function handleComplete() {
  try {
    await confirm('确认已收到商品，完成订单吗？')
    await completeOrder(order.value.orderNo)
    showSuccess('订单已完成')
    loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成订单失败:', error)
    }
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail {
  /* 使用全局样式 */
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.order-info p {
  margin: 5px 0;
  color: #606266;
}

.section {
  margin: 20px 0;
}

.section h3 {
  margin-bottom: 15px;
  font-size: 18px;
}

.order-items-table {
  width: 100%;
}

.order-items-table :deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

/* 确保表格内容垂直居中 */
.order-items-table :deep(.el-table th),
.order-items-table :deep(.el-table td) {
  vertical-align: middle !important;
}

.order-items-table :deep(.el-table__cell) {
  vertical-align: middle !important;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-price {
  margin: 0;
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

/* 单元格内容居中容器 */
.cell-content-center {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.quantity-text {
  color: #606266;
  font-size: 14px;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.order-summary {
  text-align: right;
}

.summary-row {
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.summary-row.total {
  border-bottom: none;
  margin-top: 10px;
  font-size: 18px;
}

.total-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 24px;
  margin-left: 20px;
}

.order-actions {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
  gap: var(--button-gap);
}
</style>
