<template>
  <div class="schedule-page">
    <!-- 页面标题和导航 -->
    <div class="page-header">
      <div>
        <h1 class="page-title">排班日历</h1>
        <p class="page-subtitle">查看和管理您的门诊排班</p>
      </div>
      <button class="btn-add-schedule" @click="$router.push('/doctor/schedule/add')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        新增排班
      </button>
    </div>

    <!-- 月份导航 -->
    <div class="calendar-controls">
      <div class="month-nav">
        <button class="nav-btn" @click="changeMonth(-1)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </button>
        <h2 class="current-month">{{ currentYear }}年{{ currentMonth }}月</h2>
        <button class="nav-btn" @click="changeMonth(1)">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>
      <button class="today-btn" @click="goToToday">今天</button>
    </div>

    <!-- Zocdoc 风格日历 -->
    <div class="calendar-wrapper">
      <!-- 周几标题 -->
      <div class="weekday-header">
        <div class="weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
      </div>

      <!-- 日期网格 -->
      <div class="calendar-grid">
        <div 
          v-for="(day, index) in calendarDays" 
          :key="index" 
          class="calendar-cell"
          :class="{
            'other-month': day.isOtherMonth,
            'today': day.isToday,
            'selected': selectedDay && selectedDay.dateStr === day.dateStr,
            'has-schedule': day.isWorking
          }"
          @click="selectDay(day)"
        >
          <div class="cell-date">{{ day.date }}</div>
          <div class="cell-content" v-if="!day.isOtherMonth && day.isWorking">
            <div class="slots-summary">
              <div class="slot-item" v-if="day.hasMorning">
                <span class="slot-count">{{ day.morningSlots || 0 }}</span>
                <span class="slot-label">上午</span>
              </div>
              <div class="slot-item" v-if="day.hasAfternoon">
                <span class="slot-count">{{ day.afternoonSlots || 0 }}</span>
                <span class="slot-label">下午</span>
              </div>
            </div>
            <div class="appointments-badge" v-if="day.appointmentCount > 0">
              {{ day.appointmentCount }} 预约
            </div>
          </div>
          <div class="no-schedule" v-if="!day.isOtherMonth && !day.isWorking">
            <span>无排班</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 选中日期详情卡片 -->
    <div class="detail-panel" v-if="selectedDay && !selectedDay.isOtherMonth">
      <div class="detail-card">
        <div class="detail-header">
          <div class="detail-date">
            <h3>{{ formatSelectedDate }}</h3>
            <span class="status-badge" :class="{ active: selectedDay.isWorking }">
              {{ selectedDay.isWorking ? '有排班' : '无排班' }}
            </span>
          </div>
          <button class="btn-close" @click="selectedDay = null">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>

        <div class="detail-body" v-if="selectedDay.isWorking">
          <!-- 时间段列表 -->
          <div class="schedule-slots">
            <div class="slot-card" v-if="selectedDay.hasMorning">
              <div class="slot-header">
                <div class="slot-badge morning">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                    <circle cx="12" cy="12" r="5"></circle>
                    <line x1="12" y1="1" x2="12" y2="3"></line>
                  </svg>
                  上午
                </div>
                <span class="slot-time">{{ selectedDay.morningTime || '09:00 - 12:00' }}</span>
              </div>
              <div class="slot-stats">
                <div class="stat">
                  <span class="stat-value">{{ selectedDay.morningSlots || 0 }}</span>
                  <span class="stat-label">号源</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ getMorningAppointments(selectedDay) }}</span>
                  <span class="stat-label">已预约</span>
                </div>
              </div>
            </div>

            <div class="slot-card" v-if="selectedDay.hasAfternoon">
              <div class="slot-header">
                <div class="slot-badge afternoon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                    <circle cx="12" cy="12" r="5"></circle>
                    <path d="M12 1v6"></path>
                  </svg>
                  下午
                </div>
                <span class="slot-time">{{ selectedDay.afternoonTime || '14:00 - 17:00' }}</span>
              </div>
              <div class="slot-stats">
                <div class="stat">
                  <span class="stat-value">{{ selectedDay.afternoonSlots || 0 }}</span>
                  <span class="stat-label">号源</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ getAfternoonAppointments(selectedDay) }}</span>
                  <span class="stat-label">已预约</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="detail-actions">
            <button class="btn-action primary" @click="viewAppointments(selectedDay)">
              查看预约列表
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <line x1="5" y1="12" x2="19" y2="12"></line>
                <polyline points="12 5 19 12 12 19"></polyline>
              </svg>
            </button>
          </div>
        </div>

        <div class="no-schedule-info" v-else>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="40" height="40">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <p>当天无排班</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();

const currentDate = ref(new Date());
const schedules = ref([]);
const selectedDay = ref(null);

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth() + 1);

const formatSelectedDate = computed(() => {
  if (!selectedDay.value) return '';
  const d = selectedDay.value.fullDate;
  return `${d.getMonth() + 1}月${d.getDate()}日 ${weekdays[d.getDay()]}`;
});

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear();
  const month = currentDate.value.getMonth();
  
  // 本月第一天
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  
  // 日历开始日期（上月末尾）
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - firstDay.getDay());
  
  const days = [];
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  for (let i = 0; i < 42; i++) {
    const d = new Date(startDate);
    d.setDate(startDate.getDate() + i);
    
    const dateStr = formatDateStr(d);
    const schedule = schedules.value.find(s => s.scheduleDate === dateStr);
    
    days.push({
      date: d.getDate(),
      fullDate: d,
      dateStr: dateStr,
      isOtherMonth: d.getMonth() !== month,
      isToday: d.getTime() === today.getTime(),
      isWorking: schedule?.isWorking || false,
      hasMorning: schedule?.hasMorning || false,
      hasAfternoon: schedule?.hasAfternoon || false,
      morningSlots: schedule?.morningSlots || 0,
      afternoonSlots: schedule?.afternoonSlots || 0,
      morningTime: schedule?.morningTime,
      afternoonTime: schedule?.afternoonTime,
      appointmentCount: schedule?.appointmentCount || 0
    });
  }
  
  return days;
});

onMounted(() => {
  fetchSchedules();
  // 默认选中今天
  const today = calendarDays.value.find(d => d.isToday);
  if (today) selectDay(today);
});

watch(currentDate, () => {
  fetchSchedules();
});

const fetchSchedules = async () => {
  try {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth() + 1;
    const data = await request.get('/api/doctor/schedules', {
      params: { year, month }
    });
    schedules.value = data || [];
  } catch (e) {
    console.error('获取排班失败:', e);
    schedules.value = [];
  }
};

const changeMonth = (delta) => {
  const newDate = new Date(currentDate.value);
  newDate.setMonth(newDate.getMonth() + delta);
  currentDate.value = newDate;
};

const goToToday = () => {
  currentDate.value = new Date();
  const today = calendarDays.value.find(d => d.isToday);
  if (today) selectDay(today);
};

const selectDay = (day) => {
  if (!day.isOtherMonth) {
    selectedDay.value = day;
  }
};

const formatDateStr = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const viewAppointments = (day) => {
  router.push({
    path: '/doctor/appointments',
    query: { date: day.dateStr }
  });
};

const getMorningAppointments = (day) => {
  // 这里可以根据实际数据计算上午的预约数
  return Math.floor((day.morningSlots || 0) * 0.6);
};

const getAfternoonAppointments = (day) => {
  // 这里可以根据实际数据计算下午的预约数
  return Math.floor((day.afternoonSlots || 0) * 0.6);
};
</script>

<style scoped>
/* ========== Zocdoc 风格排班日历 ========== */
.schedule-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.btn-add-schedule {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #FFD300;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-schedule:hover {
  background: #F4CA00;
  transform: translateY(-1px);
}

/* 月份控制 */
.calendar-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.month-nav {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #E8E8E8;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.nav-btn:hover {
  border-color: #FFD300;
  background: #FFF9E5;
}

.current-month {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0;
  min-width: 140px;
  text-align: center;
}

.today-btn {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  color: #2A2A2A;
  transition: all 0.2s;
}

.today-btn:hover {
  border-color: #FFD300;
  background: #FFF9E5;
}

/* 日历容器 */
.calendar-wrapper {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}

.weekday {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
}

/* Zocdoc 风格日历网格 - 更紧凑 */
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-cell {
  min-height: 100px;
  padding: 8px;
  border: 1px solid #F0F0F0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.calendar-cell:hover:not(.other-month) {
  border-color: #FFD300;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.calendar-cell.other-month {
  opacity: 0.3;
  cursor: default;
  background: #FAFAFA;
}

.calendar-cell.today {
  background: #FFF9E5;
  border-color: #FFD300;
  border-width: 2px;
}

.calendar-cell.selected {
  background: #FFF9E5;
  border-color: #FFD300;
  border-width: 2px;
  box-shadow: 0 2px 12px rgba(255,211,0,0.3);
}

.cell-date {
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  margin-bottom: 6px;
}

.calendar-cell.today .cell-date {
  color: #F4CA00;
}

/* Zocdoc 风格的时间段显示 */
.cell-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.slots-summary {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.slot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 6px;
  background: #F8F8F8;
  border-radius: 4px;
  font-size: 11px;
}

.slot-count {
  font-weight: 700;
  color: #2A2A2A;
  margin-right: 4px;
}

.slot-label {
  color: #666;
  font-size: 10px;
}

.appointments-badge {
  margin-top: auto;
  padding: 3px 6px;
  background: #E8F5E9;
  color: #2E7D32;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;
  text-align: center;
}

.no-schedule {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-schedule span {
  font-size: 11px;
  color: #999;
}

/* 详情面板 */
.detail-panel {
  position: fixed;
  right: 24px;
  top: 120px;
  width: 320px;
  z-index: 50;
}

.detail-card {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  overflow: hidden;
}

.detail-header {
  padding: 20px;
  border-bottom: 1px solid #F0F0F0;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.detail-date h3 {
  font-size: 18px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 8px 0;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  background: #F5F5F5;
  color: #666;
}

.status-badge.active {
  background: #E8F5E9;
  color: #2E7D32;
}

.btn-close {
  width: 28px;
  height: 28px;
  border: none;
  background: #F5F5F5;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #E8E8E8;
}

.detail-body {
  padding: 20px;
}

.schedule-slots {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.slot-card {
  border: 1px solid #F0F0F0;
  border-radius: 6px;
  padding: 12px;
  background: #FAFAFA;
}

.slot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.slot-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.slot-badge.morning {
  background: #E3F2FD;
  color: #1976D2;
}

.slot-badge.afternoon {
  background: #FCE4EC;
  color: #C2185B;
}

.slot-time {
  font-size: 12px;
  color: #666;
}

.slot-stats {
  display: flex;
  gap: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
}

.stat-label {
  font-size: 11px;
  color: #999;
}

.detail-actions {
  display: flex;
  gap: 8px;
}

.btn-action {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  border: 1px solid #E8E8E8;
  background: #fff;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #2A2A2A;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-action.primary {
  background: #FFD300;
  border-color: #FFD300;
}

.btn-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.no-schedule-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  color: #999;
}

.no-schedule-info p {
  margin: 12px 0 0;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .detail-panel {
    position: static;
    width: 100%;
    margin-top: 24px;
  }
}

@media (max-width: 768px) {
  .calendar-cell {
    min-height: 80px;
    padding: 6px;
  }
  
  .cell-date {
    font-size: 12px;
  }
  
  .slot-item {
    font-size: 10px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
}
</style>

