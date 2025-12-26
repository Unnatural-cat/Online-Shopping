<template>
  <div class="cart" v-loading="loading">
    <div class="cart-container">
      <div class="cart-header">
        <h1>购物车</h1>
      </div>
      <div class="cart-content">
        <el-card v-if="cartItems.length === 0 && !loading" shadow="never">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
          </el-empty>
        </el-card>

        <div v-else>
          <el-table :data="cartItems" border>
            <el-table-column type="selection" width="55" />
            <el-table-column label="商品" width="400">
              <template #default="{ row }">
                <div class="product-cell">
                  <el-checkbox
                    v-model="row.checked"
                    @change="handleCheckChange(row)"
                  />
                  <img
                    :src="row.productImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
                    :alt="row.productName"
                    class="product-image"
                    @click="$router.push(`/products/${row.productId}`)"
                  />
                  <div class="product-info">
                    <h4 @click="$router.push(`/products/${row.productId}`)" class="product-name">
                      {{ row.productName }}
                    </h4>
                    <p class="product-price">¥{{ row.productPrice }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.productPrice }}
              </template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.quantity"
                  :min="1"
                  :max="row.stock || 999"
                  @change="handleQuantityChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ (parseFloat(row.subtotal) || 0).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="cart-footer">
            <div class="cart-actions">
              <el-button @click="handleSelectAll">全选</el-button>
              <el-button @click="handleUnselectAll">取消全选</el-button>
              <el-button type="danger" @click="handleClear">清空购物车</el-button>
            </div>
            <div class="cart-summary">
              <div class="summary-item">
                <span>已选商品：{{ selectedCount }} 件</span>
              </div>
              <div class="summary-item">
                <span>合计：</span>
                <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
              </div>
              <el-button
                type="primary"
                size="large"
                @click="handleCheckout"
                :disabled="selectedCount === 0"
              >
                去结算
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, deleteCartItem, clearCart } from '@/api/cart'
import { showSuccess, showError, confirm } from '@/utils/message'

const router = useRouter()

const loading = ref(false)
const cartItems = ref([])

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.checked).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.checked)
    .reduce((sum, item) => {
      return sum + (parseFloat(item.subtotal) || 0)
    }, 0)
})

async function loadCart() {
  loading.value = true
  try {
    const response = await getCart()
    if (response.code === 0) {
      cartItems.value = response.data.items || []
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
    // 如果是401或403，说明未登录，跳转到登录页
    if (error.response?.status === 401 || error.response?.status === 403) {
      // 不显示错误提示，因为路由守卫会处理
      router.push('/login')
    } else {
      showError('加载购物车失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

async function handleQuantityChange(item) {
  try {
    await updateCartItem(item.id, {
      quantity: item.quantity,
      checked: item.checked
    })
    // 更新小计
    item.subtotal = (parseFloat(item.productPrice) * item.quantity).toFixed(2)
  } catch (error) {
    loadCart() // 重新加载以恢复原值
  }
}

async function handleCheckChange(item) {
  try {
    await updateCartItem(item.id, {
      quantity: item.quantity,
      checked: item.checked
    })
  } catch (error) {
    loadCart()
  }
}

async function handleDelete(id) {
  try {
    await confirm('确定要删除这个商品吗？')
    await deleteCartItem(id)
    showSuccess('删除成功')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

async function handleClear() {
  try {
    await confirm('确定要清空购物车吗？')
    await clearCart()
    showSuccess('清空成功')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

function handleSelectAll() {
  cartItems.value.forEach(item => {
    if (!item.checked) {
      item.checked = true
      handleCheckChange(item)
    }
  })
}

function handleUnselectAll() {
  cartItems.value.forEach(item => {
    if (item.checked) {
      item.checked = false
      handleCheckChange(item)
    }
  })
}

function handleCheckout() {
  const selectedItems = cartItems.value.filter(item => item.checked)
  if (selectedItems.length === 0) {
    showError('请选择要结算的商品')
    return
  }
  router.push('/checkout')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.cart-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.cart-header {
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.cart-header h1 {
  margin: 0;
  font-size: 24px;
}

.cart-content {
  background: white;
  padding: 20px;
  border-radius: 4px;
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
  cursor: pointer;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 5px 0;
  cursor: pointer;
  color: #303133;
}

.product-name:hover {
  color: #409eff;
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

.cart-footer {
  margin-top: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-actions {
  display: flex;
  gap: 10px;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 20px;
}

.summary-item {
  font-size: 14px;
  color: #606266;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 10px;
}
</style>
