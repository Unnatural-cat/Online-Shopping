<template>
  <CustomerLayout>
    <div class="cart page-container" v-loading="loading">
      <div class="page-header">
      <h1>购物车</h1>
      </div>
        <el-card v-if="cartItems.length === 0 && !loading">
          <el-empty description="购物车是空的">
            <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
          </el-empty>
        </el-card>

        <div v-else class="cart-content">
          <div class="table-container">
            <el-table 
              :data="cartItems" 
              border 
              stripe
              class="cart-table"
            >
            <el-table-column label="商品" :min-width="getColumnWidth('product')" class-name="product-column">
              <template #default="{ row }">
                <div class="product-cell">
                  <el-checkbox
                    v-model="row.checked"
                    @change="handleCheckChange(row)"
                    class="product-checkbox"
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
            <el-table-column label="单价" :width="getColumnWidth('price')" align="center" class-name="price-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="price-text">¥{{ row.productPrice }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="数量" :width="getColumnWidth('quantity')" align="center" class-name="quantity-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <el-input-number
                    v-model="row.quantity"
                    :min="1"
                    :max="row.stock || 999"
                    @change="handleQuantityChange(row)"
                    size="small"
                    class="quantity-input"
                  />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="小计" :width="getColumnWidth('subtotal')" align="center" class-name="subtotal-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <span class="subtotal">¥{{ (Number(row.productPrice) * row.quantity).toFixed(2) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" :width="getColumnWidth('action')" align="center" fixed="right" class-name="action-column">
              <template #default="{ row }">
                <div class="cell-content-center">
                  <el-button
                    type="danger"
                    link
                    size="small"
                    @click="handleDelete(row.id)"
                  >
                    删除
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          </div>

          <div class="cart-footer card-container">
            <div class="cart-actions">
              <el-button @click="handleSelectAll">全选</el-button>
              <el-button @click="handleUnselectAll">取消全选</el-button>
              <el-button type="danger" @click="handleClear">清空购物车</el-button>
            </div>
            <div class="cart-summary">
              <span class="summary-item">已选商品：{{ selectedCount }} 件</span>
              <span class="summary-item">
                <span>合计：</span>
                <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
              </span>
              <el-button
                type="primary"
                size="large"
                @click="handleCheckout"
                :disabled="selectedCount === 0"
                class="checkout-button"
              >
                去结算
              </el-button>
            </div>
          </div>
        </div>
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, deleteCartItem, clearCart } from '@/api/cart'
import { showSuccess, showError, confirm } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'

const router = useRouter()

const loading = ref(false)
const cartItems = ref([])
const windowWidth = ref(window.innerWidth)

// 响应式列宽配置
const columnWidths = {
  desktop: {
    product: 450,
    price: 140,
    quantity: 160,
    subtotal: 160,
    action: 120
  },
  tablet: {
    product: 350,
    price: 120,
    quantity: 140,
    subtotal: 140,
    action: 100
  },
  mobile: {
    product: 280,
    price: 100,
    quantity: 120,
    subtotal: 120,
    action: 80
  }
}

function getColumnWidth(columnName) {
  if (windowWidth.value >= 1024) {
    return columnWidths.desktop[columnName]
  } else if (windowWidth.value >= 768) {
    return columnWidths.tablet[columnName]
  } else {
    return columnWidths.mobile[columnName]
  }
}

function handleResize() {
  windowWidth.value = window.innerWidth
}

onMounted(() => {
  loadCart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.checked).length
})

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.checked)
    .reduce((sum, item) => {
      return sum + (Number(item.productPrice) || 0) * item.quantity
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
    showSuccess('更新成功')
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

</script>

<style scoped>

.cart-footer {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
  background: white;
  padding: var(--card-padding);
  border-radius: var(--card-border-radius);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  min-height: 60px;
  box-sizing: border-box;
}

.cart-content {
  width: 100%;
}

.table-container {
  background: white;
  padding: 0;
  border-radius: var(--card-border-radius);
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.cart-table {
  width: 100%;
}

.cart-table :deep(.el-table) {
  width: 100%;
  table-layout: fixed;
}

.cart-table :deep(.el-table__header-wrapper),
.cart-table :deep(.el-table__body-wrapper) {
  width: 100%;
}

.cart-table :deep(.el-table__header) {
  width: 100% !important;
  background-color: #fafafa;
}

.cart-table :deep(.el-table__body) {
  width: 100% !important;
}

/* 表头样式优化 */
.cart-table :deep(.el-table__header th) {
  background-color: #fafafa !important;
  color: #606266;
  font-weight: 600;
  border-bottom: 2px solid #ebeef5;
  padding: 16px 12px;
}

.cart-table :deep(.el-table__header th .cell) {
  font-weight: 600;
  color: #606266;
}

/* 表格行悬停效果 */
.cart-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.cart-table :deep(.el-table__row:hover td) {
  background-color: #f5f7fa !important;
}

/* 斑马纹样式优化 */
.cart-table :deep(.el-table__row--striped) {
  background-color: #fafafa;
}

.cart-table :deep(.el-table__row--striped:hover) {
  background-color: #f5f7fa !important;
}

.cart-table :deep(.el-table__row--striped:hover td) {
  background-color: #f5f7fa !important;
}

/* 禁用列宽拖动 */
.cart-table :deep(.el-table__header th) {
  user-select: none;
  position: relative;
}

.cart-table :deep(.el-table__header th .cell) {
  padding-right: 0 !important;
}

/* 隐藏拖动句柄 */
.cart-table :deep(.el-table__header th::after) {
  display: none !important;
  width: 0 !important;
}

.cart-table :deep(.el-table__header th:hover::after) {
  display: none !important;
}

.cart-table :deep(.el-table__header th.is-leaf) {
  border-right: 1px solid #EBEEF5;
}

.cart-table :deep(.el-table__header th .resizing) {
  cursor: default !important;
}

.cart-table :deep(.el-table__header th:hover) {
  cursor: default !important;
}

/* 禁用拖动相关的鼠标样式 */
.cart-table :deep(.el-table__header th) {
  cursor: default !important;
}

.cart-table :deep(.el-table__header-wrapper) {
  user-select: none;
}

/* 确保列宽固定 */
.cart-table :deep(.el-table__header th),
.cart-table :deep(.el-table__body td) {
  overflow: hidden;
}

.cart-table :deep(.product-column) {
  overflow: visible !important;
}

.cart-table :deep(.price-column),
.cart-table :deep(.quantity-column),
.cart-table :deep(.subtotal-column),
.cart-table :deep(.action-column) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 确保表格行有足够的高度 */
.cart-table :deep(.el-table__row) {
  height: auto;
  min-height: 100px;
}

.cart-table :deep(.el-table td) {
  padding: 16px 12px;
  vertical-align: middle !important;
  border-bottom: 1px solid #ebeef5;
}

.cart-table :deep(.el-table__cell) {
  padding: 16px 12px;
  height: auto;
  min-height: 100px;
  vertical-align: middle !important;
}

.cart-table :deep(.el-table th) {
  vertical-align: middle !important;
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

/* 商品列保持左对齐 */
.cart-table :deep(.product-column) {
  text-align: left !important;
}

.cart-table :deep(.product-column .cell-content-center) {
  justify-content: flex-start;
}

/* 操作列按钮样式优化 */
.cart-table :deep(.action-column) {
  text-align: center !important;
}

.cart-table :deep(.action-column .el-button) {
  padding: 0;
  font-size: 14px;
  margin: 0;
}

.cart-table :deep(.action-column .el-button--link) {
  color: #f56c6c;
  font-weight: 500;
}

.cart-table :deep(.action-column .el-button--link:hover) {
  color: #f78989;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
  min-height: 80px;
}

.product-checkbox {
  flex-shrink: 0;
}

.product-image {
  width: 80px;
  height: 80px;
  min-width: 80px;
  min-height: 80px;
  object-fit: cover;
  cursor: pointer;
  border-radius: 4px;
  flex-shrink: 0;
  border: 1px solid #ebeef5;
  transition: all 0.2s;
}

.product-image:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  margin: 0 0 10px 0;
  cursor: pointer;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  transition: color 0.2s;
}

.product-name:hover {
  color: #409eff;
}

.product-price {
  margin: 0;
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
  line-height: 1.5;
}

.price-text {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
}

.subtotal {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
  display: inline-block;
  text-align: center;
  white-space: nowrap;
}

.quantity-input {
  width: 100%;
  max-width: 120px;
  margin: 0 auto;
}

.quantity-input :deep(.el-input__inner) {
  text-align: center;
}

.quantity-input :deep(.el-input-number) {
  width: 100%;
}

.quantity-input :deep(.el-input-number__decrease),
.quantity-input :deep(.el-input-number__increase) {
  width: 32px;
}


.cart-actions {
  display: flex;
  gap: var(--button-gap);
  flex-wrap: wrap;
  align-items: center;
  height: 100%;
  min-height: 40px;
}

.cart-actions .el-button {
  border-radius: 4px;
  height: 40px;
  padding: 10px 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
  height: 100%;
  min-height: 40px;
  line-height: 1;
}

.summary-item {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  height: 40px;
  line-height: 1;
  box-sizing: border-box;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 8px;
  display: inline-block;
  line-height: 1;
  vertical-align: baseline;
}

.checkout-button {
  margin: 0;
  height: 40px;
  padding: 10px 24px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  line-height: 1;
}

/* 响应式优化 */
@media (max-width: 1024px) {
  .cart-table {
    min-width: 700px;
  }
  
  .product-image {
    width: 70px;
    height: 70px;
    min-width: 70px;
    min-height: 70px;
  }
}

@media (max-width: 768px) {
  .cart {
    padding: 16px;
  }
  
  .cart-table {
    min-width: 600px;
  }
  
  .cart-table :deep(.el-table) {
    font-size: 12px;
  }
  
  .product-image {
    width: 60px;
    height: 60px;
    min-width: 60px;
    min-height: 60px;
  }
  
  .product-cell {
    gap: 8px;
    padding: 8px 0;
  }
  
  .cart-footer {
    flex-direction: column;
    align-items: stretch;
  }
  
  .cart-summary {
    justify-content: space-between;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .cart-table {
    min-width: 500px;
  }
  
  .product-image {
    width: 50px;
    height: 50px;
    min-width: 50px;
    min-height: 50px;
  }
  
  .product-name {
    font-size: 12px;
  }
  
  .product-price {
    font-size: 14px;
  }
}
</style>
