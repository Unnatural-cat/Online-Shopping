<template>
  <CustomerLayout v-if="!isAdminRoute">
    <div class="profile" v-loading="loading">
      <h1 class="page-title">个人中心</h1>
      <el-row :gutter="20" class="profile-row">
        <el-col :span="6">
          <el-card class="menu-card">
            <el-menu
              :default-active="activeMenu"
              @select="handleMenuSelect"
              class="profile-menu"
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
            <el-card v-if="activeMenu === 'info'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><User /></el-icon>
                    <h3>个人信息</h3>
                  </div>
                </div>
              </template>
              <div class="user-profile-container">
                <!-- 用户头像区域 -->
                <div class="avatar-section">
                  <div class="avatar-wrapper">
                    <el-avatar :size="100" class="user-avatar-large">
                      <el-icon :size="50"><User /></el-icon>
                    </el-avatar>
                    <div class="avatar-badge" v-if="userInfo.role === 'ADMIN'">
                      <el-icon><Star /></el-icon>
                    </div>
                  </div>
                  <div class="user-basic-info">
                    <h2 class="user-name">{{ userInfo.nickname || '未设置昵称' }}</h2>
                    <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'" size="large" class="role-tag">
                      <el-icon style="margin-right: 4px;"><component :is="userInfo.role === 'ADMIN' ? 'Star' : 'User'" /></el-icon>
                    {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
                  </el-tag>
                  </div>
                </div>
                
                <!-- 详细信息表单 -->
                <el-divider />
                <el-form :model="userInfo" label-width="120px" class="profile-form">
                  <el-form-item label="邮箱地址">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Message /></el-icon>
                      <el-input v-model="userInfo.email" disabled class="form-input" />
                    </div>
                  </el-form-item>
                  <el-form-item label="手机号码">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Phone /></el-icon>
                      <el-input v-model="userInfo.phone" disabled class="form-input" />
                    </div>
                  </el-form-item>
                  <el-form-item label="用户昵称">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Edit /></el-icon>
                      <el-input 
                        v-model="userInfo.nickname" 
                        placeholder="请输入昵称"
                        maxlength="20"
                        show-word-limit
                        class="form-input"
                      />
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button 
                      type="primary" 
                      size="large"
                      @click="handleUpdateProfile" 
                      :loading="updating"
                      class="save-button"
                    >
                      <el-icon style="margin-right: 6px;"><Check /></el-icon>
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
              </div>
            </el-card>

            <!-- 修改密码 -->
            <el-card v-if="activeMenu === 'password'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><Lock /></el-icon>
                    <h3>修改密码</h3>
                  </div>
                </div>
              </template>
              <div class="password-form-container">
                <el-row :gutter="40" class="password-row">
                  <el-col :span="14">
              <el-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                      label-width="120px"
                      class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入原密码"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="至少8位，包含字母和数字"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item>
                        <el-button 
                          type="primary" 
                          size="large"
                          @click="handleChangePassword" 
                          :loading="changing"
                          class="save-button"
                        >
                          <el-icon style="margin-right: 6px;"><Check /></el-icon>
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
                  </el-col>
                  <el-col :span="10">
                    <el-alert
                      title="密码安全提示"
                      type="info"
                      :closable="false"
                      show-icon
                      class="password-tip"
                    >
                      <template #default>
                        <ul class="tip-list">
                          <li>密码长度至少8位</li>
                          <li>必须包含字母和数字</li>
                          <li>建议使用大小写字母、数字和特殊字符的组合</li>
                        </ul>
                      </template>
                    </el-alert>
                  </el-col>
                </el-row>
              </div>
            </el-card>

            <!-- 收货地址 -->
            <el-card v-if="activeMenu === 'addresses'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><Location /></el-icon>
                  <h3>收货地址</h3>
                  </div>
                  <el-button type="primary" @click="$router.push('/addresses')" class="manage-button">
                    <el-icon style="margin-right: 6px;"><Plus /></el-icon>
                    管理地址
                  </el-button>
                </div>
              </template>
              <div v-loading="addressLoading" class="address-container">
                <el-empty 
                  v-if="addresses.length === 0 && !addressLoading" 
                  description="暂无收货地址"
                  :image-size="120"
                >
                  <el-button type="primary" size="large" @click="$router.push('/addresses')">
                    <el-icon style="margin-right: 6px;"><Plus /></el-icon>
                    去添加地址
                  </el-button>
                </el-empty>
                <div v-else class="address-list">
                  <el-card
                    v-for="address in addresses"
                    :key="address.id"
                    class="address-card"
                    :class="{ 'default-address': address.isDefault }"
                    shadow="hover"
                  >
                    <div class="address-card-content">
                    <div class="address-header">
                      <div class="address-info">
                          <el-icon class="address-icon"><User /></el-icon>
                        <span class="receiver">{{ address.receiverName }}</span>
                          <el-divider direction="vertical" />
                          <el-icon class="address-icon"><Phone /></el-icon>
                        <span class="phone">{{ address.receiverPhone }}</span>
                          <el-tag v-if="address.isDefault" type="danger" size="small" class="default-tag">
                            <el-icon style="margin-right: 2px;"><Star /></el-icon>
                            默认地址
                          </el-tag>
                      </div>
                    </div>
                    <div class="address-detail">
                        <el-icon class="location-icon"><Location /></el-icon>
                        <span>{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}</span>
                    </div>
                    <div v-if="address.postalCode" class="postal-code">
                        <el-icon style="margin-right: 4px;"><Message /></el-icon>
                      邮编：{{ address.postalCode }}
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-card>

          </el-col>
        </el-row>
    </div>
  </CustomerLayout>
  <div v-else class="profile" v-loading="loading">
    <h1 class="page-title">个人中心</h1>
    <el-row :gutter="20" class="profile-row">
      <el-col :span="6">
        <el-card class="menu-card">
          <el-menu
            :default-active="activeMenu"
            @select="handleMenuSelect"
            class="profile-menu"
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
            <el-card v-if="activeMenu === 'info'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><User /></el-icon>
                    <h3>个人信息</h3>
                  </div>
                </div>
              </template>
              <div class="user-profile-container">
                <!-- 用户头像区域 -->
                <div class="avatar-section">
                  <div class="avatar-wrapper">
                    <el-avatar :size="100" class="user-avatar-large">
                      <el-icon :size="50"><User /></el-icon>
                    </el-avatar>
                    <div class="avatar-badge" v-if="userInfo.role === 'ADMIN'">
                      <el-icon><Star /></el-icon>
                    </div>
                  </div>
                  <div class="user-basic-info">
                    <h2 class="user-name">{{ userInfo.nickname || '未设置昵称' }}</h2>
                    <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'primary'" size="large" class="role-tag">
                      <el-icon style="margin-right: 4px;"><component :is="userInfo.role === 'ADMIN' ? 'Star' : 'User'" /></el-icon>
                    {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
                  </el-tag>
                  </div>
                </div>
                
                <!-- 详细信息表单 -->
                <el-divider />
                <el-form :model="userInfo" label-width="120px" class="profile-form">
                  <el-form-item label="邮箱地址">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Message /></el-icon>
                      <el-input v-model="userInfo.email" disabled class="form-input" />
                    </div>
                  </el-form-item>
                  <el-form-item label="手机号码">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Phone /></el-icon>
                      <el-input v-model="userInfo.phone" disabled class="form-input" />
                    </div>
                  </el-form-item>
                  <el-form-item label="用户昵称">
                    <div class="form-item-content">
                      <el-icon class="form-icon"><Edit /></el-icon>
                      <el-input 
                        v-model="userInfo.nickname" 
                        placeholder="请输入昵称"
                        maxlength="20"
                        show-word-limit
                        class="form-input"
                      />
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button 
                      type="primary" 
                      size="large"
                      @click="handleUpdateProfile" 
                      :loading="updating"
                      class="save-button"
                    >
                      <el-icon style="margin-right: 6px;"><Check /></el-icon>
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
              </div>
            </el-card>

            <!-- 修改密码 -->
            <el-card v-if="activeMenu === 'password'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><Lock /></el-icon>
                    <h3>修改密码</h3>
                  </div>
                </div>
              </template>
              <div class="password-form-container">
                <el-row :gutter="40" class="password-row">
                  <el-col :span="14">
              <el-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                      label-width="120px"
                      class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入原密码"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="至少8位，包含字母和数字"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                        <div class="form-item-content">
                          <el-icon class="form-icon"><Lock /></el-icon>
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                            class="form-input"
                  />
                        </div>
                </el-form-item>
                <el-form-item>
                        <el-button 
                          type="primary" 
                          size="large"
                          @click="handleChangePassword" 
                          :loading="changing"
                          class="save-button"
                        >
                          <el-icon style="margin-right: 6px;"><Check /></el-icon>
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
                  </el-col>
                  <el-col :span="10">
                    <el-alert
                      title="密码安全提示"
                      type="info"
                      :closable="false"
                      show-icon
                      class="password-tip"
                    >
                      <template #default>
                        <ul class="tip-list">
                          <li>密码长度至少8位</li>
                          <li>必须包含字母和数字</li>
                          <li>建议使用大小写字母、数字和特殊字符的组合</li>
                        </ul>
                      </template>
                    </el-alert>
                  </el-col>
                </el-row>
              </div>
            </el-card>

            <!-- 收货地址 -->
            <el-card v-if="activeMenu === 'addresses'" class="content-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <el-icon class="header-icon"><Location /></el-icon>
                  <h3>收货地址</h3>
                  </div>
                  <el-button type="primary" @click="$router.push('/addresses')" class="manage-button">
                    <el-icon style="margin-right: 6px;"><Plus /></el-icon>
                    管理地址
                  </el-button>
                </div>
              </template>
              <div v-loading="addressLoading" class="address-container">
                <el-empty 
                  v-if="addresses.length === 0 && !addressLoading" 
                  description="暂无收货地址"
                  :image-size="120"
                >
                  <el-button type="primary" size="large" @click="$router.push('/addresses')">
                    <el-icon style="margin-right: 6px;"><Plus /></el-icon>
                    去添加地址
                  </el-button>
                </el-empty>
                <div v-else class="address-list">
                  <el-card
                    v-for="address in addresses"
                    :key="address.id"
                    class="address-card"
                    :class="{ 'default-address': address.isDefault }"
                    shadow="hover"
                  >
                    <div class="address-card-content">
                    <div class="address-header">
                      <div class="address-info">
                          <el-icon class="address-icon"><User /></el-icon>
                        <span class="receiver">{{ address.receiverName }}</span>
                          <el-divider direction="vertical" />
                          <el-icon class="address-icon"><Phone /></el-icon>
                        <span class="phone">{{ address.receiverPhone }}</span>
                          <el-tag v-if="address.isDefault" type="danger" size="small" class="default-tag">
                            <el-icon style="margin-right: 2px;"><Star /></el-icon>
                            默认地址
                          </el-tag>
                      </div>
                    </div>
                    <div class="address-detail">
                        <el-icon class="location-icon"><Location /></el-icon>
                        <span>{{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}</span>
                    </div>
                    <div v-if="address.postalCode" class="postal-code">
                        <el-icon style="margin-right: 4px;"><Message /></el-icon>
                      邮编：{{ address.postalCode }}
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { 
  User, 
  Lock, 
  Location, 
  Message, 
  Phone, 
  Edit, 
  Check, 
  Star,
  Plus
} from '@element-plus/icons-vue'
import { getProfile, updateProfile, changePassword, getAddresses } from '@/api/user'
import { showSuccess, showError } from '@/utils/message'
import CustomerLayout from '@/components/CustomerLayout.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

// 判断是否为管理端路由
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

const loading = ref(false)
const updating = ref(false)
const changing = ref(false)
const addressLoading = ref(false)
const activeMenu = ref('info')
const userInfo = ref({})
const passwordFormRef = ref(null)
const addresses = ref([])

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

async function loadAddresses() {
  addressLoading.value = true
  try {
    const response = await getAddresses()
    if (response.code === 0) {
      addresses.value = response.data || []
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
  } finally {
    addressLoading.value = false
  }
}

function handleMenuSelect(key) {
  activeMenu.value = key
  // 当切换到收货地址菜单时，加载地址列表
  if (key === 'addresses') {
    loadAddresses()
  }
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
  padding: 20px;
  width: 100%;
}

.page-title {
  margin: 0 0 30px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-row {
  align-items: stretch;
  display: flex;
  min-height: 500px;
}

.profile-row :deep(.el-col) {
  display: flex;
  flex-direction: column;
}

.menu-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.menu-card :deep(.el-card__body) {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.profile-menu {
  border-right: none;
  flex: 1;
}

.profile-menu :deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  font-size: 15px;
  padding-left: 24px !important;
}

.profile-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 10px;
}

.profile-menu :deep(.el-menu-item.is-active) {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.content-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100%;
  height: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.content-card :deep(.el-card__body) {
  flex: 1;
  padding: 20px 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  font-size: 20px;
  color: #409eff;
}

.manage-button {
  font-size: 14px;
}

/* 个人信息样式 */
.user-profile-container {
  max-width: 700px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.user-avatar-large {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border: 4px solid #ecf5ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.avatar-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: #f56c6c;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.avatar-badge .el-icon {
  color: #fff;
  font-size: 16px;
}

.user-basic-info {
  flex: 1;
}

.user-name {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.role-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
}

.profile-form {
  margin-top: 20px;
}

.form-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.form-icon {
  font-size: 18px;
  color: #909399;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
}

.save-button {
  padding: 12px 32px;
  font-size: 15px;
  margin-top: 10px;
}

/* 修改密码样式 */
.password-form-container {
  width: 100%;
  min-height: 300px;
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
}

.password-row {
  align-items: flex-start;
  width: 100%;
}

.password-tip {
  margin-top: 0;
}

.tip-list {
  margin: 8px 0 0 0;
  padding-left: 20px;
  color: #606266;
  line-height: 1.8;
}

.tip-list li {
  margin-bottom: 4px;
}

.password-form {
  margin-top: 0;
}

.password-form :deep(.el-form-item) {
  margin-bottom: 28px;
}

.password-form :deep(.el-form-item:last-child) {
  margin-bottom: 0;
  margin-top: 10px;
}

.password-row :deep(.el-col) {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

/* 收货地址样式 */
.address-container {
  min-height: 200px;
}

.address-list {
  display: grid;
  gap: 20px;
}

.address-card {
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.address-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.default-address {
  border: 2px solid #f56c6c;
  background: linear-gradient(to right, #fef0f0 0%, #fff 20%);
}

.address-card-content {
  padding: 5px;
}

.address-header {
  margin-bottom: 16px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.address-icon {
  font-size: 16px;
  color: #909399;
}

.receiver {
  font-weight: 600;
  font-size: 18px;
  color: #303133;
}

.phone {
  color: #606266;
  font-size: 15px;
}

.default-tag {
  font-weight: 500;
  padding: 4px 10px;
}

.address-detail {
  color: #606266;
  line-height: 1.8;
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 15px;
}

.location-icon {
  font-size: 18px;
  color: #409eff;
  margin-top: 2px;
  flex-shrink: 0;
}

.postal-code {
  color: #909399;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile {
    padding: 15px;
  }
  
  .profile-row {
    flex-direction: column;
  }
  
  .profile-row :deep(.el-col) {
    width: 100% !important;
    margin-bottom: 20px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
}
</style>
