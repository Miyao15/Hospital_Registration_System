<template>
  <div class="bento-home">
    <!-- Bento Grid Layout -->
    <div class="bento-grid">
      
      <!-- Hero Welcome Card (Large) -->
      <div class="bento-card hero-card">
        <div class="card-glow"></div>
        <div class="hero-content">
          <div class="greeting">
            <div class="greeting-time">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                <circle cx="12" cy="12" r="5"></circle>
                <line x1="12" y1="1" x2="12" y2="3"></line>
                <line x1="12" y1="21" x2="12" y2="23"></line>
              </svg>
              <span>{{ greetingText }}</span>
            </div>
            <h1 class="doctor-name">{{ doctorName }} 医生</h1>
            <p class="date-display">{{ todayDateFull }}</p>
          </div>
          <div class="quick-summary">
            <div class="summary-item">
              <span class="summary-label">今日预约</span>
              <span class="summary-value">{{ stats.todayCount }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">待就诊</span>
              <span class="summary-value pending">{{ stats.pendingCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Next Patient Card with Countdown -->
      <div class="bento-card next-patient-card" v-if="nextAppointment">
        <div class="card-header">
          <h3>下一位患者</h3>
          <div class="countdown-ring">
            <svg class="progress-ring" width="60" height="60">
              <circle
                class="progress-ring-circle-bg"
                stroke="#f1f5f9"
                stroke-width="4"
                fill="transparent"
                r="26"
                cx="30"
                cy="30"
              />
              <circle
                class="progress-ring-circle"
                stroke="url(#gradient)"
                stroke-width="4"
                fill="transparent"
                r="26"
                cx="30"
                cy="30"
                :stroke-dasharray="`${circumference} ${circumference}`"
                :stroke-dashoffset="progressOffset"
              />
              <defs>
                <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#FFD300" />
                  <stop offset="100%" stop-color="#FFC300" />
                </linearGradient>
              </defs>
            </svg>
            <span class="time-left">{{ minutesLeft }}m</span>
          </div>
        </div>
        <div class="patient-info-card">
          <div class="patient-avatar-large">{{ nextAppointment.patientName?.charAt(0) || '患' }}</div>
          <div class="patient-details-large">
            <h4>{{ nextAppointment.patientName }}</h4>
            <p>{{ nextAppointment.periodName || '上午' }} · {{ nextAppointment.symptomDesc || '常规检查' }}</p>
          </div>
        </div>
        <button class="btn-check-in-large" @click="handleQuickCheckIn(nextAppointment)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
          快速签到
        </button>
      </div>

      <!-- Stats Cards with Sparklines -->
      <div class="bento-card stat-card patients-stat">
        <div class="stat-bg-pattern">
          <svg class="sparkline" width="100%" height="60" preserveAspectRatio="none">
            <polyline
              fill="none"
              stroke="rgba(255,211,0,0.3)"
              stroke-width="2"
              points="0,40 20,35 40,45 60,30 80,38 100,25 120,30"
            />
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-icon patients">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"></path>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.todayCount }}</span>
            <span class="stat-label">今日患者</span>
          </div>
        </div>
      </div>

      <div class="bento-card stat-card reviews-stat">
        <div class="stat-bg-pattern">
          <svg class="sparkline" width="100%" height="60" preserveAspectRatio="none">
            <polyline
              fill="none"
              stroke="rgba(245,158,11,0.3)"
              stroke-width="2"
              points="0,50 20,45 40,48 60,42 80,44 100,38 120,40"
            />
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-icon reviews">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.rating || '5.0' }}</span>
            <span class="stat-label">患者评分</span>
          </div>
        </div>
      </div>

      <div class="bento-card stat-card completed-stat">
        <div class="stat-bg-pattern">
          <svg class="sparkline" width="100%" height="60" preserveAspectRatio="none">
            <polyline
              fill="none"
              stroke="rgba(16,185,129,0.3)"
              stroke-width="2"
              points="0,45 20,40 40,35 60,38 80,32 100,28 120,30"
            />
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-icon completed">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M22 11.08V12a10 10 0 11-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.completedCount }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </div>

      <!-- Schedule Preview - Ticket Style -->
      <div class="bento-card schedule-card">
        <div class="card-header">
          <h3>今日排班</h3>
          <router-link to="/doctor/appointments" class="view-link">查看全部</router-link>
        </div>
        <div class="schedule-tickets" v-if="todayAppointments.length > 0">
          <div 
            v-for="apt in todayAppointments.slice(0, 4)" 
            :key="apt.id"
            class="ticket-item"
            :class="apt.status?.toLowerCase()"
          >
            <div class="ticket-perforation-left"></div>
            <div class="ticket-content">
              <div class="ticket-time">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                <span>{{ apt.periodName }}</span>
              </div>
              <div class="ticket-patient">
                <span class="patient-name-ticket">{{ apt.patientName }}</span>
                <span class="status-badge-mini" :class="apt.status?.toLowerCase()">
                  {{ getStatusText(apt.status) }}
                </span>
              </div>
            </div>
            <div class="ticket-perforation-right"></div>
          </div>
        </div>
        <div v-else class="empty-schedule">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="48" height="48">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <p>今日暂无排班</p>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="bento-card actions-card">
        <h3>快捷操作</h3>
        <div class="action-grid">
          <button class="action-btn" @click="$router.push('/doctor/appointments')">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              </svg>
            </div>
            <span>预约管理</span>
          </button>
          <button class="action-btn" @click="$router.push('/doctor/schedule')">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22">
                <circle cx="12" cy="12" r="10"></circle>
              </svg>
            </div>
            <span>查看排班</span>
          </button>
          <button class="action-btn" @click="$router.push('/doctor/leaves')">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22">
                <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"></path>
              </svg>
            </div>
            <span>申请请假</span>
          </button>
          <button class="action-btn" @click="$router.push('/doctor/reviews')">
            <div class="action-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
            <span>我的评价</span>
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const userStore = useUserStore();

const loading = ref(true);
const todayAppointments = ref([]);
const stats = ref({
  todayCount: 0,
  pendingCount: 0,
  completedCount: 0,
  rating: '5.0',
});

const doctorName = computed(() => userStore.userInfo?.realName || '医生');

const greetingText = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 18) return '下午好';
  if (hour < 22) return '晚上好';
  return '夜深了';
});

const todayDateFull = computed(() => {
  const now = new Date();
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekdays[now.getDay()]}`;
});

// Next appointment with countdown
const nextAppointment = computed(() => {
  return todayAppointments.value.find(apt => apt.status === 'PENDING') || null;
});

const circumference = 2 * Math.PI * 26;
const minutesLeft = ref(45);
const progressOffset = computed(() => {
  const progress = minutesLeft.value / 60;
  return circumference - (progress * circumference);
});

onMounted(async () => {
  await fetchTodayAppointments();
});

const fetchTodayAppointments = async () => {
  loading.value = true;
  try {
    const data = await request.get('/api/doctor/work/appointments/today');
    todayAppointments.value = data || [];
    
    stats.value.todayCount = todayAppointments.value.length;
    stats.value.pendingCount = todayAppointments.value.filter(a => a.status === 'PENDING').length;
    stats.value.completedCount = todayAppointments.value.filter(a => a.status === 'COMPLETED').length;
  } catch (e) {
    console.error('获取今日预约失败:', e);
  } finally {
    loading.value = false;
  }
};

const getStatusText = (status) => {
  const map = {
    'PENDING': '待就诊',
    'CHECKED_IN': '已签到',
    'COMPLETED': '已完成',
    'NO_SHOW': '爽约',
  };
  return map[status] || status;
};

const handleQuickCheckIn = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/check-in`);
    ElMessage.success('签到成功');
    apt.status = 'CHECKED_IN';
    stats.value.pendingCount--;
  } catch (e) {
    ElMessage.error('签到失败');
  }
};
</script>

<style scoped>
/* ========== Bento Grid Layout ========== */
.bento-home {
  width: 100%;
}

.bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: minmax(120px, auto);
  gap: 24px;
}

/* ========== Bento Card Base ========== */
.bento-card {
  background: #FFFFFF;
  border-radius: 32px;
  padding: 32px;
  box-shadow: 0 20px 40px -10px rgba(0,0,0,0.08);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.bento-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 30px 60px -15px rgba(0,0,0,0.12);
}

/* ========== Hero Welcome Card ========== */
.hero-card {
  grid-column: span 2;
  grid-row: span 2;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  position: relative;
  overflow: hidden;
}

.card-glow {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,211,0,0.3) 0%, transparent 70%);
  animation: glow 8s ease-in-out infinite;
}

@keyframes glow {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(-30px, 30px) scale(1.1); }
}

.hero-content {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.greeting-time {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #FFD300;
  margin-bottom: 12px;
}

.doctor-name {
  font-size: 48px;
  font-weight: 800;
  margin: 0;
  line-height: 1.2;
  background: linear-gradient(135deg, #FFD300, #FFC300);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.date-display {
  font-size: 18px;
  color: rgba(255,255,255,0.7);
  margin: 8px 0 0;
}

.quick-summary {
  display: flex;
  gap: 32px;
  margin-top: 24px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-label {
  font-size: 14px;
  color: rgba(255,255,255,0.6);
}

.summary-value {
  font-size: 36px;
  font-weight: 800;
  color: #fff;
}

.summary-value.pending {
  color: #FFD300;
}

/* ========== Next Patient Card ========== */
.next-patient-card {
  grid-column: span 2;
  grid-row: span 2;
  background: linear-gradient(135deg, #FFD300 0%, #FFC300 100%);
  color: #1a1a2e;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}

.countdown-ring {
  position: relative;
  width: 60px;
  height: 60px;
}

.progress-ring-circle {
  transform: rotate(-90deg);
  transform-origin: 50% 50%;
  transition: stroke-dashoffset 1s ease;
  stroke-linecap: round;
}

.progress-ring-circle-bg {
  opacity: 0.3;
}

.time-left {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  font-weight: 700;
}

.patient-info-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: rgba(255,255,255,0.5);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  margin-bottom: 20px;
}

.patient-avatar-large {
  width: 64px;
  height: 64px;
  background: #1a1a2e;
  color: #FFD300;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  flex-shrink: 0;
}

.patient-details-large h4 {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px 0;
}

.patient-details-large p {
  font-size: 15px;
  opacity: 0.8;
  margin: 0;
}

.btn-check-in-large {
  width: 100%;
  padding: 18px;
  background: #1a1a2e;
  color: #FFD300;
  border: none;
  border-radius: 20px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s;
}

.btn-check-in-large:hover {
  transform: scale(1.02);
  box-shadow: 0 10px 30px -5px rgba(26,26,46,0.3);
}

/* ========== Stat Cards with Sparklines ========== */
.stat-card {
  grid-column: span 1;
  grid-row: span 1;
  position: relative;
  overflow: hidden;
}

.stat-bg-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.4;
}

.sparkline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
}

.stat-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.stat-icon.patients {
  background: linear-gradient(135deg, #FFD300, #FFC300);
  color: #1a1a2e;
}

.stat-icon.reviews {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: #fff;
}

.stat-icon.completed {
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
}

.stat-value {
  font-size: 42px;
  font-weight: 800;
  color: #1a1a2e;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

/* ========== Schedule Tickets ========== */
.schedule-card {
  grid-column: span 2;
  grid-row: span 2;
}

.view-link {
  font-size: 14px;
  color: #FFD300;
  text-decoration: none;
  font-weight: 600;
}

.schedule-tickets {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ticket-item {
  position: relative;
  background: #f8fafc;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 2px dashed #e2e8f0;
  transition: all 0.3s;
}

.ticket-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.ticket-item.completed {
  opacity: 0.6;
}

.ticket-perforation-left,
.ticket-perforation-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  background: #FFFFFF;
  border-radius: 50%;
}

.ticket-perforation-left {
  left: -6px;
}

.ticket-perforation-right {
  right: -6px;
}

.ticket-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ticket-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.ticket-patient {
  display: flex;
  align-items: center;
  gap: 12px;
}

.patient-name-ticket {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.status-badge-mini {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge-mini.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge-mini.completed {
  background: #d1fae5;
  color: #065f46;
}

.empty-schedule {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  color: #cbd5e1;
}

.empty-schedule p {
  margin: 12px 0 0;
  font-size: 15px;
}

/* ========== Quick Actions ========== */
.actions-card {
  grid-column: span 2;
  grid-row: span 1;
}

.actions-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 20px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.action-btn {
  padding: 20px 16px;
  background: #f8fafc;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.action-btn:hover {
  background: #FFD300;
  transform: translateY(-4px);
  box-shadow: 0 12px 24px -6px rgba(255,211,0,0.4);
}

.action-icon {
  width: 48px;
  height: 48px;
  background: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a1a2e;
}

.action-btn span {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
}

/* ========== Responsive ========== */
@media (max-width: 1200px) {
  .bento-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-card,
  .next-patient-card,
  .schedule-card,
  .actions-card {
    grid-column: span 2;
  }
  
  .stat-card {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .bento-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .bento-card {
    grid-column: span 1 !important;
    grid-row: span 1 !important;
    padding: 24px;
  }
  
  .doctor-name {
    font-size: 36px;
  }
  
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

