<template>
  <el-container class="patient-layout">
    <!-- 侧边栏 -->
    <el-aside width="250px" class="sidebar">
      <div class="logo">
        <el-icon :size="40" color="#667eea"><UserFilled /></el-icon>
        <h3>患者中心</h3>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/patient/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/patient/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="/patient/appointments">
          <el-icon><Calendar /></el-icon>
          <span>我的预约</span>
        </el-menu-item>
        <el-menu-item index="/patient/doctors">
          <el-icon><Avatar /></el-icon>
          <span>查找医生</span>
        </el-menu-item>
        <el-menu-item index="/patient/records">
          <el-icon><Document /></el-icon>
          <span>就诊记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

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
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="settings">
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
    case 'profile':
      router.push('/patient/profile')
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

.sidebar {
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.logo {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-bottom: 1px solid #f0f0f0;
  
  h3 {
    font-size: 20px;
    color: #333;
    margin: 0;
  }
}

.sidebar-menu {
  border: none;
  padding: 20px 10px;
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
