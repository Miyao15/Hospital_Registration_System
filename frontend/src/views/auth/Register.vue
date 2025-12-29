<template>
  <div class="auth-page">
    <div class="cream-wrapper">
      <header class="main-header">
        <div class="container nav-container">
          <div class="logo-area" @click="goHome">
            <div class="logo-box">优</div>
            <span class="logo-text">优医预约</span>
          </div>
          <nav class="main-nav">
            <span class="nav-text">已有账户？</span>
            <a href="#" class="nav-link login-link" @click.prevent="goLogin">登录</a>
            <button class="btn-signup" @click="goHome">返回首页</button>
          </nav>
        </div>
      </header>
    </div>

    <main class="auth-main">
      <div class="auth-card">
        <h1 class="auth-title">创建您的专属账户</h1>
        <p class="auth-subtitle">加入优医，享受便捷的在线医疗服务</p>

        <div class="role-selector">
          <button 
            :class="['role-btn', { active: role === 'patient' }]" 
            @click="role = 'patient'">
            我是患者
          </button>
          <button 
            :class="['role-btn', { active: role === 'doctor' }]" 
            @click="role = 'doctor'">
            我是医生
          </button>
          <button 
            :class="['role-btn', { active: role === 'admin' }]" 
            @click="role = 'admin'">
            管理员注册
          </button>
        </div>

        <!-- Patient Registration Form -->
        <form v-if="role === 'patient'" @submit.prevent="handleRegister">
          <div class="form-grid">
            <div class="form-group">
              <label for="patient-name">姓名</label>
              <input type="text" id="patient-name" v-model="patientForm.name" placeholder="请输入您的真实姓名" required>
            </div>
            <div class="form-group">
              <label for="patient-phone">手机号</label>
              <input type="tel" id="patient-phone" v-model="patientForm.phone" placeholder="用于登录和接收通知" required>
            </div>
            <div class="form-group">
              <label for="patient-id-card">身份证号</label>
              <input type="text" id="patient-id-card" v-model="patientForm.idCard" placeholder="请输入18位身份证号" required>
            </div>
            <div class="form-group">
              <label for="patient-birthdate">出生日期</label>
              <input 
                type="text" 
                id="patient-birthdate" 
                v-model="patientForm.birthDate" 
                placeholder="yyyy-mm-dd" 
                onfocus="(this.type='date')" 
                onblur="if(!this.value)this.type='text'"
                required>
            </div>
             <div class="form-group">
              <label>性别</label>
              <div class="gender-options">
                <label><input type="radio" v-model="patientForm.gender" value="MALE"> 男</label>
                <label><input type="radio" v-model="patientForm.gender" value="FEMALE"> 女</label>
              </div>
            </div>
            <div class="form-group">
              <label for="patient-password">设置密码</label>
              <input type="password" id="patient-password" v-model="patientForm.password" placeholder="至少6位，区分大小写" required>
            </div>
          </div>
          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '注册中...' : '同意协议并注册' }}
          </button>
        </form>

        <!-- Doctor Registration Form -->
        <form v-if="role === 'doctor'" @submit.prevent="handleRegister">
          <div class="form-grid">
            <div class="form-group">
              <label for="doctor-name">姓名</label>
              <input type="text" id="doctor-name" v-model="doctorForm.name" placeholder="请输入您的真实姓名" required>
            </div>
             <div class="form-group">
              <label for="doctor-phone">手机号</label>
              <input type="tel" id="doctor-phone" v-model="doctorForm.phone" placeholder="用于登录和接收通知" required>
            </div>
            <div class="form-group">
              <label for="doctor-employeeId">工号</label>
              <input type="text" id="doctor-employeeId" v-model="doctorForm.employeeId" placeholder="请输入您的工号" required>
            </div>
            <div class="form-group">
              <label for="doctor-department">科室</label>
              <input type="text" id="doctor-department" v-model="doctorForm.departmentId" placeholder="请输入科室ID" required>
            </div>
            <div class="form-group">
              <label for="doctor-title">职称</label>
              <select id="doctor-title" v-model="doctorForm.title" required>
                <option disabled value="">请选择您的职称</option>
                <option value="RESIDENT">住院医师</option>
                <option value="ATTENDING">主治医师</option>
                <option value="DEPUTY_CHIEF_PHYSICIAN">副主任医师</option>
                <option value="CHIEF">主任医师</option>
              </select>
            </div>
             <div class="form-group">
              <label for="doctor-license">医师资格证号</label>
              <input type="text" id="doctor-license" v-model="doctorForm.licenseNumber" placeholder="请输入资格证号" required>
            </div>
            <div class="form-group">
              <label for="doctor-password">设置密码</label>
              <input type="password" id="doctor-password" v-model="doctorForm.password" placeholder="至少6位，区分大小写" required>
            </div>
          </div>
          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '注册中...' : '提交审核' }}
          </button>
        </form>

        <!-- Admin Registration Form -->
        <form v-if="role === 'admin'" @submit.prevent="handleRegister">
          <div class="form-grid">
            <div class="form-group">
              <label for="admin-name">姓名</label>
              <input type="text" id="admin-name" v-model="adminForm.name" placeholder="请输入您的真实姓名" required>
            </div>
            <div class="form-group">
              <label for="admin-employeeId">工号</label>
              <input type="text" id="admin-employeeId" v-model="adminForm.employeeId" placeholder="请输入您的工号" required>
            </div>
            <div class="form-group">
              <label for="admin-phone">手机号</label>
              <input type="tel" id="admin-phone" v-model="adminForm.phone" placeholder="用于登录和接收通知" required>
            </div>
            <div class="form-group">
              <label for="admin-password">设置密码</label>
              <input type="password" id="admin-password" v-model="adminForm.password" placeholder="请确保密码强度" required>
            </div>
            <div class="form-group" style="grid-column: span 2;">
              <label for="admin-key">管理员注册密钥</label>
              <input type="password" id="admin-key" v-model="adminForm.adminRegistrationKey" placeholder="请输入管理员注册密钥" required>
            </div>
          </div>
          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '注册中...' : '创建管理员账户' }}
          </button>
        </form>

         <div class="auth-footer">
          注册或登录即代表您同意我们的 <a href="#">服务条款</a> 和 <a href="#">隐私政策</a>.
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();
const role = ref('patient'); // 'patient' or 'doctor' or 'admin'
const loading = ref(false);

const patientForm = reactive({
  phone: '',
  password: '',
  name: '',
  idCard: '',
  gender: 'MALE',
  birthDate: '',
});

const doctorForm = reactive({
  phone: '',
  password: '',
  name: '',
  employeeId: '',
  title: '',
  departmentId: '',
  licenseNumber: '',
  specialty: 'General', // Add default for fields not in form
  introduction: 'N/A',   // Add default for fields not in form
});

const adminForm = reactive({
  name: '',
  phone: '',
  password: '',
  employeeId: '',
  adminRegistrationKey: ''
});

const goHome = () => router.push('/');
const goLogin = () => router.push('/login');

const handleRegister = async () => {
  loading.value = true;
  try {
    let success = false;
    if (role.value === 'patient') {
      success = await userStore.register(patientForm, 'patient');
    } else if (role.value === 'doctor') {
      if (!doctorForm.title) {
        ElMessage.error('请选择您的职称');
        loading.value = false;
        return;
      }
      success = await userStore.register(doctorForm, 'doctor');
    } else if (role.value === 'admin') {
      success = await userStore.register(adminForm, 'admin');
    }

    if (success) {
      router.push('/login');
    }
  } catch (error) {
    // Store action already displays the error message.
    console.error('Registration failed in component:', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 继承 Landing.vue 的风格 */
.auth-page { background-color: #fff; min-height: 100vh; }
.cream-wrapper { background-color: #FFF9E5; padding-bottom: 0; }
.main-header { height: 60px; display: flex; align-items: center; position: relative; z-index: 50; }
.container { max-width: 1080px; margin: 0 auto; padding: 0 20px; position: relative; height: 100%; }
.nav-container { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.logo-area { display: flex; align-items: center; gap: 6px; cursor: pointer; }
.logo-box { background: #FFD300; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; font-weight: bold; border-radius: 4px 0 4px 0; color: #000; font-size: 13px; }
.logo-text { font-size: 18px; font-weight: 700; color: #2A2A2A; }
.main-nav { display: flex; align-items: center; gap: 20px; }
.nav-text { font-size: 14px; color: #666; }
.nav-link { text-decoration: none; color: #2A2A2A; font-size: 14px; font-weight: 600; }
.btn-signup { background: #FFD300; border: none; padding: 8px 18px; font-size: 14px; font-weight: 700; border-radius: 4px; cursor: pointer; transition: background 0.2s; color: #2A2A2A; }
.btn-signup:hover { background: #F4CA00; }

/* Auth Card Styles */
.auth-main { display: flex; justify-content: center; align-items: flex-start; padding: 60px 20px; background-color: #fff; }
.auth-card { background-color: #fff; border: 1px solid #E8E8E8; border-radius: 12px; padding: 40px; width: 100%; max-width: 600px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
.auth-title { font-size: 28px; font-weight: 700; text-align: center; color: #2A2A2A; margin: 0 0 8px; }
.auth-subtitle { font-size: 16px; text-align: center; color: #666; margin-bottom: 32px; }

/* Role Selector */
.role-selector { display: flex; justify-content: center; margin-bottom: 32px; background-color: #f0f0f0; border-radius: 6px; padding: 4px; }
.role-btn { flex: 1; padding: 10px; border: none; background-color: transparent; border-radius: 4px; font-size: 14px; font-weight: 600; color: #666; cursor: pointer; transition: all 0.2s ease-in-out; }
.role-btn.active { background-color: #fff; color: #2A2A2A; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }

/* Form Styles */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px 24px; }
.form-group { display: flex; flex-direction: column; }
.form-group label { font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; }
.form-group input, .form-group select { width: 100%; padding: 12px; font-size: 14px; border: 1px solid #E8E8E8; border-radius: 4px; background-color: #FAFAFA; box-sizing: border-box; }
.form-group input:focus, .form-group select:focus { outline: none; border-color: #FFD300; background-color: #fff; box-shadow: 0 0 0 2px rgba(255, 211, 0, 0.3); }

.gender-options { display: flex; align-items: center; gap: 24px; height: 45px; }
.gender-options label { display: flex; align-items: center; gap: 6px; font-weight: normal; }

.btn-submit { width: 100%; background: #FFD300; border: none; padding: 14px; font-size: 16px; font-weight: 700; border-radius: 4px; cursor: pointer; transition: background 0.2s; color: #2A2A2A; margin-top: 32px; }
.btn-submit:hover { background: #F4CA00; }
.btn-submit:disabled { background-color: #ccc; cursor: not-allowed; }

.auth-footer { text-align: center; margin-top: 24px; font-size: 12px; color: #888; }
.auth-footer a { color: #555; text-decoration: none; font-weight: 600; }
.auth-footer a:hover { text-decoration: underline; }

@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
  .auth-card { padding: 30px 25px; }
}
</style>