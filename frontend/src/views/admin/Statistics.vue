<template>
  <div class="admin-statistics">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">数据统计</h1>
      <p class="page-subtitle">查看系统运行数据和预约就诊情况</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: #E3F2FD;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#1976D2" stroke-width="2" width="24" height="24">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M23 21v-2a4 4 0 00-3-3.87"></path>
            <path d="M16 3.13a4 4 0 010 7.75"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalPatients || 0 }}</div>
          <div class="stat-label">总患者数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: #E8F5E9;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#388E3C" stroke-width="2" width="24" height="24">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalDoctors || 0 }}</div>
          <div class="stat-label">总医生数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: #FFF3E0;">
          <svg viewBox="0 0 24 24" fill="none" stroke="#F57C00" stroke-width="2" width="24" height="24">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalAppointments || 0 }}</div>
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
          <div class="stat-value">{{ stats.todayAppointments || 0 }}</div>
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
          <div class="today-stat-value">{{ stats.todayCheckedIn || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">已完成</div>
          <div class="today-stat-value completed">{{ stats.todayCompleted || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">爽约</div>
          <div class="today-stat-value no-show">{{ stats.todayNoShow || 0 }}</div>
        </div>
        <div class="today-stat-card">
          <div class="today-stat-label">就诊率</div>
          <div class="today-stat-value">{{ (stats.checkInRate || 0).toFixed(1) }}%</div>
        </div>
      </div>
    </div>

    <!-- 科室预约排行 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">科室预约排行（近30天）</h2>
      </div>
      <div class="ranking-table">
        <el-table :data="stats.departmentRanking || []" stripe style="width: 100%">
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
        <el-table :data="stats.doctorWorkload || []" stripe style="width: 100%">
          <el-table-column type="index" label="排名" width="80" align="center" />
          <el-table-column prop="doctorName" label="医生姓名" />
          <el-table-column prop="title" label="职称" />
          <el-table-column prop="count" label="就诊数量" align="center" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getDashboardStatistics } from '@/api/admin';
import { ElMessage } from 'element-plus';

const stats = ref({
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

onMounted(() => {
  loadStatistics();
});

const loadStatistics = async () => {
  try {
    const data = await getDashboardStatistics();
    stats.value = data || stats.value;
  } catch (error) {
    console.error('加载统计数据失败:', error);
    ElMessage.error('加载统计数据失败');
  }
};
</script>

<style scoped>
.admin-statistics {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 页面标题 ========== */
.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
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

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
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
  
  .today-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

