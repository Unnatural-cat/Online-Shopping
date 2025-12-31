<template>
  <CustomerLayout>
    <div class="order-list page-container" v-loading="loading">
      <div class="page-header">
      <h1>我的订单</h1>
      </div>
      <el-card class="card-container">
          <template #header>
            <div class="filter-header">
              <span>订单状态：</span>
              <el-radio-group v-model="queryParams.status" @change="handleSearch">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="CREATED">待支付</el-radio-button>
                <el-radio-button label="PAID">待发货</el-radio-button>
                <el-radio-button label="SHIPPED">已发货</el-radio-button>
                <el-radio-button label="COMPLETED">已完成</el-radio-button>
                <el-radio-button label="CANCELLED">已取消</el-radio-button>
              </el-radio-group>
            </div>
          </template>

          <el-empty v-if="orders.length === 0 && !loading" description="暂无订单">
            <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
          </el-empty>

          <div v-else class="order-list-content">
            <el-card
              v-for="order in orders"
              :key="order.orderNo"
              class="order-card"
              shadow="hover"
            >
              <div class="order-header">
                <div class="order-info">
                  <span>订单号：{{ order.orderNo }}</span>
                  <span>下单时间：{{ formatDate(order.createdAt) }}</span>
                </div>
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
              <div class="order-items">
                <div
                  v-for="item in order.items"
                  :key="item.id"
                  class="order-item"
                  @click="$router.push(`/orders/${order.orderNo}`)"
                >
                  <img
                    :src="item.productImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
                    :alt="item.productName"
                    class="item-image"
                  />
                  <div class="item-info">
                    <h4>{{ item.productName }}</h4>
                    <p>¥{{ item.productPrice }} × {{ item.quantity }}</p>
                  </div>
                </div>
              </div>
              <div class="order-footer">
                <div class="order-total">
                  合计：<span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span>
                </div>
                <div class="order-actions">
                  <el-button
                    v-if="order.status === 'CREATED'"
                    @click="handleCancel(order.orderNo)"
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
                    type="primary"
                    @click="$router.push(`/orders/${order.orderNo}`)"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>

        <div class="pagination-container">
          <Pagination
            v-if="total > 0"
            :total="total"
            :page="queryParams.page"
            :size="queryParams.size"
            @change="handlePageChange"
          />
        </div>
        </el-card>
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrders, cancelOrder } from '@/api/order'
import { showSuccess, showError, confirm } from '@/utils/message'
import Pagination from '@/components/Pagination.vue'
import CustomerLayout from '@/components/CustomerLayout.vue'

const loading = ref(false)
const orders = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  status: ''
})

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

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.status) {
      params.status = queryParams.status
    }

    const response = await getOrders(params)
    if (response.code === 0) {
      orders.value = response.data.content || []
      total.value = response.data.totalElements || 0
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadOrders()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadOrders()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function handleCancel(orderNo) {
  try {
    await confirm('确定要取消这个订单吗？')
    await cancelOrder(orderNo)
    showSuccess('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-list {
  /* 使用全局样式 */
}

.filter-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.order-list-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.order-items {
  margin-bottom: 15px;
}

.order-item {
  display: flex;
  gap: 15px;
  padding: 10px 0;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 5px 0;
  color: #303133;
}

.item-info p {
  margin: 0;
  color: #909399;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.order-total {
  font-size: 16px;
}

.total-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: var(--button-gap);
  align-items: center;
}
</style>
