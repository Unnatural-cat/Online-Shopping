<template>
  <div class="admin-product-form" v-loading="loading">
    <el-card>
      <template #header>
        <div class="header-content">
          <h3>{{ isEdit ? '编辑商品' : '新增商品' }}</h3>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-form
        :model="productForm"
        :rules="rules"
        ref="productFormRef"
        label-width="100px"
        style="max-width: 800px;"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            :step="0.01"
            placeholder="请输入商品价格"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            :step="1"
            placeholder="请输入库存数量"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="5"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="封面图片URL" prop="coverImageUrl">
          <el-input v-model="productForm.coverImageUrl" placeholder="请输入图片URL" />
          <div v-if="productForm.coverImageUrl" style="margin-top: 10px;">
            <el-image
              :src="productForm.coverImageUrl"
              fit="contain"
              style="width: 200px; height: 200px;"
            />
          </div>
        </el-form-item>
        <el-form-item label="分类ID" prop="categoryId">
          <el-input-number
            v-model="productForm.categoryId"
            :min="0"
            placeholder="请输入分类ID（可选）"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getAdminProductDetail,
  createProduct,
  updateProduct
} from '@/api/admin/product'
import { showSuccess, showError } from '@/utils/message'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const productFormRef = ref(null)
const isEdit = ref(false)

const productForm = reactive({
  name: '',
  price: null,
  stock: null,
  description: '',
  coverImageUrl: '',
  categoryId: null
})

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格必须大于等于0', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存必须大于等于0', trigger: 'blur' }
  ]
}

async function loadProduct() {
  const id = route.params.id
  if (!id) return

  isEdit.value = true
  loading.value = true
  try {
    const response = await getAdminProductDetail(id)
    if (response.code === 0) {
      Object.assign(productForm, {
        name: response.data.name || '',
        price: response.data.price || null,
        stock: response.data.stock || null,
        description: response.data.description || '',
        coverImageUrl: response.data.coverImageUrl || '',
        categoryId: response.data.categoryId || null
      })
    }
  } catch (error) {
    showError('加载商品信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!productFormRef.value) return

  await productFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          await updateProduct(route.params.id, productForm)
          showSuccess('更新成功')
        } else {
          await createProduct(productForm)
          showSuccess('创建成功')
        }
        router.push('/admin/products')
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

onMounted(() => {
  if (route.params.id) {
    loadProduct()
  }
})
</script>

<style scoped>
.admin-product-form {
  height: 100%;
}
</style>
