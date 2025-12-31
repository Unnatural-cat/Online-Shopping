<template>
  <CustomerLayout>
    <div class="product-detail" v-loading="loading">
      <el-row :gutter="20" v-if="product" class="product-row">
        <el-col :span="12">
          <div class="product-image-container">
            <el-image
              :src="product.coverImageUrl || 'https://via.placeholder.com/500x500?text=No+Image'"
              fit="contain"
              :preview-src-list="[product.coverImageUrl || '']"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="product-info">
            <h1>{{ product.name }}</h1>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
            </div>
            <div class="product-meta">
              <p>库存：<span :class="{ 'stock-low': product.stock < 10 }">{{ product.stock }}</span></p>
              <p>销量：{{ product.salesCount || 0 }}</p>
            </div>
            <div class="product-description">
              <h3>商品描述</h3>
              <p>{{ product.description || '暂无描述' }}</p>
            </div>
            <div class="product-actions">
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                :disabled="product.stock === 0"
                style="width: 120px; margin-right: 10px;"
              />
              <el-button
                type="primary"
                size="large"
                @click="addToCart"
                :disabled="product.stock === 0"
              >
                加入购物车
              </el-button>
              <el-button
                type="success"
                size="large"
                @click="buyNow"
                :disabled="product.stock === 0"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else-if="!loading" description="商品不存在" />
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { addCartItem } from '@/api/cart'
import { showSuccess, showError } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)

async function loadProductDetail() {
  const id = route.params.id
  if (!id) {
    router.push('/products')
    return
  }

  loading.value = true
  try {
    const response = await getProductDetail(id)
    if (response.code === 0) {
      product.value = response.data
      quantity.value = 1
    } else {
      showError('商品不存在')
      router.push('/products')
    }
  } catch (error) {
    // 如果是403错误，可能是权限问题，但不影响页面展示
    if (error.response?.status === 403) {
      showError('无权限访问，请先登录')
      router.push('/login')
    } else {
      showError('加载商品详情失败')
      router.push('/products')
    }
  } finally {
    loading.value = false
  }
}

async function addToCart() {
  if (!userStore.isLoggedIn) {
    showError('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await addCartItem({
      productId: product.value.id,
      quantity: quantity.value
    })
    if (response.code === 0) {
      showSuccess('已加入购物车')
    }
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

function buyNow() {
  if (!userStore.isLoggedIn) {
    showError('请先登录')
    router.push('/login')
    return
  }

  // 跳转到订单确认页面，传递商品信息
  router.push({
    name: 'Checkout',
    query: {
      productId: product.value.id,
      quantity: quantity.value
    }
  })
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.product-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-row {
  align-items: stretch;
}

.product-row :deep(.el-col) {
  display: flex;
  flex-direction: column;
}

.product-image-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
  text-align: center;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
}

.product-image-container .el-image {
  width: 100%;
  max-height: 500px;
  object-fit: contain;
}

.product-info {
  background: white;
  padding: 30px;
  border-radius: 4px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.product-info h1 {
  font-size: 24px;
  margin: 0 0 20px 0;
}

.product-price {
  margin: 20px 0;
}

.product-price .price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}

.product-meta {
  margin: 20px 0;
  color: #606266;
}

.product-meta p {
  margin: 10px 0;
}

.stock-low {
  color: #f56c6c;
  font-weight: bold;
}

.product-description {
  margin: 30px 0;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.product-description h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.product-description p {
  color: #606266;
  line-height: 1.6;
}

.product-actions {
  margin-top: 30px;
  display: flex;
  align-items: center;
}
</style>
