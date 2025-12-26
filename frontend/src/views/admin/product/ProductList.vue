<template>
  <div class="admin-product-list" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>商品管理</h3>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>

      <div class="filter-bar">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="关键词">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索商品名称"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              style="width: 200px;"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" clearable placeholder="全部" @change="handleSearch">
              <el-option label="上架" value="ON_SALE" />
              <el-option label="下架" value="OFF_SALE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="products" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.coverImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
              :preview-src-list="[row.coverImageUrl || '']"
              fit="cover"
              style="width: 80px; height: 80px;"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="salesCount" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'info'">
              {{ row.status === 'ON_SALE' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row.id)">编辑</el-button>
            <el-button
              link
              :type="row.status === 'ON_SALE' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ON_SALE' ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getAdminProducts,
  deleteProduct,
  updateProductStatus
} from '@/api/admin/product'
import { showSuccess, showError, confirm } from '@/utils/message'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()

const loading = ref(false)
const products = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  keyword: '',
  status: ''
})

async function loadProducts() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.status) params.status = queryParams.status

    const response = await getAdminProducts(params)
    if (response.code === 0) {
      products.value = response.data.content || []
      total.value = response.data.totalElements || 0
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
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
  queryParams.status = ''
  queryParams.page = 1
  loadProducts()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadProducts()
}

function handleAdd() {
  router.push('/admin/products/create')
}

function handleEdit(id) {
  router.push(`/admin/products/${id}/edit`)
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE'
  try {
    await updateProductStatus(row.id, newStatus)
    showSuccess('操作成功')
    loadProducts()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

async function handleDelete(id) {
  try {
    await confirm('确定要删除这个商品吗？删除后无法恢复。')
    await deleteProduct(id)
    showSuccess('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.admin-product-list {
  height: 100%;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  margin-bottom: 20px;
}
</style>
