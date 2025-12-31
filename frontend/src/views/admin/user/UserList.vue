<template>
  <div class="admin-user-list" v-loading="loading">
    <el-card>
      <template #header>
        <h3>用户管理</h3>
      </template>

      <div class="filter-bar">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="关键词">
            <el-input
              v-model="queryParams.keyword"
              placeholder="请输入邮箱、手机号或昵称"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              style="width: 250px;"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-container">
        <el-table :data="users" border stripe class="user-table" style="width: 100%">
          <el-table-column prop="id" label="用户ID" width="100" align="center" />
          <el-table-column prop="nickname" label="昵称" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
          <el-table-column prop="phone" label="手机号" min-width="130" />
          <el-table-column prop="role" label="角色" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
                {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="orderCount" label="订单数" width="100" align="center" />
          <el-table-column prop="totalSpent" label="总消费" width="120" align="center">
            <template #default="{ row }">
              ¥{{ (row.totalSpent / 100).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" min-width="180" class-name="divider-column">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleViewDetail(row.id)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminUsers } from '@/api/admin/user'
import Pagination from '@/components/Pagination.vue'

const router = useRouter()

const loading = ref(false)
const users = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  keyword: ''
})

async function loadUsers() {
  loading.value = true
  try {
    const params = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.keyword) {
      params.keyword = queryParams.keyword
    }

    const response = await getAdminUsers(params)
    if (response.code === 0) {
      users.value = response.data.content || []
      total.value = response.data.totalElements || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadUsers()
}

function resetFilters() {
  queryParams.keyword = ''
  queryParams.page = 1
  loadUsers()
}

function handlePageChange({ page, size }) {
  queryParams.page = page
  queryParams.size = size
  loadUsers()
}

function handleViewDetail(userId) {
  router.push(`/admin/users/${userId}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  // 处理ISO格式的日期字符串，移除毫秒部分
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr
  
  // 格式化为：YYYY-MM-DD HH:mm:ss
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-user-list {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 为注册时间列添加右边框作为分割线 */
.user-table :deep(.divider-column) {
  border-right: 2px solid #e4e7ed;
}

.user-table :deep(.divider-column .cell) {
  border-right: none;
}
</style>

