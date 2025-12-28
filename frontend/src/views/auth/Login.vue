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
            <span class="nav-text">还没有账户？</span>
            <a href="#" class="nav-link register-link" @click.prevent="goRegister">立即注册</a>
            <button class="btn-signup" @click="goHome">返回首页</button>
          </nav>
        </div>
      </header>
    </div>

    <main class="auth-main">
      <div class="auth-card">
        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">请选择您的身份并登录系统</p>

        <div class="role-selector">
          <button :class="['role-btn', { active: role === 'patient' }]" @click="role = 'patient'">患者登录</button>
          <button :class="['role-btn', { active: role === 'doctor' }]" @click="role = 'doctor'">医生登录</button>
          <button :class="['role-btn', { active: role === 'admin' }]" @click="role = 'admin'">管理员</button>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label v-if="role !== 'admin'">手机号</label>
            <label v-else>用户名</label>
            <input 
              v-if="role !== 'admin'"
              type="tel" 
              v-model="loginForm.identifier" 
              placeholder="请输入您的手机号" 
              required
            >
            <input 
              v-else
              type="text" 
              v-model="loginForm.identifier" 
              placeholder="请输入管理员用户名" 
              required
            >
          </div>
          <div class="form-group" style="margin-top: 20px;">
            <label>密码</label>
            <input 
              type="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码" 
              required
            >
          </div>
          
          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" v-model="loginForm.remember"> 记住我
            </label>
            <a href="#" class="forgot-pwd">忘记密码？</a>
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>
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
const role = ref('patient'); // Default role for UI selection, not sent to backend
const loading = ref(false);

const loginForm = reactive({
  identifier: '',
  password: '',
  remember: false
});

const goHome = () => router.push('/');
const goRegister = () => router.push('/register');

const handleLogin = async () => {
  if (!loginForm.identifier || !loginForm.password) {
    ElMessage.error('请输入手机号/用户名和密码');
    return;
  }
  
  loading.value = true;
  try {
    const loginPayload = {
      identifier: loginForm.identifier,
      password: loginForm.password,
    };
    // Call the store action. The store now handles success/error messages,
    // state management, and redirection.
    await userStore.login(loginPayload);
  } catch (error) {
    // The store's login action already shows an error message.
    // This catch block can be used for component-specific error logic if needed.
    console.error('Login component caught error:', error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-page { background-color: #fff; min-height: 100vh; }
.cream-wrapper { background-color: #FFF9E5; }
.main-header { height: 60px; display: flex; align-items: center; }
.container { max-width: 1080px; margin: 0 auto; padding: 0 20px; width: 100%; }
.nav-container { display: flex; justify-content: space-between; align-items: center; }
.logo-area { display: flex; align-items: center; gap: 6px; cursor: pointer; }
.logo-box { background: #FFD300; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; font-weight: bold; border-radius: 4px 0 4px 0; font-size: 13px; }
.logo-text { font-size: 18px; font-weight: 700; color: #2A2A2A; }
.main-nav { display: flex; align-items: center; gap: 20px; }
.nav-text { font-size: 14px; color: #666; }
.nav-link { text-decoration: none; color: #2A2A2A; font-size: 14px; font-weight: 600; }
.btn-signup { background: #FFD300; border: none; padding: 8px 18px; font-size: 14px; font-weight: 700; border-radius: 4px; cursor: pointer; color: #2A2A2A; }

.auth-main { display: flex; justify-content: center; padding: 60px 20px; }
.auth-card { background-color: #fff; border: 1px solid #E8E8E8; border-radius: 12px; padding: 40px; width: 100%; max-width: 400px; box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
.auth-title { font-size: 28px; font-weight: 700; text-align: center; margin: 0 0 8px; }
.auth-subtitle { font-size: 15px; text-align: center; color: #666; margin-bottom: 32px; }

.role-selector { display: flex; background-color: #f0f0f0; border-radius: 6px; padding: 4px; margin-bottom: 32px; }
.role-btn { flex: 1; padding: 10px; border: none; background: transparent; border-radius: 4px; font-size: 14px; font-weight: 600; color: #666; cursor: pointer; }
.role-btn.active { background: #fff; color: #2A2A2A; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }

.form-group { display: flex; flex-direction: column; }
.form-group label { font-size: 13px; font-weight: 600; margin-bottom: 6px; }
.form-group input { padding: 12px; border: 1px solid #E8E8E8; border-radius: 4px; background: #FAFAFA; font-size: 14px; }
.form-group input:focus { outline: none; border-color: #FFD300; background: #fff; box-shadow: 0 0 0 2px rgba(255, 211, 0, 0.2); }

.form-options { display: flex; justify-content: space-between; align-items: center; margin-top: 16px; font-size: 13px; }
.remember-me { display: flex; align-items: center; gap: 6px; color: #666; cursor: pointer; }
.forgot-pwd { color: #2A2A2A; text-decoration: none; font-weight: 600; }

.btn-submit { width: 100%; background: #FFD300; border: none; padding: 14px; font-size: 16px; font-weight: 700; border-radius: 4px; cursor: pointer; margin-top: 32px; color: #2A2A2A; }
.btn-submit:hover { background: #F4CA00; }
.btn-submit:disabled { background: #ccc; cursor: not-allowed; }
</style>