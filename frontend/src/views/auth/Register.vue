<template>
  <div class="register-container">
    <div class="register-background"></div>
    <div class="register-wrapper">
      <div class="register-card">
        <h2 class="register-title">用户注册</h2>
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
          <el-form-item prop="email">
            <el-input 
              v-model="registerForm.email" 
              placeholder="请输入邮箱"
              size="large"
              class="register-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Message /></el-icon>
      </template>
            </el-input>
        </el-form-item>
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              size="large"
              class="register-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Phone /></el-icon>
              </template>
            </el-input>
        </el-form-item>
          <el-form-item prop="nickname">
            <el-input 
              v-model="registerForm.nickname" 
              placeholder="请输入昵称"
              size="large"
              class="register-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><User /></el-icon>
              </template>
            </el-input>
        </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="至少8位，包含字母和数字" 
              show-password
              size="large"
              class="register-input"
              @keyup.enter="handleRegister"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
        </el-form-item>
        <el-form-item>
            <el-button 
              type="primary" 
              @click="handleRegister" 
              :loading="loading" 
              size="large"
              class="register-button"
            >
              注册
            </el-button>
        </el-form-item>
        <el-form-item>
            <div class="register-footer">
              <el-link type="primary" @click="$router.push('/login')" class="login-link">
                已有账号？立即登录
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { showError, showSuccess } from '@/utils/message'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  email: '',
  phone: '',
  nickname: '',
  password: ''
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

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await userStore.registerAction(registerForm)
        if (result.success) {
          showSuccess(result.message || '注册成功')
          router.push('/login')
        } else {
          showError(result.message || '注册失败')
        }
      } catch (error) {
        showError('注册失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
}

.register-background {
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

.register-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.register-wrapper {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 500px;
  padding: 20px;
}

.register-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 40px 35px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.register-title {
  text-align: center;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 40px 0;
  letter-spacing: 1px;
}

.register-form {
  margin-top: 20px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.register-input {
  width: 100%;
}

.register-input :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 4px;
}

.input-icon {
  color: #909399;
  font-size: 18px;
}

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 4px;
  margin-top: 10px;
}

.register-footer {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

.login-link {
  font-size: 14px;
  text-decoration: none;
}

.login-link:hover {
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
  .register-wrapper {
    max-width: 90%;
    padding: 15px;
  }
  
  .register-card {
    padding: 30px 25px;
  }
  
  .register-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
}
</style>

