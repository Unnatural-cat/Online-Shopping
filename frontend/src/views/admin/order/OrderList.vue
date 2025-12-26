<template>
  <div class="admin-order-list" v-loading="loading">
    <el-card>
      <template #header>
        <h3>订单管理</h3>
      </template>

      <div class="filter-bar">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="订单号">
            <el-input
              v-model="queryParams.orderNo"
              placeholder="请输入订单号"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" clearable placeholder="全部" @change="handleSearch">
              <el-option label="待支付" value="CREATED" />
              <el-option label="待发货" value="PAID" />
              <el-option label="已发货" value="SHIPPED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户ID">
            <el-input-number
              v-model="queryParams.userId"
              :min="1"
              placeholder="请输入用户ID"
              clearable
              @clear="handleSearch"
              style="width: 150px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="orders" border>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="receiverName" label="收货人" width="120" />
        <el-table-column prop="receiverPhone" label="联系电话" width="130" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewDetail(row.orderNo)">
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 'PAID'"
              link
              type="success"
              @click="handleShip(row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination
        v-if="total > 0"
        :total="total"
        :page="queryParams.page"
        :size="queryParams.size"
        @change="handlePageChange"
      />
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
import { useRouter } from 'vue-router'
import { getAdminOrders, shipOrder } from '@/api/admin/order'
import { showSuccess, showError } from '@/utils/message'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()

const loading = ref(false)
const shipping = ref(false)
const orders = ref([])
const total = ref(0)
const shipDialogVisible = ref(false)
const shipFormRef = ref(null)
const currentOrderNo = ref('')

const queryParams = reactive({
  page: 1,
  size: 20,
  orderNo: '',
  status: '',
  userId: null
})

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

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.orderNo) params.orderNo = queryParams.orderNo
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.userId) params.userId = queryParams.userId

    const response = await getAdminOrders(params)
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

function resetFilters() {
  queryParams.orderNo = ''
  queryParams.status = ''
  queryParams.userId = null
  queryParams.page = 1
  loadOrders()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadOrders()
}

function handleViewDetail(orderNo) {
  router.push(`/admin/orders/${orderNo}`)
}

function handleShip(row) {
  currentOrderNo.value = row.orderNo
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
        await shipOrder(currentOrderNo.value, shipForm)
        showSuccess('发货成功')
        shipDialogVisible.value = false
        loadOrders()
      } catch (error) {
        console.error('发货失败:', error)
      } finally {
        shipping.value = false
      }
    }
  })
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.admin-order-list {
  height: 100%;
}

.filter-bar {
  margin-bottom: 20px;
}
</style>
