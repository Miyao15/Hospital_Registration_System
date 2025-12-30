<template>
  <div class="dashboard-page">
    
    <section class="dashboard-hero">
      <div class="container hero-container">
        <p class="hero-subtitle">Find and book appointments with local doctors.</p>        
        <div class="search-bar-wrapper">
          <div class="search-bar">
            <div class="input-group">
              <el-icon class="input-icon"><Search /></el-icon>
              <input type="text" v-model="searchForm.specialty" placeholder="病情、治疗或医生姓名..." />
            </div>
            <div class="divider"></div>
            <div class="input-group">
              <el-icon class="input-icon"><MapLocation /></el-icon>
              <input type="text" v-model="searchForm.location" placeholder="城市、街道或邮编" />
            </div>
            <div class="divider"></div>

            <button class="btn-search-submit" @click="goToSearch">
              <el-icon><Search /></el-icon>
            </button>
          </div>
        </div>
      </div>
    </section>

    <div class="container main-content">
      
      <div class="left-col">
        <MedicalItems />
      </div>

      <div class="right-col">
        <div class="sidebar-card medical-card">
          <div class="medical-card-image">
            <img src="https://images.unsplash.com/photo-1576091160399-112ba8d25d1f?w=400&h=300&fit=crop" alt="医疗健康" />
          </div>
          <div class="medical-card-content">
            <h3 class="sidebar-title">您的健康档案</h3>
            <p class="medical-card-desc">管理您的医疗记录，随时查看就诊历史</p>
            <button class="btn-medical-card" @click="$router.push('/patient/medical-records')">查看档案</button>
          </div>
        </div>

        <div class="sidebar-card">
          <h3 class="sidebar-title">您的医疗团队</h3>
          <div class="care-list">
            <div class="care-item">
              <div class="avatar-placeholder">
                <el-icon><User /></el-icon>
              </div>
              <div class="care-info">
                <span>寻找家庭医生</span>
                <small>Primary Care</small>
              </div>
              <button class="btn-xs-outline">添加</button>
            </div>
            <div class="care-divider"></div>
            <div class="care-item">
              <div class="avatar-placeholder">
                <el-icon><FirstAidKit /></el-icon>
              </div>
              <div class="care-info">
                <span>寻找牙医</span>
                <small>Dentist</small>
              </div>
              <button class="btn-xs-outline">添加</button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, MapLocation, User, FirstAidKit } from '@element-plus/icons-vue';
import MedicalItems from '@/views/patient/MedicalItems.vue';

const router = useRouter()

const searchForm = ref({
  specialty: '',
  location: ''
});

const goToSearch = () => {
  router.push({
    path: '/search-results',
    query: { ...searchForm.value }
  });
};
</script>

<style scoped>
/* === 全局容器与字体 === */
.dashboard-page {
  background-color: #FAFAFA; /* 浅灰底色，突出白色卡片 */
  min-height: 100vh;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #2A2A2A;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}

/* === 1. Hero 区域 (米黄色背景) === */
.dashboard-hero {
  background-color: #FFF9E5; /* Zocdoc 标志性米黄 */
  padding: 60px 0 80px;
  text-align: center;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #2A2A2A;
}

.hero-subtitle {
  font-size: 18px;
  color: #555;
  margin-bottom: 40px;
}

/* 搜索栏样式优化 */
.search-bar-wrapper {
  display: flex;
  justify-content: center;
}

.search-bar {
  background: #fff;
  border-radius: 50px; /* 大圆角 */
  box-shadow: 0 4px 20px rgba(0,0,0,0.08); /* 柔和阴影 */
  display: flex;
  align-items: center;
  padding: 6px;
  width: 100%;
  max-width: 900px;
  height: 60px;
  box-sizing: border-box;
}

.input-group {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 100%;
}

.input-icon {
  font-size: 18px;
  color: #999;
  margin-right: 10px;
}

.input-group input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 15px;
  color: #333;
  background: transparent;
}

.divider {
  width: 1px;
  height: 30px;
  background-color: #E0E0E0;
}

.btn-search-submit {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #FFD300; /* Zocdoc 黄 */
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 20px;
  color: #000;
  flex-shrink: 0;
}

.btn-search-submit:hover {
  background-color: #F4CA00;
}

/* === 2. 主体分栏布局 === */
.main-content {
  display: flex;
  gap: 40px;
  margin-top: -30px; /* 稍微上移，压在 Hero 区域上一点点（可选） */
  padding-bottom: 60px;
}

.left-col { flex: 65%; }
.right-col { flex: 35%; }

/* 左侧 Header */
.section-header { margin-bottom: 24px; }
.section-header h2 { font-size: 24px; font-weight: 700; margin: 0 0 4px; }
.subtitle { font-size: 14px; color: #666; }

/* 进度条 */
.progress-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 30px;
}
.progress-track {
  flex-grow: 1;
  height: 8px;
  background-color: #E0E0E0;
  border-radius: 4px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background-color: #2A2A2A;
  border-radius: 4px;
}
.progress-label {
  font-size: 13px;
  font-weight: 600;
  color: #666;
  white-space: nowrap;
}

/* 任务卡片 (核心样式) */
.task-card {
  background: #fff;
  border: 1px solid #E5E5E5;
  border-radius: 8px; /* 卡片圆角 */
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 16px;
  transition: box-shadow 0.2s;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.task-icon-wrapper {
  width: 48px;
  height: 48px;
  background-color: #FFF9E5; /* 浅黄背景 */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.task-content { flex: 1; }

.task-badges { margin-bottom: 6px; }
.badge-due {
  background-color: #FDECEA;
  color: #D93025;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
}

.task-content h3 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
}

.task-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #555;
}

.task-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 100px;
}

.btn-text {
  background: none;
  border: none;
  font-size: 13px;
  color: #2A2A2A;
  text-decoration: underline;
  cursor: pointer;
  padding: 0;
}

.btn-outline {
  background: #fff;
  border: 1px solid #CCC;
  border-radius: 4px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-outline:hover {
  border-color: #2A2A2A;
  background-color: #F8F8F8;
}

/* 右侧边栏 */
.sidebar-card {
  background: #fff;
  border: 1px solid #E5E5E5;
  border-radius: 8px;
  padding: 24px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px;
}

.care-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  background-color: #F0F0F0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 20px;
}

.care-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.care-info span {
  font-size: 14px;
  font-weight: 500;
  color: #2A2A2A;
}

.care-info small {
  font-size: 12px;
  color: #777;
}

.text-blue { color: #0065D6 !important; cursor: pointer; }

.btn-xs-outline {
  background: #fff;
  border: 1px solid #DDD;
  border-radius: 4px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}
.btn-xs-outline:hover { border-color: #999; }

.care-divider {
  height: 1px;
  background-color: #EEE;
  margin: 12px 0;
}

/* 医疗卡片 */
.medical-card {
  padding: 0;
  overflow: hidden;
  margin-bottom: 20px;
}

.medical-card-image {
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.medical-card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.medical-card:hover .medical-card-image img {
  transform: scale(1.05);
}

.medical-card-content {
  padding: 20px;
}

.medical-card-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  margin: 8px 0 16px 0;
}

.btn-medical-card {
  width: 100%;
  background-color: #FFD300;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  color: #2A2A2A;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-medical-card:hover {
  background-color: #F4CA00;
}

/* 响应式适配 */
@media (max-width: 900px) {
  .main-content { flex-direction: column; }
  .search-bar { flex-direction: column; height: auto; border-radius: 12px; padding: 12px; }
  .input-group { width: 100%; border-bottom: 1px solid #EEE; padding: 12px 0; }
  .divider { display: none; }
  .btn-search-submit { width: 100%; border-radius: 8px; margin-top: 10px; }
}
</style>