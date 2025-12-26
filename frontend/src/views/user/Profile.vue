<template>
  <div class="profile" v-loading="loading">
    <div class="profile-container">
      <div class="profile-header">
        <h1>个人中心</h1>
      </div>
      <div class="profile-content">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card>
              <el-menu
                :default-active="activeMenu"
                @select="handleMenuSelect"
              >
                <el-menu-item index="info">
                  <el-icon><User /></el-icon>
                  <span>个人信息</span>
                </el-menu-item>
                <el-menu-item index="password">
                  <el-icon><Lock /></el-icon>
                  <span>修改密码</span>
                </el-menu-item>
                <el-menu-item index="addresses">
                  <el-icon><Location /></el-icon>
                  <span>收货地址</span>
                </el-menu-item>
              </el-menu>
            </el-card>
          </el-col>
          <el-col :span="18">
            <!-- 个人信息 -->
            <el-card v-if="activeMenu === 'info'">
              <template #header>
                <h3>个人信息</h3>
              </template>
              <el-form :model="userInfo" label-width="100px" style="max-width: 500px;">
                <el-form-item label="邮箱">
                  <el-input v-model="userInfo.email" disabled />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="userInfo.phone" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="userInfo.nickname" />
                </el-form-item>
                <el-form-item label="角色">
                  <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'">
                    {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
                  </el-tag>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdateProfile" :loading="updating">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <!-- 修改密码 -->
            <el-card v-if="activeMenu === 'password'">
              <template #header>
                <h3>修改密码</h3>
              </template>
              <el-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                label-width="100px"
                style="max-width: 500px;"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入原密码"
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="至少8位，包含字母和数字"
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword" :loading="changing">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>

            <!-- 收货地址 -->
            <el-card v-if="activeMenu === 'addresses'">
              <template #header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <h3>收货地址</h3>
                  <el-button type="primary" @click="$router.push('/addresses')">
                    管理地址
                  </el-button>
                </div>
              </template>
              <p>点击"管理地址"按钮进行收货地址的增删改查操作</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Lock, Location } from '@element-plus/icons-vue'
import { getProfile, updateProfile, changePassword } from '@/api/user'
import { showSuccess, showError } from '@/utils/message'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const updating = ref(false)
const changing = ref(false)
const activeMenu = ref('info')
const userInfo = ref({})
const passwordFormRef = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度至少8位'))
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function loadUserInfo() {
  loading.value = true
  try {
    const response = await getProfile()
    if (response.code === 0) {
      userInfo.value = { ...response.data }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

function handleMenuSelect(key) {
  activeMenu.value = key
}

async function handleUpdateProfile() {
  updating.value = true
  try {
    const response = await updateProfile({
      nickname: userInfo.value.nickname
    })
    if (response.code === 0) {
      showSuccess('修改成功')
      await userStore.fetchUserInfo()
      await loadUserInfo()
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
  } finally {
    updating.value = false
  }
}

async function handleChangePassword() {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changing.value = true
      try {
        const response = await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        if (response.code === 0) {
          showSuccess('密码修改成功，请重新登录')
          passwordForm.oldPassword = ''
          passwordForm.newPassword = ''
          passwordForm.confirmPassword = ''
          setTimeout(() => {
            userStore.logout()
            window.location.href = '/login'
          }, 1500)
        }
      } catch (error) {
        console.error('修改密码失败:', error)
      } finally {
        changing.value = false
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.profile-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.profile-header h1 {
  margin: 0;
  font-size: 24px;
}

.profile-content {
  background: white;
  padding: 20px;
  border-radius: 4px;
}
</style>

