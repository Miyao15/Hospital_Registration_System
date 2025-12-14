<template>
  <div class="register-container">
    <div class="register-card">
      <!-- 顶部导航 -->
      <div class="register-header">
        <el-icon :size="40" color="#667eea">
          <User />
        </el-icon>
        <h2>用户注册</h2>
        <p>已有账号？<el-link type="primary" @click="goToLogin">立即登录</el-link></p>
      </div>

      <!-- 步骤条 -->
      <el-steps :active="activeStep" align-center class="register-steps">
        <el-step title="选择身份" icon="UserFilled" />
        <el-step title="填写信息" icon="EditPen" />
        <el-step title="完成注册" icon="SuccessFilled" />
      </el-steps>

      <!-- 步骤内容 -->
      <div class="register-content">
        <!-- 步骤 1: 选择身份 -->
        <div v-show="activeStep === 0" class="step-content fade-in-up">
          <h3>请选择您的身份</h3>
          <div class="role-cards">
            <div 
              v-for="role in roles" 
              :key="role.value"
              :class="['role-card', { active: registerForm.role === role.value }]"
              @click="registerForm.role = role.value"
            >
              <el-icon :size="60"><component :is="role.icon" /></el-icon>
              <h4>{{ role.label }}</h4>
              <p>{{ role.description }}</p>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="large" 
            @click="nextStep"
            :disabled="!registerForm.role"
          >
            下一步
          </el-button>
        </div>

        <!-- 步骤 2: 填写信息 -->
        <div v-show="activeStep === 1" class="step-content fade-in-up">
          <h3>填写{{ getRoleLabel() }}信息</h3>
          
          <el-form 
            ref="registerFormRef" 
            :model="registerForm" 
            :rules="registerRules"
            label-width="100px"
            class="register-form"
          >
            <!-- 通用信息 -->
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名" clearable />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码" 
                show-password 
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码" 
                show-password 
              />
            </el-form-item>

            <el-form-item label="姓名" prop="realName">
              <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" clearable />
            </el-form-item>

            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="registerForm.idCard" placeholder="请输入身份证号" clearable />
            </el-form-item>

            <!-- 患者特有信息 -->
            <template v-if="registerForm.role === 'patient'">
              <el-form-item label="过敏史" prop="allergyHistory">
                <el-input 
                  v-model="registerForm.allergyHistory" 
                  type="textarea" 
                  :rows="2"
                  placeholder="请输入过敏史（如有）" 
                />
              </el-form-item>

              <el-form-item label="病史" prop="medicalHistory">
                <el-input 
                  v-model="registerForm.medicalHistory" 
                  type="textarea" 
                  :rows="2"
                  placeholder="请输入既往病史（如有）" 
                />
              </el-form-item>
            </template>

            <!-- 医生特有信息 -->
            <template v-if="registerForm.role === 'doctor'">
              <el-form-item label="工号" prop="employeeId">
                <el-input v-model="registerForm.employeeId" placeholder="请输入工号" clearable />
              </el-form-item>

              <el-form-item label="职称" prop="title">
                <el-select v-model="registerForm.title" placeholder="请选择职称" style="width: 100%">
                  <el-option label="住院医师" value="resident" />
                  <el-option label="主治医师" value="attending" />
                  <el-option label="副主任医师" value="associate" />
                  <el-option label="主任医师" value="chief" />
                </el-select>
              </el-form-item>

              <el-form-item label="科室" prop="department">
                <el-select v-model="registerForm.department" placeholder="请选择科室" style="width: 100%">
                  <el-option label="内科" value="internal" />
                  <el-option label="外科" value="surgery" />
                  <el-option label="儿科" value="pediatrics" />
                  <el-option label="妇科" value="gynecology" />
                  <el-option label="骨科" value="orthopedics" />
                </el-select>
              </el-form-item>

              <el-form-item label="擅长领域" prop="expertise">
                <el-input 
                  v-model="registerForm.expertise" 
                  type="textarea" 
                  :rows="2"
                  placeholder="请输入擅长领域" 
                />
              </el-form-item>

              <el-form-item label="医师资格证" prop="licenseNumber">
                <el-input v-model="registerForm.licenseNumber" placeholder="请输入医师资格证号" clearable />
              </el-form-item>
            </template>

            <el-form-item>
              <el-checkbox v-model="agreeTerms">
                我已阅读并同意
                <el-link type="primary">《用户协议》</el-link>
                和
                <el-link type="primary">《隐私政策》</el-link>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button @click="prevStep">上一步</el-button>
              <el-button 
                type="primary" 
                @click="handleRegister"
                :loading="loading"
                :disabled="!agreeTerms"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 步骤 3: 注册成功 -->
        <div v-show="activeStep === 2" class="step-content success-content fade-in-up">
          <el-result icon="success" title="注册成功！" sub-title="您的账号已创建成功，请登录使用">
            <template #extra>
              <el-button type="primary" size="large" @click="goToLogin">
                前往登录
              </el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const activeStep = ref(0)
const loading = ref(false)
const agreeTerms = ref(false)

const roles = [
  { 
    value: 'patient', 
    label: '患者', 
    icon: 'User',
    description: '预约挂号、查看医生、在线咨询'
  },
  { 
    value: 'doctor', 
    label: '医生', 
    icon: 'UserFilled',
    description: '查看预约、管理患者、坐诊管理'
  },
  { 
    value: 'admin', 
    label: '管理员', 
    icon: 'Management',
    description: '系统管理、用户管理、数据统计'
  }
]

const registerForm = reactive({
  role: '',
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  realName: '',
  idCard: '',
  // 患者特有
  allergyHistory: '',
  medicalHistory: '',
  // 医生特有
  employeeId: '',
  title: '',
  department: '',
  expertise: '',
  licenseNumber: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

const getRoleLabel = () => {
  return roles.find(r => r.value === registerForm.role)?.label || ''
}

const nextStep = () => {
  if (registerForm.role) {
    activeStep.value++
  }
}

const prevStep = () => {
  activeStep.value--
}

const handleRegister = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.registerAction(registerForm)
        if (success) {
          activeStep.value = 2
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 100%;
  max-width: 900px;
  background: white;
  border-radius: 20px;
  padding: 50px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.6s ease-out;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 32px;
    color: #333;
    margin: 20px 0 10px;
  }
  
  p {
    color: #666;
  }
}

.register-steps {
  margin-bottom: 50px;
}

.step-content {
  min-height: 400px;
  
  h3 {
    font-size: 24px;
    color: #333;
    margin-bottom: 30px;
    text-align: center;
  }
}

.role-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.role-card {
  padding: 40px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #667eea;
    background: #f5f7ff;
    transform: translateY(-5px);
  }
  
  &.active {
    border-color: #667eea;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }
  
  h4 {
    font-size: 20px;
    margin: 20px 0 10px;
  }
  
  p {
    font-size: 14px;
    opacity: 0.8;
  }
}

.register-form {
  max-width: 600px;
  margin: 0 auto;
}

.success-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .role-cards {
    grid-template-columns: 1fr;
  }
}
</style>
