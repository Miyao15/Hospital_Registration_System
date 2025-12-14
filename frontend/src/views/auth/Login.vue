<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 左侧装饰 -->
      <div class="login-left">
        <div class="logo-section">
          <el-icon :size="80" color="#fff">
            <User />
          </el-icon>
          <h1>医院门诊预约挂号系统</h1>
          <p>Hospital Registration System</p>
        </div>
        <div class="features">
          <div class="feature-item" v-for="(feature, index) in features" :key="index">
            <el-icon :size="24"><component :is="feature.icon" /></el-icon>
            <span>{{ feature.text }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2>欢迎登录</h2>
          <p class="subtitle">请选择您的身份并登录</p>

          <!-- 角色选择 -->
          <div class="role-tabs">
            <div 
              v-for="role in roles" 
              :key="role.value"
              :class="['role-tab', { active: loginForm.role === role.value }]"
              @click="loginForm.role = role.value"
            >
              <el-icon :size="30"><component :is="role.icon" /></el-icon>
              <span>{{ role.label }}</span>
            </div>
          </div>

          <!-- 登录表单 -->
          <el-form 
            ref="loginFormRef" 
            :model="loginForm" 
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名/手机号"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="primary" :underline="false">忘记密码？</el-link>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>

            <div class="register-link">
              还没有账号？
              <el-link type="primary" @click="goToRegister">立即注册</el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock, Checked, ChatDotRound, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const roles = [
  { value: 'patient', label: '患者', icon: 'User' },
  { value: 'doctor', label: '医生', icon: 'UserFilled' },
  { value: 'admin', label: '管理员', icon: 'Management' }
]

const features = [
  { icon: 'Checked', text: '快速预约挂号' },
  { icon: 'ChatDotRound', text: '在线咨询' },
  { icon: 'TrendCharts', text: '智能推荐' }
]

const loginForm = reactive({
  username: '',
  password: '',
  role: 'patient'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.loginAction(loginForm)
        if (success) {
          // 根据角色跳转到不同页面
          const roleRoutes = {
            patient: '/patient/home',
            doctor: '/doctor/home',
            admin: '/admin/home'
          }
          router.push(roleRoutes[loginForm.role])
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 650px;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.6s ease-out;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 40px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.logo-section {
  text-align: center;
  
  h1 {
    font-size: 32px;
    margin: 20px 0 10px;
    font-weight: 600;
  }
  
  p {
    font-size: 14px;
    opacity: 0.9;
  }
}

.features {
  margin-top: 60px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  margin-bottom: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateX(10px);
  }
  
  span {
    font-size: 16px;
  }
}

.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
  
  h2 {
    font-size: 32px;
    color: #333;
    margin-bottom: 10px;
  }
  
  .subtitle {
    color: #666;
    margin-bottom: 30px;
  }
}

.role-tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.role-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 10px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #667eea;
    background: #f5f7ff;
  }
  
  &.active {
    border-color: #667eea;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  span {
    font-size: 14px;
    font-weight: 500;
  }
}

.login-form {
  margin-top: 20px;
}

.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  
  &:hover {
    opacity: 0.9;
  }
}

.register-link {
  text-align: center;
  color: #666;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
  }
  
  .login-left {
    padding: 40px 20px;
  }
  
  .login-right {
    padding: 40px 20px;
  }
}
</style>
