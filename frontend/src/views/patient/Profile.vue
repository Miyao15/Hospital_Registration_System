<template>
  <div class="patient-profile">
    <el-card class="card-shadow">
      <template #header>
        <div class="card-header">
          <span class="card-title">个人信息</span>
          <el-button 
            v-if="!isEditing" 
            type="primary" 
            @click="isEditing = true"
          >
            <el-icon><Edit /></el-icon>
            编辑信息
          </el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" :loading="loading" @click="saveProfile">
              保存
            </el-button>
          </div>
        </div>
      </template>

      <div class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <el-avatar :size="120" :src="profileForm.avatar">
            {{ profileForm.realName?.charAt(0) }}
          </el-avatar>
          <el-button v-if="isEditing" type="text" class="upload-avatar">
            <el-icon><Upload /></el-icon>
            更换头像
          </el-button>
          <div class="user-basic">
            <h2>{{ profileForm.realName }}</h2>
            <p>{{ profileForm.phone }}</p>
          </div>
        </div>

        <!-- 信息表单 -->
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="120px"
          class="profile-form"
          :disabled="!isEditing"
        >
          <el-divider content-position="left">基本信息</el-divider>
          
          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="profileForm.gender">
                  <el-radio label="male">男</el-radio>
                  <el-radio label="female">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="profileForm.idCard" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="profileForm.birthDate"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input v-model="profileForm.age" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="联系地址" prop="address">
            <el-input v-model="profileForm.address" />
          </el-form-item>

          <el-divider content-position="left">医疗信息</el-divider>

          <el-form-item label="血型" prop="bloodType">
            <el-select v-model="profileForm.bloodType" placeholder="请选择血型">
              <el-option label="A型" value="A" />
              <el-option label="B型" value="B" />
              <el-option label="AB型" value="AB" />
              <el-option label="O型" value="O" />
              <el-option label="未知" value="unknown" />
            </el-select>
          </el-form-item>

          <el-form-item label="过敏史" prop="allergyHistory">
            <el-input
              v-model="profileForm.allergyHistory"
              type="textarea"
              :rows="3"
              placeholder="请详细描述您的过敏史（如药物过敏、食物过敏等）"
            />
          </el-form-item>

          <el-form-item label="既往病史" prop="medicalHistory">
            <el-input
              v-model="profileForm.medicalHistory"
              type="textarea"
              :rows="3"
              placeholder="请描述您的既往病史（如慢性病、手术史等）"
            />
          </el-form-item>

          <el-form-item label="家族病史" prop="familyHistory">
            <el-input
              v-model="profileForm.familyHistory"
              type="textarea"
              :rows="3"
              placeholder="请描述家族遗传病史"
            />
          </el-form-item>

          <el-divider content-position="left">紧急联系人</el-divider>

          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="联系人姓名" prop="emergencyContact">
                <el-input v-model="profileForm.emergencyContact" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="与本人关系" prop="emergencyRelation">
                <el-select v-model="profileForm.emergencyRelation" placeholder="请选择">
                  <el-option label="配偶" value="spouse" />
                  <el-option label="父母" value="parent" />
                  <el-option label="子女" value="child" />
                  <el-option label="兄弟姐妹" value="sibling" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="联系人电话" prop="emergencyPhone">
            <el-input v-model="profileForm.emergencyPhone" />
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="card-shadow password-card">
      <template #header>
        <span class="card-title">安全设置</span>
      </template>
      <div class="password-section">
        <div class="password-item">
          <div class="password-info">
            <h4>登录密码</h4>
            <p>定期更换密码可以保护账号安全</p>
          </div>
          <el-button type="primary" @click="showPasswordDialog = true">
            修改密码
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="500px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="changePassword">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { changePassword as changePasswordApi } from '@/api/auth'

const userStore = useUserStore()
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

const isEditing = ref(false)
const loading = ref(false)
const showPasswordDialog = ref(false)
const passwordLoading = ref(false)

const profileForm = reactive({
  avatar: '',
  realName: '',
  gender: 'male',
  phone: '',
  idCard: '',
  birthDate: '',
  age: '',
  address: '',
  bloodType: '',
  allergyHistory: '',
  medicalHistory: '',
  familyHistory: '',
  emergencyContact: '',
  emergencyRelation: '',
  emergencyPhone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与原密码相同'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, validator: validateNewPassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
}

// 计算年龄
const calculateAge = computed(() => {
  if (!profileForm.birthDate) return ''
  const birth = new Date(profileForm.birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age
})

const loadProfile = () => {
  // 从 userStore 加载数据
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(profileForm, {
      avatar: userInfo.avatar || '',
      realName: userInfo.realName || '',
      gender: userInfo.gender || 'male',
      phone: userInfo.phone || '',
      idCard: userInfo.idCard || '',
      birthDate: userInfo.birthDate || '',
      age: calculateAge.value,
      address: userInfo.address || '',
      bloodType: userInfo.bloodType || '',
      allergyHistory: userInfo.allergyHistory || '',
      medicalHistory: userInfo.medicalHistory || '',
      familyHistory: userInfo.familyHistory || '',
      emergencyContact: userInfo.emergencyContact || '',
      emergencyRelation: userInfo.emergencyRelation || '',
      emergencyPhone: userInfo.emergencyPhone || ''
    })
  }
}

const saveProfile = async () => {
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.updateUserInfoAction(profileForm)
        if (success) {
          isEditing.value = false
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const cancelEdit = () => {
  isEditing.value = false
  loadProfile()
}

const changePassword = async () => {
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await changePasswordApi(passwordForm)
        ElMessage.success('密码修改成功，请重新登录')
        showPasswordDialog.value = false
        // 清空表单
        Object.assign(passwordForm, {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        })
        // 退出登录
        setTimeout(() => {
          userStore.logout()
          router.push('/login')
        }, 1500)
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style lang="scss" scoped>
.patient-profile {
  max-width: 1200px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }

  .profile-content {
    padding: 20px 0;
  }

  .avatar-section {
    text-align: center;
    padding: 30px 0;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 40px;

    .upload-avatar {
      display: block;
      margin: 15px auto 0;
    }

    .user-basic {
      margin-top: 20px;

      h2 {
        font-size: 24px;
        color: #333;
        margin-bottom: 8px;
      }

      p {
        color: #666;
      }
    }
  }

  .profile-form {
    max-width: 900px;
    margin: 0 auto;
  }

  .password-card {
    margin-top: 20px;
  }

  .password-section {
    .password-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;

      .password-info {
        h4 {
          font-size: 16px;
          color: #333;
          margin-bottom: 8px;
        }

        p {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}
</style>
