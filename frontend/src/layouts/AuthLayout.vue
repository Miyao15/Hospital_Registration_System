<template>
  <div class="auth-layout">
    <header class="main-header">
      <div class="container nav-container">
        <div class="logo-area" @click="goHome">
          <div class="logo-box">优</div>
          <span class="logo-text">优医预约</span>
        </div>
        <nav class="main-nav">
          <span class="nav-text">{{ navText }}</span>
          <button class="btn-secondary" @click="navAction">{{ navButtonText }}</button>
        </nav>
      </div>
    </header>
    <main class="auth-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const goHome = () => router.push('/');

const isLogin = computed(() => route.path.startsWith('/login'));

const navText = computed(() => isLogin.value ? '还没有账户？' : '已有账户？');
const navButtonText = computed(() => isLogin.value ? '立即注册' : '登录');

const navAction = () => {
  if (isLogin.value) {
    router.push('/register');
  } else {
    router.push('/login');
  }
};
</script>

<style scoped>
.auth-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #FFF9E5; /* Consistent background with landing page hero */
}

/* Header styles copied from Landing.vue for consistency */
.main-header {
  height: 64px;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
  z-index: 10;
}
.container {
  max-width: 1080px;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
}
.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo-area {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.logo-box {
  background: #FFD300;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  border-radius: 4px 0 4px 0;
  color: #000;
  font-size: 13px;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #2A2A2A;
}
.main-nav {
  display: flex;
  align-items: center;
  gap: 16px;
}
.nav-text {
  font-size: 14px;
  color: #666;
}
.btn-secondary {
  background: transparent;
  border: 1px solid #ccc;
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 4px;
  cursor: pointer;
  color: #2A2A2A;
  transition: background-color 0.2s;
}
.btn-secondary:hover {
  background-color: #f5f5f5;
}

.auth-main {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 60px 20px 40px;
}
</style>
