<template>
  <CustomerLayout>
    <div class="address-list" v-loading="loading">
      <div class="page-header">
        <h1>收货地址管理</h1>
        <el-button type="primary" @click="handleAdd">新增地址</el-button>
      </div>
        <el-empty v-if="addresses.length === 0 && !loading" description="暂无收货地址">
          <el-button type="primary" @click="handleAdd">新增地址</el-button>
        </el-empty>

        <div v-else class="address-grid">
          <el-card
            v-for="address in addresses"
            :key="address.id"
            class="address-card"
            :class="{ 'default-address': address.isDefault }"
            shadow="hover"
          >
            <div class="address-header">
              <div class="address-info">
                <span class="receiver">{{ address.receiverName }}</span>
                <span class="phone">{{ address.receiverPhone }}</span>
                <el-tag v-if="address.isDefault" type="danger" size="small">默认</el-tag>
              </div>
              <div class="address-actions">
                <el-button
                  v-if="!address.isDefault"
                  link
                  type="primary"
                  @click="handleSetDefault(address.id)"
                >
                  设为默认
                </el-button>
                <el-button link type="primary" @click="handleEdit(address)">编辑</el-button>
                <el-button link type="danger" @click="handleDelete(address.id)">删除</el-button>
              </div>
            </div>
            <div class="address-detail">
              {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
            </div>
            <div v-if="address.postalCode" class="postal-code">
              邮编：{{ address.postalCode }}
            </div>
          </el-card>
        </div>

    <!-- 新增/编辑地址对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form
        :model="addressForm"
        :rules="addressRules"
        ref="addressFormRef"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="邮编" prop="postalCode">
          <el-input v-model="addressForm.postalCode" placeholder="请输入邮编（可选）" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
    </div>
  </CustomerLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/user'
import { showSuccess, showError, confirm } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const addressFormRef = ref(null)
const addresses = ref([])
const editingId = ref(null)

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  postalCode: '',
  isDefault: false
})

const addressRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

async function loadAddresses() {
  loading.value = true
  try {
    const response = await getAddresses()
    if (response.code === 0) {
      addresses.value = response.data || []
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  editingId.value = null
  dialogTitle.value = '新增地址'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(address) {
  editingId.value = address.id
  dialogTitle.value = '编辑地址'
  Object.assign(addressForm, {
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    province: address.province,
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    postalCode: address.postalCode || '',
    isDefault: address.isDefault
  })
  dialogVisible.value = true
}

function resetForm() {
  Object.assign(addressForm, {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    postalCode: '',
    isDefault: false
  })
  if (addressFormRef.value) {
    addressFormRef.value.clearValidate()
  }
}

async function handleSubmit() {
  if (!addressFormRef.value) return

  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (editingId.value) {
          await updateAddress(editingId.value, addressForm)
          showSuccess('更新成功')
        } else {
          await addAddress(addressForm)
          showSuccess('添加成功')
        }
        dialogVisible.value = false
        await loadAddresses()
      } catch (error) {
        console.error('保存地址失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

async function handleDelete(id) {
  try {
    await confirm('确定要删除这个地址吗？')
    await deleteAddress(id)
    showSuccess('删除成功')
    await loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
    }
  }
}

async function handleSetDefault(id) {
  try {
    await setDefaultAddress(id)
    showSuccess('设置成功')
    await loadAddresses()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.address-list {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
}

.address-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.address-card {
  transition: transform 0.2s;
}

.address-card:hover {
  transform: translateY(-5px);
}

.default-address {
  border: 2px solid #f56c6c;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.receiver {
  font-weight: bold;
  font-size: 16px;
}

.phone {
  color: #606266;
}

.address-actions {
  display: flex;
  gap: 5px;
}

.address-detail {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 5px;
}

.postal-code {
  color: #909399;
  font-size: 12px;
}
</style>
