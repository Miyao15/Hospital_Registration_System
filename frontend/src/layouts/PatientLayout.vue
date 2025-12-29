<template>
  <el-container class="patient-layout">


    <!-- 主体内容 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="notification">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.realName?.charAt(0) }}
              </el-avatar>
              <span>{{ userStore.userInfo?.realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">
                  <el-icon><HomeFilled /></el-icon>首页
                </el-dropdown-item>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="appointments">
                  <el-icon><Calendar /></el-icon>我的预约
                </el-dropdown-item>
                <el-dropdown-item command="doctors">
                  <el-icon><Avatar /></el-icon>查找医生
                </el-dropdown-item>
                <el-dropdown-item command="records">
                  <el-icon><Document /></el-icon>就诊记录
                </el-dropdown-item>
                <el-dropdown-item command="settings" divided>
                  <el-icon><Setting /></el-icon>设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '患者中心')

const handleCommand = (command) => {
  switch (command) {
    case 'home':
      router.push('/patient/home')
      break
    case 'profile':
      router.push('/patient/profile')
      break
    case 'appointments':
      router.push('/patient/appointments')
      break
    case 'doctors':
      router.push('/patient/doctors')
      break
    case 'records':
      router.push('/patient/records')
      break
    case 'settings':
      // TODO: 跳转到设置页面
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        router.push('/login')
      })
      break
  }
}
</script>

<style lang="scss" scoped>
.patient-layout {
  height: 100vh;
  background: #f5f7fa;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.notification {
  cursor: pointer;
  
  &:hover {
    color: #667eea;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  
  span {
    font-size: 14px;
    color: #333;
  }
}

.main-content {
  padding: 30px;
  overflow-y: auto;
}
</style>
