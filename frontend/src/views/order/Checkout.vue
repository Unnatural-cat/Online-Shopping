<template>
  <CustomerLayout>
    <div class="checkout" v-loading="loading">
      <h1>订单确认</h1>
        <el-card>
          <template #header>
            <div class="address-header">
              <h3>收货地址</h3>
              <el-button type="primary" link @click="handleManageAddress">管理地址</el-button>
            </div>
          </template>
          <div v-if="addresses.length === 0" class="address-empty">
            <el-empty description="暂无收货地址">
              <el-button type="primary" @click="handleManageAddress">去添加地址</el-button>
            </el-empty>
          </div>
          <el-table 
            v-else 
            :data="addresses" 
            border 
            class="address-table" 
            @row-click="handleAddressRowClick"
            :row-class-name="getRowClassName"
          >
            <el-table-column label="收货人" width="120" align="center" class-name="receiver-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="receiver">{{ row.receiverName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="联系电话" width="150" align="center" class-name="phone-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="phone">{{ row.receiverPhone }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="收货地址" min-width="300" align="left" class-name="address-column">
              <template #default="{ row }">
                <div class="address-detail">
                  {{ row.province || '' }} {{ row.city || '' }} {{ row.district || '' }} {{ row.detailAddress || '' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center" class-name="status-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <el-tag v-if="row.isDefault" type="danger" size="small" style="font-size: 11px; padding: 2px 6px; line-height: 1.2;">默认</el-tag>
                  <span v-else style="color: #909399; font-size: 13px;">-</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <h3>商品信息</h3>
          </template>
          <el-table :data="orderItems" border class="checkout-table">
            <el-table-column label="商品" min-width="450" align="left" class-name="product-column">
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
            <el-table-column label="数量" width="160" align="center" class-name="quantity-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="quantity-text">{{ row.quantity }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="小计" width="160" align="center" class-name="subtotal-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="subtotal">¥{{ (Number(row.productPrice) * row.quantity).toFixed(2) }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card style="margin-top: 20px;">
          <div class="order-summary">
            <div class="summary-row">
              <span>商品总价：</span>
              <span>¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="summary-row total">
              <span>应付总额：</span>
              <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="checkout-actions">
            <el-button 
              size="large" 
              @click="$router.back()"
              class="back-button"
            >
              返回
            </el-button>
            <el-button 
              type="primary" 
              size="large" 
              @click="handleSubmit" 
              :disabled="!selectedAddressId"
              class="submit-button"
            >
              提交订单
            </el-button>
          </div>
        </el-card>
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAddresses } from '@/api/user'
import { getCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { showSuccess, showError } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const addresses = ref([])
const selectedAddressId = ref(null)
const orderItems = ref([])

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (Number(item.productPrice) || 0) * item.quantity
  }, 0)
})

async function loadAddresses() {
  try {
    const response = await getAddresses()
    if (response.code === 0) {
      addresses.value = response.data || []
      const defaultAddress = addresses.value.find(addr => addr.isDefault)
      if (defaultAddress) {
        selectedAddressId.value = defaultAddress.id
      } else if (addresses.value.length > 0) {
        selectedAddressId.value = addresses.value[0].id
      }
    }
  } catch (error) {
    console.error('加载地址失败:', error)
  }
}

async function loadCartItems() {
  try {
    const response = await getCart()
    if (response.code === 0) {
      const checkedItems = (response.data.items || []).filter(item => item.checked)
      if (checkedItems.length === 0) {
        showError('请先选择要结算的商品')
        router.push('/cart')
        return
      }
      orderItems.value = checkedItems
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
  }
}

function handleManageAddress() {
  router.push({
    path: '/addresses',
    query: { returnUrl: '/checkout' }
  })
}

function handleAddressRowClick(row) {
  selectedAddressId.value = row.id
}

function getRowClassName({ row, rowIndex }) {
  if (selectedAddressId.value === row.id) {
    return 'selected-row'
  }
  return ''
}

async function handleSubmit() {
  if (!selectedAddressId.value) {
    showError('请选择收货地址')
    return
  }

  loading.value = true
  try {
    const selectedAddress = addresses.value.find(addr => addr.id === selectedAddressId.value)
    if (!selectedAddress) {
      showError('收货地址不存在')
      return
    }

    // 构建完整地址字符串
    const fullAddress = `${selectedAddress.province || ''} ${selectedAddress.city || ''} ${selectedAddress.district || ''} ${selectedAddress.detailAddress || ''}`.trim()

    const response = await createOrder({
      addressId: selectedAddressId.value,
      cartItemIds: orderItems.value.map(item => item.id),
      receiverName: selectedAddress.receiverName,
      receiverPhone: selectedAddress.receiverPhone,
      receiverAddress: fullAddress
    })
    if (response.code === 0) {
      showSuccess('订单创建成功')
      router.push(`/payment/${response.data.orderNo}`)
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    if (error.response?.data?.message) {
      showError(error.response.data.message)
    } else {
      showError('创建订单失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadAddresses()
  await loadCartItems()
})

// 监听路由变化，当从地址管理页面返回时刷新地址列表
watch(() => route.name, async (newName, oldName) => {
  // 如果从地址管理页面返回到订单确认页面，刷新地址列表
  if (oldName === 'Addresses' && newName === 'Checkout') {
    await loadAddresses()
  }
}, { flush: 'post' })
</script>

<style scoped>
.checkout {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.checkout h1 {
  margin: 0 0 20px 0;
  font-size: 24px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.address-empty {
  padding: 20px 0;
}

/* 地址表格样式 */
.address-table {
  width: 100%;
}

.address-table :deep(.el-table) {
  width: 100%;
  table-layout: fixed;
}

.address-table :deep(.el-table th),
.address-table :deep(.el-table td) {
  vertical-align: middle !important;
  padding: 6px 10px;
  text-align: center;
}

.address-table :deep(.el-table__cell) {
  vertical-align: middle !important;
  padding: 6px 10px;
  height: auto;
  min-height: 32px;
  text-align: center;
}

.address-table :deep(.el-table th) {
  text-align: center;
  background-color: #fafafa;
  font-weight: 600;
  color: #606266;
  padding: 6px 10px;
  font-size: 13px;
}

/* 地址列保持左对齐 */
.address-table :deep(.address-column) {
  text-align: left !important;
}

/* 单元格内容居中容器 */
.cell-content-center {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 32px;
}

.address-table :deep(.address-column .cell-content-center) {
  justify-content: flex-start;
}

/* 表格行样式 */
.address-table :deep(.el-table__row) {
  height: auto;
  min-height: 32px;
  cursor: pointer;
}

.address-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.address-table :deep(.el-table__row:hover td) {
  background-color: #f5f7fa !important;
}

/* 选中行高亮 - 增强颜色深度 */
.address-table :deep(.el-table__row.selected-row) {
  background-color: #b3d8ff !important;
  border: 3px solid #409eff !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3) !important;
}

.address-table :deep(.el-table__row.selected-row td) {
  background-color: #b3d8ff !important;
  border-color: #409eff !important;
}

.address-table :deep(.el-table__row.selected-row:hover) {
  background-color: #a0cfff !important;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.4) !important;
}

.address-table :deep(.el-table__row.selected-row:hover td) {
  background-color: #a0cfff !important;
}

.receiver {
  font-weight: 600;
  font-size: 13px;
  color: #303133;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
  line-height: 1.4;
}

.phone {
  color: #606266;
  font-size: 13px;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
  line-height: 1.4;
}

.address-detail {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.4;
  word-break: break-all;
  text-align: left;
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
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
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
}

.checkout-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.checkout-actions .el-button {
  height: 40px;
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  line-height: 1;
  margin: 0;
}

.back-button {
  min-width: 100px;
}

.submit-button {
  min-width: 120px;
}

/* 表格样式优化 */
.checkout-table {
  width: 100%;
}

.checkout-table :deep(.el-table) {
  width: 100%;
  table-layout: fixed;
}

/* 确保表格内容垂直居中 */
.checkout-table :deep(.el-table th),
.checkout-table :deep(.el-table td) {
  vertical-align: middle !important;
  padding: 16px 12px;
  text-align: center;
}

.checkout-table :deep(.el-table__cell) {
  vertical-align: middle !important;
  padding: 16px 12px;
  height: auto;
  min-height: 100px;
  text-align: center;
}

.checkout-table :deep(.el-table th) {
  text-align: center;
  background-color: #fafafa;
  font-weight: 600;
  color: #606266;
}

/* 商品列保持左对齐 */
.checkout-table :deep(.product-column) {
  text-align: left !important;
}

.checkout-table :deep(.product-column .cell-content-center) {
  justify-content: flex-start;
}

/* 单元格内容居中容器 */
.cell-content-center {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 100px;
}

/* 数量文字样式 */
.quantity-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
}

/* 小计样式 */
.subtotal {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
}

/* 表格行样式 */
.checkout-table :deep(.el-table__row) {
  height: auto;
  min-height: 100px;
}

.checkout-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.checkout-table :deep(.el-table__row:hover td) {
  background-color: #f5f7fa !important;
}
</style>
