<template>
  <div class="checkout" v-loading="loading">
    <div class="checkout-container">
      <div class="checkout-header">
        <h1>订单确认</h1>
      </div>
      <div class="checkout-content">
        <el-card>
          <template #header>
            <h3>收货地址</h3>
          </template>
          <el-radio-group v-model="selectedAddressId">
            <el-radio
              v-for="address in addresses"
              :key="address.id"
              :label="address.id"
              class="address-radio"
            >
              <div class="address-info">
                <span class="receiver">{{ address.receiverName }}</span>
                <span class="phone">{{ address.receiverPhone }}</span>
                <el-tag v-if="address.isDefault" type="danger" size="small">默认</el-tag>
                <p class="address-detail">
                  {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
                </p>
              </div>
            </el-radio>
          </el-radio-group>
          <el-button type="primary" link @click="$router.push('/addresses')">管理地址</el-button>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <h3>商品信息</h3>
          </template>
          <el-table :data="orderItems" border>
            <el-table-column label="商品" width="400">
              <template #default="{ row }">
                <div class="product-cell">
                  <img
                    :src="row.product?.coverImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
                    :alt="row.product?.name"
                    class="product-image"
                  />
                  <div class="product-info">
                    <h4>{{ row.product?.name }}</h4>
                    <p class="product-price">¥{{ row.product?.price }}</p>
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
                <span class="subtotal">¥{{ (row.product?.price * row.quantity).toFixed(2) }}</span>
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
            <el-button @click="$router.back()">返回</el-button>
            <el-button type="primary" size="large" @click="handleSubmit" :disabled="!selectedAddressId">
              提交订单
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAddresses } from '@/api/user'
import { getCart } from '@/api/cart'
import { createOrder } from '@/api/order'
import { showSuccess, showError } from '@/utils/message'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const addresses = ref([])
const selectedAddressId = ref(null)
const orderItems = ref([])

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
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

async function handleSubmit() {
  if (!selectedAddressId.value) {
    showError('请选择收货地址')
    return
  }

  loading.value = true
  try {
    const selectedAddress = addresses.value.find(addr => addr.id === selectedAddressId.value)
    const response = await createOrder({
      addressId: selectedAddressId.value,
      items: orderItems.value.map(item => ({
        cartItemId: item.id,
        quantity: item.quantity
      }))
    })
    if (response.code === 0) {
      showSuccess('订单创建成功')
      router.push(`/payment/${response.data.orderNo}`)
    }
  } catch (error) {
    console.error('创建订单失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadAddresses()
  await loadCartItems()
})
</script>

<style scoped>
.checkout {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.checkout-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.checkout-header {
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.checkout-header h1 {
  margin: 0;
  font-size: 24px;
}

.checkout-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.address-radio {
  display: block;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.address-radio:hover {
  border-color: #409eff;
}

.address-info {
  margin-left: 10px;
}

.receiver {
  font-weight: bold;
  margin-right: 10px;
}

.phone {
  margin-right: 10px;
}

.address-detail {
  margin: 5px 0 0 0;
  color: #606266;
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
  gap: 10px;
  margin-top: 20px;
}
</style>

