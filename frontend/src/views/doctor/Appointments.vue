<template>
  <div class="appointments-page">
    <!-- 日期选择器 -->
    <div class="date-selector">
      <button class="date-nav" @click="changeDate(-1)">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <div class="date-display">
        <span class="date-main">{{ formatDateDisplay(selectedDate) }}</span>
        <span class="date-sub">{{ getWeekday(selectedDate) }}</span>
      </div>
      <button class="date-nav" @click="changeDate(1)">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
          <polyline points="9 18 15 12 9 6"></polyline>
        </svg>
      </button>
      <button class="today-btn" @click="goToToday" v-if="!isToday">今天</button>
      <input type="date" v-model="datePickerValue" @change="onDatePick" class="date-picker" />
    </div>

    <!-- 统计栏 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-num">{{ appointments.length }}</span>
        <span class="stat-label">总预约</span>
      </div>
      <div class="stat-item pending">
        <span class="stat-num">{{ pendingCount }}</span>
        <span class="stat-label">待就诊</span>
      </div>
      <div class="stat-item completed">
        <span class="stat-num">{{ completedCount }}</span>
        <span class="stat-label">已完成</span>
      </div>
      <div class="stat-item no-show">
        <span class="stat-num">{{ noShowCount }}</span>
        <span class="stat-label">爽约</span>
      </div>
    </div>

    <!-- 预约列表 -->
    <div class="appointments-container">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="appointments.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="64" height="64">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
          <line x1="9" y1="16" x2="15" y2="16"></line>
        </svg>
        <h3>当日暂无预约</h3>
        <p>选择其他日期查看预约</p>
      </div>

      <div v-else class="appointments-list">
        <!-- 上午时段 -->
        <div class="time-section" v-if="morningAppointments.length > 0">
          <div class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <circle cx="12" cy="12" r="5"></circle>
              <line x1="12" y1="1" x2="12" y2="3"></line>
              <line x1="12" y1="21" x2="12" y2="23"></line>
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
              <line x1="1" y1="12" x2="3" y2="12"></line>
              <line x1="21" y1="12" x2="23" y2="12"></line>
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
            </svg>
            <span>上午 ({{ morningAppointments.length }})</span>
          </div>
          <div class="appointment-cards">
            <div 
              v-for="apt in morningAppointments" 
              :key="apt.id" 
              class="appointment-card"
              :class="apt.status?.toLowerCase()"
            >
              <div class="card-header">
                <div class="patient-avatar">
                  {{ apt.patientName?.charAt(0) || '患' }}
                </div>
                <div class="patient-info">
                  <span class="patient-name">{{ apt.patientName }}</span>
                  <span class="patient-phone">{{ maskPhone(apt.patientPhone) }}</span>
                </div>
                <div class="status-badge" :class="apt.status?.toLowerCase()">
                  {{ getStatusText(apt.status) }}
                </div>
              </div>
              <div class="card-body">
                <p class="symptom" v-if="apt.symptomDesc"><strong>主诉：</strong>{{ apt.symptomDesc }}</p>
                <p class="medical-item" v-if="apt.medicalItemName">
                  <strong>检查项目：</strong>{{ apt.medicalItemName }}
                  <span v-if="apt.medicalItemPrice" class="item-price">(¥{{ apt.medicalItemPrice }})</span>
                </p>
              </div>
              <div class="card-footer">
                <span class="appointment-no">{{ apt.appointmentNo }}</span>
                <div class="actions" v-if="apt.status === 'PENDING'">
                  <button class="btn check-in" @click="handleCheckIn(apt)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                      <path d="M22 11.08V12a10 10 0 11-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    签到
                  </button>
                  <button class="btn complete" @click="handleComplete(apt)">完成</button>
                  <button class="btn no-show" @click="handleNoShow(apt)">爽约</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 下午时段 -->
        <div class="time-section" v-if="afternoonAppointments.length > 0">
          <div class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <circle cx="12" cy="12" r="5"></circle>
              <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"></path>
            </svg>
            <span>下午 ({{ afternoonAppointments.length }})</span>
          </div>
          <div class="appointment-cards">
            <div 
              v-for="apt in afternoonAppointments" 
              :key="apt.id" 
              class="appointment-card"
              :class="apt.status?.toLowerCase()"
            >
              <div class="card-header">
                <div class="patient-avatar">
                  {{ apt.patientName?.charAt(0) || '患' }}
                </div>
                <div class="patient-info">
                  <span class="patient-name">{{ apt.patientName }}</span>
                  <span class="patient-phone">{{ maskPhone(apt.patientPhone) }}</span>
                </div>
                <div class="status-badge" :class="apt.status?.toLowerCase()">
                  {{ getStatusText(apt.status) }}
                </div>
              </div>
              <div class="card-body">
                <p class="symptom" v-if="apt.symptomDesc"><strong>主诉：</strong>{{ apt.symptomDesc }}</p>
                <p class="medical-item" v-if="apt.medicalItemName">
                  <strong>检查项目：</strong>{{ apt.medicalItemName }}
                  <span v-if="apt.medicalItemPrice" class="item-price">(¥{{ apt.medicalItemPrice }})</span>
                </p>
              </div>
              <div class="card-footer">
                <span class="appointment-no">{{ apt.appointmentNo }}</span>
                <div class="actions" v-if="apt.status === 'PENDING'">
                  <button class="btn check-in" @click="handleCheckIn(apt)">签到</button>
                  <button class="btn complete" @click="handleComplete(apt)">完成</button>
                  <button class="btn no-show" @click="handleNoShow(apt)">爽约</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const loading = ref(true);
const appointments = ref([]);
const selectedDate = ref(new Date());

const datePickerValue = computed({
  get: () => formatDateForInput(selectedDate.value),
  set: (val) => {
    selectedDate.value = new Date(val);
  }
});

const isToday = computed(() => {
  const today = new Date();
  return selectedDate.value.toDateString() === today.toDateString();
});

const morningAppointments = computed(() => 
  appointments.value.filter(a => a.period === 'MORNING')
);

const afternoonAppointments = computed(() => 
  appointments.value.filter(a => a.period === 'AFTERNOON' || a.period === 'EVENING')
);

const pendingCount = computed(() => 
  appointments.value.filter(a => a.status === 'PENDING').length
);

const completedCount = computed(() => 
  appointments.value.filter(a => a.status === 'COMPLETED').length
);

const noShowCount = computed(() => 
  appointments.value.filter(a => a.status === 'NO_SHOW').length
);

onMounted(() => {
  fetchAppointments();
});

watch(selectedDate, () => {
  fetchAppointments();
});

const fetchAppointments = async () => {
  loading.value = true;
  try {
    const dateStr = formatDateForInput(selectedDate.value);
    const data = await request.get('/api/doctor/work/appointments', {
      params: { date: dateStr }
    });
    // request.js 的响应拦截器已经提取了 data 字段
    appointments.value = Array.isArray(data) ? data : [];
  } catch (e) {
    console.error('获取预约失败:', e);
    appointments.value = [];
  } finally {
    loading.value = false;
  }
};

const changeDate = (delta) => {
  const newDate = new Date(selectedDate.value);
  newDate.setDate(newDate.getDate() + delta);
  selectedDate.value = newDate;
};

const goToToday = () => {
  selectedDate.value = new Date();
};

const onDatePick = (e) => {
  selectedDate.value = new Date(e.target.value);
};

const formatDateForInput = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const formatDateDisplay = (date) => {
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const getWeekday = (date) => {
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return weekdays[date.getDay()];
};

const getStatusText = (status) => {
  const map = {
    'PENDING': '待就诊',
    'CHECKED_IN': '已签到',
    'COMPLETED': '已完成',
    'NO_SHOW': '爽约',
    'CANCELLED': '已取消'
  };
  return map[status] || status;
};

const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

const handleCheckIn = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/check-in`);
    apt.status = 'CHECKED_IN';
    ElMessage.success('签到成功');
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '签到失败');
  }
};

const handleComplete = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/complete`);
    apt.status = 'COMPLETED';
    ElMessage.success('已完成就诊');
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败');
  }
};

const handleNoShow = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/no-show`);
    apt.status = 'NO_SHOW';
    ElMessage.warning('已标记为爽约');
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败');
  }
};
</script>

<style scoped>
.appointments-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* 日期选择器 */
.date-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.date-nav {
  width: 40px;
  height: 40px;
  border: none;
  background: #f1f5f9;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #475569;
  transition: all 0.2s;
}

.date-nav:hover {
  background: #e2e8f0;
}

.date-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}

.date-main {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.date-sub {
  font-size: 14px;
  color: #64748b;
}

.today-btn {
  padding: 8px 20px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  color: #1a1a2e;
}

.today-btn:hover {
  background: #f4ca00;
}

.date-picker {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

/* 统计栏 */
.stats-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-item {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #1a1a2e;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
}

.stat-item.pending .stat-num { color: #f59e0b; }
.stat-item.completed .stat-num { color: #10b981; }
.stat-item.no-show .stat-num { color: #ef4444; }

/* 加载和空状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 16px;
  color: #94a3b8;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-state h3 {
  margin: 16px 0 8px;
  color: #475569;
}

.empty-state p {
  margin: 0;
}

/* 时段分组 */
.time-section {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #FFD300;
}

.appointment-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
}

.appointment-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border-left: 4px solid #FFD300;
  transition: all 0.2s;
}

.appointment-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.appointment-card.completed {
  border-left-color: #10b981;
  opacity: 0.8;
}

.appointment-card.no_show {
  border-left-color: #ef4444;
  opacity: 0.7;
}

.appointment-card.cancelled {
  border-left-color: #94a3b8;
  opacity: 0.6;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.patient-avatar {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
}

.patient-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.patient-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.patient-phone {
  font-size: 13px;
  color: #64748b;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.checked_in {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-badge.completed {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.no_show {
  background: #fee2e2;
  color: #991b1b;
}

.status-badge.cancelled {
  background: #f3f4f6;
  color: #6b7280;
}

.card-body {
  margin-bottom: 12px;
}

.symptom {
  font-size: 14px;
  color: #475569;
  margin: 0;
  line-height: 1.5;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.appointment-no {
  font-size: 12px;
  color: #94a3b8;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn.check-in {
  background: #dbeafe;
  color: #1d4ed8;
}

.btn.check-in:hover {
  background: #bfdbfe;
}

.btn.complete {
  background: #d1fae5;
  color: #065f46;
}

.btn.complete:hover {
  background: #a7f3d0;
}

.btn.no-show {
  background: #f3f4f6;
  color: #6b7280;
}

.btn.no-show:hover {
  background: #e5e7eb;
}

@media (max-width: 768px) {
  .stats-bar {
    flex-wrap: wrap;
  }
  
  .stat-item {
    flex: 1 1 45%;
  }
  
  .appointment-cards {
    grid-template-columns: 1fr;
  }
}
</style>

