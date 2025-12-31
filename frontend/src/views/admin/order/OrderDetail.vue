<template>
  <div class="admin-order-detail" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>订单详情</h3>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <div v-if="order">
        <div class="order-header">
          <div class="order-info">
            <p>订单号：{{ order.orderNo }}</p>
            <p>用户ID：{{ order.userId }}</p>
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

        <div class="order-actions" v-if="order.status === 'PAID'">
          <el-button type="primary" @click="handleShip">发货</el-button>
        </div>
      </div>
      <el-empty v-else-if="!loading" description="订单不存在" />
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
        <el-form-item label="物流单号" prop="trackingNo">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="shipForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleShipSubmit" :loading="shipping">
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAdminOrderDetail, shipOrder } from '@/api/admin/order'
import { showSuccess, showError } from '@/utils/message'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const shipping = ref(false)
const order = ref(null)
const shipDialogVisible = ref(false)
const shipFormRef = ref(null)

const shipForm = reactive({
  trackingNo: '',
  remark: ''
})

const shipRules = {
  trackingNo: [
    { required: true, message: '请输入物流单号', trigger: 'blur' }
  ]
}

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
    router.back()
    return
  }

  loading.value = true
  try {
    const response = await getAdminOrderDetail(orderNo)
    if (response.code === 0) {
      order.value = response.data
    } else {
      showError('订单不存在')
      router.back()
    }
  } catch (error) {
    showError('加载订单详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

function handleShip() {
  shipForm.trackingNo = ''
  shipForm.remark = ''
  shipDialogVisible.value = true
}

async function handleShipSubmit() {
  if (!shipFormRef.value) return

  await shipFormRef.value.validate(async (valid) => {
    if (valid) {
      shipping.value = true
      try {
        await shipOrder(order.value.orderNo, shipForm)
        showSuccess('发货成功')
        shipDialogVisible.value = false
        loadOrderDetail()
      } catch (error) {
        console.error('发货失败:', error)
      } finally {
        shipping.value = false
      }
    }
  })
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.admin-order-detail {
  height: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* 确保表格内容垂直居中 */
.order-items-table :deep(.el-table th),
.order-items-table :deep(.el-table td) {
  vertical-align: middle !important;
}

.order-items-table :deep(.el-table__cell) {
  vertical-align: middle !important;
}
</style>
