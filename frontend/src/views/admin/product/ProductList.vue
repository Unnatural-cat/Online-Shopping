<template>
  <div class="admin-product-list page-container" v-loading="loading">
    <!-- 搜索/筛选区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="商品名称">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入商品名称"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select 
            v-model="queryParams.status" 
            clearable 
            placeholder="请选择商品状态" 
            @change="handleSearch"
            style="width: 150px;"
          >
            <el-option label="上架" value="ON_SALE" />
            <el-option label="下架" value="OFF_SALE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-section">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建商品
      </el-button>
      <el-button type="primary" @click="handleBatchEdit" :disabled="selectedIds.length === 0">
        <el-icon><Edit /></el-icon>
        编辑商品
      </el-button>
      <el-button 
        type="danger" 
        @click="handleBatchDelete" 
        :disabled="selectedIds.length === 0"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 选择状态显示 -->
    <div class="selection-info" v-if="selectedIds.length > 0">
      <span>已选择 {{ selectedIds.length }} 项</span>
      <el-link type="primary" :underline="false" @click="clearSelection" class="clear-link">
        清空
      </el-link>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table 
        ref="tableRef"
        :data="products" 
        border
        stripe
        @selection-change="handleSelectionChange"
        class="data-table"
        :show-overflow-tooltip="false"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="商品图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.coverImageUrl || 'https://via.placeholder.com/80x80?text=No+Image'"
              :preview-src-list="[row.coverImageUrl || '']"
              fit="cover"
              style="width: 80px; height: 80px; border-radius: 4px;"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" align="center" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="120" align="center">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center" />
        <el-table-column prop="salesCount" label="销量" width="100" align="center" />
        <el-table-column prop="status" label="发布状态" width="120" align="center">
          <template #default="{ row }">
            <div class="status-tag-wrapper">
              <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'info'" size="small">
                {{ row.status === 'ON_SALE' ? '已发布' : '未发布' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="handleEdit(row.id)">
                商品配置
              </el-button>
              <el-button 
                link 
                :type="row.status === 'ON_SALE' ? 'warning' : 'success'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 'ON_SALE' ? '下架' : '上架' }}
              </el-button>
              <el-dropdown @command="(cmd) => handleMoreAction(cmd, row)">
                <el-button link type="primary">
                  更多
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Search, 
  Refresh, 
  Plus, 
  Edit, 
  Delete, 
  ArrowDown 
} from '@element-plus/icons-vue'
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
const selectedIds = ref([])
const tableRef = ref(null)

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

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function clearSelection() {
  selectedIds.value = []
  if (tableRef.value) {
    tableRef.value.clearSelection()
  }
}

function handleBatchEdit() {
  if (selectedIds.value.length === 0) {
    showError('请先选择要编辑的商品')
    return
  }
  if (selectedIds.value.length > 1) {
    showError('只能选择一个商品进行编辑')
    return
  }
  handleEdit(selectedIds.value[0])
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    showError('请先选择要删除的商品')
    return
  }
  try {
    await confirm(`确定要删除选中的 ${selectedIds.value.length} 个商品吗？删除后无法恢复。`)
    for (const id of selectedIds.value) {
      await deleteProduct(id)
    }
    showSuccess('删除成功')
    selectedIds.value = []
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

function handleMoreAction(command, row) {
  if (command === 'edit') {
    handleEdit(row.id)
  } else if (command === 'delete') {
    handleDelete(row.id)
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.admin-product-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.search-section {
  margin-bottom: 10px;
}

.search-section :deep(.el-form) {
  margin: 0;
}

.search-section :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 15px;
}

/* 对齐第一个表单项（商品名称）的标签与表格左侧 */
.search-section :deep(.el-form-item:first-child) {
  margin-left: 0;
  padding-left: 0px; /* 与表格第一列的padding-left对齐 */
}

.action-section {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-section .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.clear-link {
  margin-left: 10px;
  cursor: pointer;
}

.data-table :deep(.el-table__header) {
  background-color: #fafafa;
}

.data-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 确保表格内容垂直居中 */
.data-table :deep(.el-table th),
.data-table :deep(.el-table td) {
  vertical-align: middle !important;
}

.data-table :deep(.el-table__cell) {
  vertical-align: middle !important;
}

/* 确保设置了居中对齐的列内容正确居中 */
.data-table :deep(.el-table__cell.is-center .cell) {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 确保发布状态列的标签居中 */
.status-tag-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.data-table :deep(.el-table__cell.is-center .el-tag) {
  margin: 0;
}

/* 价格文本样式 */
.price-text {
  font-weight: 600;
  color: #f56c6c;
}

/* 统一表格边框样式，确保所有列之间的分界线一致 */
.data-table :deep(.el-table) {
  border-collapse: separate;
  border-spacing: 0;
}

/* 统一所有单元格的右边框 */
.data-table :deep(.el-table__cell) {
  border-right: 1px solid #EBEEF5 !important;
}

/* 确保表格第一列的padding，用于对齐筛选栏 */
.data-table :deep(.el-table__cell:first-child) {
  padding-left: 20px;
}

/* 完全移除固定列的所有阴影和特殊效果 */
.data-table :deep(.el-table__fixed-right) {
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  border-left: 1px solid #EBEEF5 !important;
}

.data-table :deep(.el-table__fixed-right-patch) {
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  border-left: 1px solid #EBEEF5 !important;
  background-color: #fff;
}

/* 完全移除固定列的伪元素（这些伪元素通常用于创建阴影效果） */
.data-table :deep(.el-table__fixed-right::before),
.data-table :deep(.el-table__fixed-right::after) {
  display: none !important;
  content: none !important;
  width: 0 !important;
  height: 0 !important;
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  background: none !important;
}

.data-table :deep(.el-table__fixed-right-patch::before),
.data-table :deep(.el-table__fixed-right-patch::after) {
  display: none !important;
  content: none !important;
  width: 0 !important;
  height: 0 !important;
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  background: none !important;
}

/* 确保固定列内部所有元素都没有阴影 */
.data-table :deep(.el-table__fixed-right *),
.data-table :deep(.el-table__fixed-right-patch *) {
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
}

/* 确保发布状态列（最后一列普通列）的右边框与操作列（第一列固定列）的左边框一致 */
.data-table :deep(.el-table__body-wrapper .el-table__body tr td:last-of-type:not(.el-table__fixed-right td)) {
  border-right: 1px solid #EBEEF5 !important;
}

.data-table :deep(.el-table__header-wrapper .el-table__header tr th:last-of-type:not(.el-table__fixed-right th)) {
  border-right: 1px solid #EBEEF5 !important;
}

/* 确保固定列的第一列（操作列）的左边框与其他列一致，移除可能的重叠边框 */
.data-table :deep(.el-table__fixed-right .el-table__cell:first-child) {
  border-left: 1px solid #EBEEF5 !important;
  border-right: none !important;
}

.data-table :deep(.el-table__fixed-right .el-table__cell:not(:first-child)) {
  border-right: none !important;
}

/* 确保发布状态列和操作列之间的分界线统一，移除固定列包装器可能产生的额外边框 */
.data-table :deep(.el-table__fixed-right-body-wrapper),
.data-table :deep(.el-table__fixed-right-header-wrapper) {
  border-left: none !important;
}

/* 确保固定列包装器不会产生额外的边框效果 */
.data-table :deep(.el-table__fixed-right-patch) {
  border-left: none !important;
}

/* 移除固定列的所有可能产生阴影的样式 */
.data-table :deep(.el-table__fixed-right),
.data-table :deep(.el-table__fixed-right-patch),
.data-table :deep(.el-table__fixed-right-body-wrapper),
.data-table :deep(.el-table__fixed-right-header-wrapper) {
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
  filter: none !important;
  -webkit-filter: none !important;
}

/* 确保发布状态列和操作列之间的分界线完全一致，无任何阴影或特殊效果 */
/* 移除固定列包装器的所有阴影 */
.data-table :deep(.el-table__fixed-right-body-wrapper),
.data-table :deep(.el-table__fixed-right-header-wrapper) {
  box-shadow: none !important;
  -webkit-box-shadow: none !important;
  -moz-box-shadow: none !important;
}

/* 确保固定列背景色与表格一致，避免视觉差异 */
.data-table :deep(.el-table__fixed-right) {
  background-color: #fff;
}

.data-table :deep(.el-table__fixed-right .el-table__body tr),
.data-table :deep(.el-table__fixed-right .el-table__header tr) {
  background-color: #fff;
}

/* 操作列按钮对齐 */
.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.action-buttons .el-button {
  margin: 0;
  padding: 0;
  height: auto;
  line-height: 1.5;
}

.action-buttons .el-dropdown {
  margin: 0;
}
</style>
