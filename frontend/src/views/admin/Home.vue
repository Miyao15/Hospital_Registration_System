<template>
  <div class="admin-home page-animate">
    <!-- Hero区域 - Oatmeal风格 -->
    <div class="hero-section slide-down">
      <div class="hero-content">
        <div class="hero-text fade-in">
          <h1 class="hero-title">欢迎回来，{{ adminName }}</h1>
          <p class="hero-subtitle">管理系统概览 · 一切尽在掌握</p>
        </div>
        <div class="hero-image scale-in">
          <div class="hero-image-overlay"></div>
          <img src="https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600&h=400&fit=crop&q=80" alt="管理后台" />
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card card-hover stagger-item">
        <div class="stat-icon" style="background: #E3F2FD;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#1976D2" stroke-width="2" width="24" height="24">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 00-3-3.87"></path>
            <path d="M16 3.13a4 4 0 010 7.75"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalPatients || 0 }}</div>
          <div class="stat-label">总患者数</div>
        </div>
      </div>

      <div class="stat-card card-hover stagger-item">
        <div class="stat-icon" style="background: #E8F5E9;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#388E3C" stroke-width="2" width="24" height="24">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalDoctors || 0 }}</div>
          <div class="stat-label">总医生数</div>
        </div>
      </div>

      <div class="stat-card card-hover stagger-item">
        <div class="stat-icon" style="background: #FFF3E0;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#F57C00" stroke-width="2" width="24" height="24">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.totalAppointments || 0 }}</div>
          <div class="stat-label">总预约数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: #FCE4EC;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#C2185B" stroke-width="2" width="24" height="24">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.todayAppointments || 0 }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
    </div>

    <!-- 今日统计 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">今日统计</h2>
      </div>
      <div class="today-stats-grid">
        <div class="today-stat-card">
          <div class="today-stat-label">已签到</div>
          <div class="today-stat-value">{{ statistics.todayCheckedIn || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">已完成</div>
          <div class="today-stat-value completed">{{ statistics.todayCompleted || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">爽约</div>
          <div class="today-stat-value no-show">{{ statistics.todayNoShow || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">就诊率</div>
          <div class="today-stat-value">{{ (statistics.checkInRate || 0).toFixed(1) }}%</div>
        </div>
      </div>
    </div>

    <!-- 待处理事项 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">待处理事项</h2>
      </div>

      <div class="tasks-grid">
        <!-- 待审批医生 -->
        <div class="task-card" @click="$router.push('/admin/doctors')">
          <div class="task-header">
            <div class="task-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
                <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
                <circle cx="8.5" cy="7" r="4"></circle>
                <polyline points="17 11 19 13 23 9"></polyline>
              </svg>
            </div>
            <span class="task-count">{{ stats.pendingDoctors }}</span>
          </div>
          <div class="task-title">待审批医生</div>
          <div class="task-action">查看详情 →</div>
        </div>

        <!-- 待审批请假 -->
        <div class="task-card" @click="$router.push('/admin/leaves')">
          <div class="task-header">
            <div class="task-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
                <line x1="8" y1="14" x2="16" y2="14"></line>
              </svg>
            </div>
            <span class="task-count">{{ stats.pendingLeaves }}</span>
          </div>
          <div class="task-title">待审批请假</div>
          <div class="task-action">查看详情 →</div>
        </div>
      </div>
    </div>

    <!-- 科室预约排行 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">科室预约排行（近30天）</h2>
      </div>
      <div class="ranking-table">
        <el-table :data="statistics.departmentRanking || []" stripe style="width: 100%">
          <el-table-column type="index" label="排名" width="80" align="center" />
          <el-table-column prop="departmentName" label="科室名称" />
          <el-table-column prop="count" label="预约数量" align="center" />
        </el-table>
      </div>
    </div>

    <!-- 医生工作量排行 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">医生工作量排行（近30天）</h2>
      </div>
      <div class="ranking-table">
        <el-table :data="statistics.doctorWorkload || []" stripe style="width: 100%">
          <el-table-column type="index" label="排名" width="80" align="center" />
          <el-table-column prop="doctorName" label="医生姓名" />
          <el-table-column prop="title" label="职称" />
          <el-table-column prop="count" label="就诊数量" align="center" />
        </el-table>
      </div>
    </div>

    <!-- 功能卡片区域 - Oatmeal风格 -->
    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card" @click="$router.push('/admin/departments')">
          <div class="feature-icon" style="background: #E3F2FD;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
          </div>
          <h3 class="feature-title">科室管理</h3>
          <p class="feature-desc">管理医院科室信息，维护科室数据</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/admin/schedules')">
          <div class="feature-icon" style="background: #E8F5E9;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <h3 class="feature-title">排班管理</h3>
          <p class="feature-desc">设置和管理医生排班计划</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/admin/users')">
          <div class="feature-icon" style="background: #FFF9E5;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 00-3-3.87"></path>
              <path d="M16 3.13a4 4 0 010 7.75"></path>
            </svg>
          </div>
          <h3 class="feature-title">用户管理</h3>
          <p class="feature-desc">查看和管理系统用户信息</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/admin/doctors')">
          <div class="feature-icon" style="background: #FFF8E1;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
            </svg>
          </div>
          <h3 class="feature-title">医生审批</h3>
          <p class="feature-desc">审批和管理医生注册申请</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/admin/leaves')">
          <div class="feature-icon" style="background: #FCE4EC;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <h3 class="feature-title">请假审批</h3>
          <p class="feature-desc">审批和管理医生请假申请</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/admin/appointments')">
          <div class="feature-icon" style="background: #E0F2F1;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <h3 class="feature-title">预约管理</h3>
          <p class="feature-desc">查看和管理所有预约记录</p>
          <div class="feature-link">查看详情 →</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { listUsers, getPendingLeaves, getDashboardStatistics } from '@/api/admin';
import { listAllDoctors } from '@/api/doctor';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();

const adminName = ref('管理员');

const stats = ref({
  pendingApprovals: 0,
  pendingDoctors: 0,
  pendingLeaves: 0
});

const statistics = ref({
  totalPatients: 0,
  totalDoctors: 0,
  totalAppointments: 0,
  todayAppointments: 0,
  todayCheckedIn: 0,
  todayCompleted: 0,
  todayNoShow: 0,
  checkInRate: 0,
  noShowRate: 0,
  departmentRanking: [],
  doctorWorkload: []
});

onMounted(async () => {
  if (userStore.userInfo) {
    adminName.value = userStore.userInfo.realName || '管理员';
  }
  
  await Promise.all([loadStats(), loadStatistics()]);
});

const loadStats = async () => {
  try {
    // 获取医生数
    const doctorsRes = await listAllDoctors({ page: 0, size: 1000 });
    const doctors = doctorsRes.content || [];
    stats.value.pendingDoctors = doctors.filter(d => d.status === 'PENDING').length || 0;

    // 获取待审批请假数
    const leavesRes = await getPendingLeaves({ page: 0, size: 1 });
    stats.value.pendingLeaves = leavesRes.totalElements || 0;

    // 计算总待审批数
    stats.value.pendingApprovals = stats.value.pendingDoctors + stats.value.pendingLeaves;
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

const loadStatistics = async () => {
  try {
    const data = await getDashboardStatistics();
    statistics.value = data || statistics.value;
  } catch (error) {
    console.error('加载统计数据失败:', error);
    ElMessage.error('加载统计数据失败');
  }
};
</script>

<style scoped>
.admin-home {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== Hero区域 ========== */
.hero-section {
  background: linear-gradient(135deg, #FFF9E5 0%, #FFFBF0 100%);
  border-radius: 16px;
  padding: 48px 40px;
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
  gap: 40px;
}

.hero-text {
  flex: 1;
  max-width: 50%;
  z-index: 2;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 12px 0;
  letter-spacing: -1px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.hero-image {
  width: 50%;
  min-width: 500px;
  height: 300px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  position: relative;
  background: linear-gradient(135deg, #FFF9E5 0%, #FFFBF0 100%);
}

.hero-image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 249, 229, 0.85) 0%, rgba(255, 255, 255, 0.5) 100%);
  z-index: 2;
  pointer-events: none;
  border-radius: 16px;
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.5;
  filter: sepia(30%) brightness(1.15) contrast(0.9) saturate(0.8);
  mix-blend-mode: multiply;
}

/* ========== 统计卡片 ========== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2A2A2A;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* ========== 内容区块 ========== */
.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0;
}

/* ========== 待处理事项 ========== */
.tasks-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.task-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.task-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.task-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #FFF9E5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFD300;
}

.task-count {
  font-size: 24px;
  font-weight: 700;
  color: #2A2A2A;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin-bottom: 8px;
}

.task-action {
  font-size: 14px;
  color: #666;
  transition: color 0.2s;
}

.task-card:hover .task-action {
  color: #FFD300;
}

/* ========== 功能卡片区域 ========== */
.features-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 24px 0;
  letter-spacing: -0.5px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 12px;
  padding: 32px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FFD300, #FFF9E5);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  border-color: #FFD300;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  color: #2A2A2A;
}

.feature-title {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 12px 0;
}

.feature-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.feature-link {
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  transition: color 0.2s;
}

.feature-card:hover .feature-link {
  color: #FFD300;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .hero-content {
    flex-direction: column;
    gap: 24px;
  }
  
  .hero-image {
    width: 100%;
    min-width: 100%;
    height: 250px;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 32px 24px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .tasks-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}

/* ========== 今日统计 ========== */
.today-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.today-stat-card {
  text-align: center;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 8px;
}

.today-stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.today-stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2A2A2A;
}

.today-stat-value.completed {
  color: #10b981;
}

.today-stat-value.no-show {
  color: #ef4444;
}

/* ========== 排行表格 ========== */
.ranking-table {
  margin-top: 16px;
}
</style>
