<template>
  <div class="order-detail" v-loading="loading">
    <div class="order-detail-container">
      <div class="order-detail-header">
        <h1>订单详情</h1>
        <el-button @click="$router.push('/orders')">返回订单列表</el-button>
      </div>
      <div class="order-detail-content">
        <el-card v-if="order">
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
            <el-table :data="order.items" border>
              <el-table-column label="商品" width="400">
                <template #default="{ row }">
                  <div class="product-cell">
                    <img
                      :src="row.productImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
                      :alt="row.productName"
                      class="product-image"
                    />
                    <div class="product-info">
                      <h4>{{ row.productName }}</h4>
                      <p class="product-price">¥{{ row.productPrice }}</p>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="数量" width="100">
                <template #default="{ row }">
                  {{ row.quantity }}
                </template>
              </el-table-column>
              <el-table-column label="小计" width="120">
                <template #default="{ row }">
                  <span class="subtotal">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
                </template>
              </el-table-column>
            </el-table>
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
          </div>
        </el-card>
        <el-empty v-else-if="!loading" description="订单不存在" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder } from '@/api/order'
import { showSuccess, showError, confirm } from '@/utils/message'

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

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.order-detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.order-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.order-detail-header h1 {
  margin: 0;
  font-size: 24px;
}

.order-detail-content {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.order-detail h1 {
  margin: 0;
  font-size: 24px;
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

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info h4 {
  margin: 0 0 5px 0;
}

.product-price {
  margin: 0;
  color: #f56c6c;
  font-weight: bold;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
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
  margin-top: 30px;
  text-align: right;
}
</style>
