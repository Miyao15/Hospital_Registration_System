<template>
  <div class="doctor-layout">
    <!-- Zocdoc 风格顶部导航栏 -->
    <header class="top-navbar">
      <div class="navbar-container">
        <!-- Logo 区域 -->
        <div class="logo-area" @click="$router.push('/doctor/home')">
          <div class="logo-icon">优</div>
          <span class="logo-text">优医医生端</span>
        </div>

        <!-- 中间导航菜单 -->
        <nav class="nav-menu">
          <router-link 
            v-for="item in mainMenuItems" 
            :key="item.path"
            :to="item.path" 
            class="nav-link"
            :class="{ active: isActive(item.path) }"
          >
            {{ item.name }}
          </router-link>
        </nav>

        <!-- 右侧操作区 -->
        <div class="nav-actions">
          <!-- 日期显示 -->
          <div class="date-display">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            <span>{{ currentDate }}</span>
          </div>

          <!-- 通知图标 -->
          <button class="icon-button" @click="$router.push('/doctor/notifications')">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="9">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
                <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                <path d="M13.73 21a2 2 0 01-3.46 0"></path>
              </svg>
            </el-badge>
          </button>

          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" trigger="click" class="user-dropdown">
            <div class="user-trigger">
              <img :src="doctorInfo.avatarUrl || defaultAvatar" class="user-avatar" />
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <polyline points="6 9 12 15 18 9"></polyline>
              </svg>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu">
                <div class="dropdown-header">
                  <div class="dropdown-user-info">
                    <img :src="doctorInfo.avatarUrl || defaultAvatar" class="dropdown-avatar" />
                    <div class="dropdown-text">
                      <div class="dropdown-name">{{ doctorInfo.name || '医生' }}</div>
                      <div class="dropdown-dept">{{ doctorInfo.departmentName || '科室' }}</div>
                    </div>
                  </div>
                </div>
                <el-dropdown-item command="profile">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                    <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                    <circle cx="12" cy="12" r="3"></circle>
                    <path d="M12 1v6m0 6v6m5.2-15.8l-4.3 4.3m-1.8 1.8l-4.3 4.3m16.8 0l-4.3-4.3m-1.8-1.8l-4.3-4.3"></path>
                  </svg>
                  设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                    <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"></path>
                    <polyline points="16 17 21 12 16 7"></polyline>
                    <line x1="21" y1="12" x2="9" y2="12"></line>
                  </svg>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="content-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessageBox, ElMessage } from 'element-plus';
import { getMyDoctorInfo } from '@/api/doctor';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const unreadCount = ref(0);
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const doctorInfo = ref({
  name: '',
  departmentName: '',
  avatarUrl: ''
});

// 主导航菜单项
const mainMenuItems = [
  { name: '工作台', path: '/doctor/home' },
  { name: '预约管理', path: '/doctor/appointments' },
  { name: '排班日历', path: '/doctor/schedule' },
  { name: '请假管理', path: '/doctor/leaves' },
  { name: '我的评价', path: '/doctor/reviews' }
];

const currentDate = computed(() => {
  const now = new Date();
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return `${now.getMonth() + 1}月${now.getDate()}日 ${weekdays[now.getDay()]}`;
});

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/');
};

onMounted(async () => {
  // 获取医生信息 - 先从 userStore 获取基本信息
  if (userStore.userInfo) {
    doctorInfo.value.name = userStore.userInfo.realName || '医生';
  }
  
  // 获取完整的医生信息（包括科室）
  try {
    const data = await getMyDoctorInfo();
    if (data) {
      doctorInfo.value.name = data.name || doctorInfo.value.name;
      doctorInfo.value.departmentName = data.departmentName || '科室';
      doctorInfo.value.avatarUrl = data.avatarUrl || defaultAvatar;
    }
  } catch (error) {
    console.error('获取医生信息失败:', error);
    // 如果API失败，使用默认值
    if (!doctorInfo.value.departmentName) {
      doctorInfo.value.departmentName = '科室';
    }
  }
  
  // TODO: 获取未读通知数量
});

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/doctor/profile');
      break;
    case 'settings':
      router.push('/doctor/settings');
      break;
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout();
        router.push('/login');
      });
      break;
  }
};
</script>

<style scoped>
/* ========== Zocdoc 风格布局 ========== */
.doctor-layout {
  min-height: 100vh;
  background: #FAFAFA;
  display: flex;
  flex-direction: column;
}

/* ========== 顶部导航栏 ========== */
.top-navbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #E8E8E8;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}

.navbar-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo 区域 */
.logo-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo-area:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 24px;
  height: 24px;
  background: #FFD300;
  border-radius: 4px 0 4px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
  color: #2A2A2A;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #2A2A2A;
  letter-spacing: -0.3px;
}

/* 中间导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  text-decoration: none;
  color: #2A2A2A;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #000;
}

.nav-link.active {
  color: #000;
  font-weight: 600;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #FFD300;
}

/* 右侧操作区 */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #F5F5F5;
  border-radius: 6px;
  font-size: 13px;
  color: #2A2A2A;
}

.date-display svg {
  color: #FFD300;
}

.icon-button {
  width: 36px;
  height: 36px;
  background: transparent;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  color: #666;
  position: relative;
}

.icon-button:hover {
  background: #F5F5F5;
}

/* 用户下拉菜单 */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px 4px 4px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: #F5F5F5;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #FFD300;
}

/* 下拉菜单样式 */
:deep(.custom-dropdown-menu) {
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08) !important;
  border: 1px solid #E8E8E8 !important;
  padding: 8px !important;
  min-width: 220px !important;
}

.dropdown-header {
  padding: 12px;
  border-bottom: 1px solid #F0F0F0;
  margin-bottom: 8px;
}

.dropdown-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dropdown-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #FFD300;
}

.dropdown-text {
  flex: 1;
}

.dropdown-name {
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  margin-bottom: 2px;
}

.dropdown-dept {
  font-size: 12px;
  color: #666;
}

:deep(.el-dropdown-menu__item) {
  border-radius: 8px !important;
  margin: 2px 0 !important;
  padding: 10px 12px !important;
  display: flex !important;
  align-items: center !important;
  gap: 12px !important;
  font-size: 14px !important;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #FFF9E5 !important;
}

:deep(.el-dropdown-menu__item svg) {
  color: #666;
}

/* ========== 主内容区 ========== */
.main-content {
  flex: 1;
  overflow-y: auto;
}

.content-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* ========== Badge 自定义 ========== */
:deep(.el-badge__content) {
  background: #FFD300 !important;
  color: #2A2A2A !important;
  font-weight: 700 !important;
  border: 2px solid #fff !important;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .nav-menu {
    gap: 20px;
  }
  
  .date-display span {
    display: none;
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .navbar-container {
    padding: 0 16px;
  }
  
  .content-container {
    padding: 20px 16px;
  }
}

/* ========== 滚动条样式 ========== */
.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.main-content::-webkit-scrollbar-thumb {
  background: #DDD;
  border-radius: 10px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #BBB;
}
</style>
