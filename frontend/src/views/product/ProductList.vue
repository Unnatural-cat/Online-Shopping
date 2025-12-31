<template>
  <CustomerLayout>
    <div class="product-list page-container">
      <div class="page-header">
        <h1>商品列表</h1>
        <div class="button-group">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索商品名称或描述"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            style="width: 300px;"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
      </div>
      
      <div class="search-section">
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="价格区间">
              <el-input-number
                v-model="queryParams.minPrice"
                :min="0"
                :precision="2"
                placeholder="最低价"
                style="width: 180px;"
                @change="handleSearch"
              />
              <span style="margin: 0 10px;">-</span>
              <el-input-number
                v-model="queryParams.maxPrice"
                :min="0"
                :precision="2"
                placeholder="最高价"
                style="width: 180px;"
                @change="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button @click="resetFilters">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-loading="loading" class="product-grid">
          <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
          <el-card
            v-for="product in products"
            :key="product.id"
            class="product-card"
            shadow="hover"
            @click="goToDetail(product.id)"
          >
            <div class="product-image">
              <img
                v-if="product.coverImageUrl"
                :src="product.coverImageUrl"
                :alt="product.name"
                @error="handleImageError"
              />
              <el-image
                v-else
                :src="'https://via.placeholder.com/300x300?text=No+Image'"
                fit="cover"
              />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-price">¥{{ product.price }}</p>
              <div class="product-meta">
                <span>库存：{{ product.stock }}</span>
                <span>销量：{{ product.salesCount || 0 }}</span>
              </div>
              <el-button
                type="primary"
                @click.stop="addToCart(product)"
                :disabled="product.stock === 0"
              >
                加入购物车
              </el-button>
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
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProducts } from '@/api/product'
import { addCartItem } from '@/api/cart'
import { showSuccess, showError } from '@/utils/message'
import Pagination from '@/components/Pagination.vue'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  keyword: '',
  minPrice: null,
  maxPrice: null
})

async function loadProducts() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.minPrice) params.minPrice = queryParams.minPrice
    if (queryParams.maxPrice) params.maxPrice = queryParams.maxPrice

    const response = await getProducts(params)
    if (response.code === 0) {
      products.value = response.data.content || []
      total.value = response.data.totalElements || 0
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    // 如果是403错误，可能是权限问题，但不影响页面展示
    if (error.response?.status !== 403) {
      showError('加载商品列表失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadProducts()
}

function resetFilters() {
  queryParams.keyword = ''
  queryParams.minPrice = null
  queryParams.maxPrice = null
  queryParams.page = 1
  // 清除URL参数
  router.push({ path: '/products', query: {} })
  loadProducts()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToDetail(id) {
  router.push(`/products/${id}`)
}

async function addToCart(product) {
  if (!userStore.isLoggedIn) {
    showError('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await addCartItem({
      productId: product.id,
      quantity: 1
    })
    if (response.code === 0) {
      showSuccess('已加入购物车')
    }
  } catch (error) {
    console.error('加入购物车失败:', error)
  }
}

function handleImageError(event) {
  event.target.src = 'https://via.placeholder.com/300x300?text=No+Image'
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-list {
  /* 使用全局样式，无需重复定义 */
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  margin-bottom: 10px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
  margin: 10px 0;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.product-card .el-button {
  width: 100%;
}

.search-section {
  margin: 8px 0;
}

.search-section :deep(.el-form-item) {
  margin-bottom: 0;
}
</style>
