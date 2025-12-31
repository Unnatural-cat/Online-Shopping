<template>
  <div class="login-container">
    <div class="login-background"></div>
    <div class="login-wrapper">
      <div class="login-card">
        <h2 class="login-title">在线购物 Pro</h2>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="account">
            <el-input 
              v-model="loginForm.account" 
              placeholder="请输入邮箱或手机号"
              size="large"
              class="login-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码" 
              show-password
              size="large"
              class="login-input"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
      </template>
            </el-input>
        </el-form-item>
          <el-form-item>
            <div class="login-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>
        </el-form-item>
        <el-form-item>
            <el-button 
              type="primary" 
              @click="handleLogin" 
              :loading="loading" 
              size="large"
              class="login-button"
            >
              登录
            </el-button>
        </el-form-item>
        <el-form-item>
            <div class="login-footer">
              <el-link type="primary" @click="$router.push('/register')" class="register-link">
                还没有账号？立即注册
              </el-link>
          </div>
        </el-form-item>
      </el-form>
      </div>
      <div class="copyright">
        Copyright © 2024 在线购物 Pro All Rights Reserved.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { showError, showSuccess } from '@/utils/message'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  account: '',
  password: ''
})

// 从localStorage读取记住的账号
onMounted(() => {
  const rememberedAccount = localStorage.getItem('rememberedAccount')
  if (rememberedAccount) {
    loginForm.account = rememberedAccount
    rememberMe.value = true
  }
})

const rules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少8位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 处理记住我功能
        if (rememberMe.value) {
          localStorage.setItem('rememberedAccount', loginForm.account)
        } else {
          localStorage.removeItem('rememberedAccount')
        }
        
        const result = await userStore.loginAction(loginForm)
        if (result.success) {
          showSuccess('登录成功')
          // 如果有重定向参数，使用重定向参数
          if (route.query.redirect) {
            router.push(route.query.redirect)
          } else {
            // 根据用户角色决定跳转位置
            // 管理员跳转到管理端，普通用户跳转到用户端
            if (userStore.isAdmin) {
              router.push('/admin/products')
            } else {
              router.push('/products')
            }
          }
        } else {
          showError(result.message || '登录失败')
        }
      } catch (error) {
        showError('登录失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://images.unsplash.com/photo-1519681393784-d120267933ba?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: blur(8px);
  transform: scale(1.1);
  z-index: 0;
}

.login-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.login-wrapper {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 450px;
  padding: 20px;
}

.login-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 40px 35px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.login-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 40px 0;
  letter-spacing: 1px;
}

.login-form {
  margin-top: 20px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-input {
  width: 100%;
}

.login-input :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 4px;
}

.input-icon {
  color: #909399;
  font-size: 18px;
}

.login-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-options :deep(.el-checkbox) {
  color: #606266;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 4px;
  margin-top: 10px;
}

.login-footer {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

.register-link {
  font-size: 14px;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}

.copyright {
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  margin-top: 30px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    max-width: 90%;
    padding: 15px;
  }
  
  .login-card {
    padding: 30px 25px;
  }
  
  .login-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
}
</style>

