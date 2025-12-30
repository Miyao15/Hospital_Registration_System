<template>
  <div class="admin-home">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">工作台</h1>
      <p class="page-subtitle">欢迎回来，{{ adminName }}</p>
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
          <div class="stat-value">{{ statistics.totalPatients || 0 }}</div>
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
          <div class="stat-value">{{ statistics.totalDoctors || 0 }}</div>
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

    <!-- 快捷操作 -->
    <div class="content-section">
      <div class="section-header">
        <h2 class="section-title">快捷操作</h2>
      </div>

      <div class="actions-grid">
        <div class="action-card" @click="$router.push('/admin/departments')">
          <div class="action-icon" style="background: #E3F2FD;">
            <svg viewBox="0 0 24 24" fill="none" stroke="#1976D2" stroke-width="2" width="24" height="24">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
          </div>
          <div class="action-title">科室管理</div>
          <div class="action-desc">管理医院科室信息</div>
        </div>

        <div class="action-card" @click="$router.push('/admin/schedules')">
          <div class="action-icon" style="background: #E8F5E9;">
            <svg viewBox="0 0 24 24" fill="none" stroke="#388E3C" stroke-width="2" width="24" height="24">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <div class="action-title">排班管理</div>
          <div class="action-desc">设置医生排班计划</div>
        </div>

        <div class="action-card" @click="$router.push('/admin/users')">
          <div class="action-icon" style="background: #FFF3E0;">
            <svg viewBox="0 0 24 24" fill="none" stroke="#F57C00" stroke-width="2" width="24" height="24">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 00-3-3.87"></path>
              <path d="M16 3.13a4 4 0 010 7.75"></path>
            </svg>
          </div>
          <div class="action-title">用户管理</div>
          <div class="action-desc">查看和管理用户</div>
        </div>

        <div class="action-card" @click="$router.push('/admin/doctors')">
          <div class="action-icon" style="background: #FCE4EC;">
            <svg viewBox="0 0 24 24" fill="none" stroke="#C2185B" stroke-width="2" width="24" height="24">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
            </svg>
          </div>
          <div class="action-title">医生审批</div>
          <div class="action-desc">审批医生注册申请</div>
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

/* ========== 快捷操作 ========== */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.action-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.action-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.action-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin-bottom: 8px;
}

.action-desc {
  font-size: 14px;
  color: #666;
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
  
  .tasks-grid,
  .actions-grid {
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
