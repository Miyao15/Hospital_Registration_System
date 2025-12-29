<template>
  <div class="doctors-page">
    <!-- 顶部导航 -->
    <header class="top-bar">
      <div class="nav-left">
        <div class="logo" @click="router.push('/')">
          <div class="logo-box">优</div>
          <span class="logo-text">优医预约</span>
        </div>
        
        <!-- 搜索栏 -->
        <div class="search-bar-inline">
          <div class="search-input-group">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input type="text" v-model="searchKeyword" placeholder="搜索医生姓名..." @keyup.enter="handleSearch" />
          </div>
          <button class="search-btn" @click="handleSearch">
            <svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </button>
        </div>
      </div>
      <div class="nav-right">
        <button class="btn-back" @click="router.back()">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
          返回
        </button>
      </div>
    </header>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <button class="filter-pill" :class="{ active: !selectedDeptFilter }" @click="selectedDeptFilter = ''">
        全部科室
      </button>
      <button 
        v-for="dept in departments" 
        :key="dept.id" 
        class="filter-pill" 
        :class="{ active: selectedDeptFilter === dept.id }"
        @click="filterByDepartment(dept.id)"
      >
        {{ dept.name }}
      </button>
    </div>

    <!-- 项目信息卡片 -->
    <section class="item-info-section" v-if="medicalItem">
      <div class="container">
        <div class="item-info-card">
          <div class="item-icon-wrapper">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
            </svg>
          </div>
          <div class="item-details">
            <span class="item-badge">检查项目</span>
            <h2 class="item-name">{{ medicalItem.name }}</h2>
            <p class="item-desc">{{ medicalItem.description || '专业检查项目' }}</p>
            <div class="item-meta">
              <span class="item-price">¥{{ medicalItem.price }}</span>
              <span class="item-category">{{ medicalItem.category }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 主体内容 -->
    <main class="main-content">
      <div class="container">
        <!-- 结果头部 -->
        <div class="results-header">
          <h2>{{ doctors.length }} 位医生可预约</h2>
          <div class="date-nav">
            <button class="nav-arrow" @click="prevDatePage" :disabled="dayOffset === 0">‹</button>
            <span class="date-range">{{ dateRangeText }}</span>
            <button class="nav-arrow" @click="nextDatePage">›</button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>正在加载医生信息...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="doctors.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M16 16s-1.5-2-4-2-4 2-4 2"></path>
              <line x1="9" y1="9" x2="9.01" y2="9"></line>
              <line x1="15" y1="9" x2="15.01" y2="9"></line>
            </svg>
          </div>
          <p>暂无可预约的医生</p>
          <button class="btn-outline" @click="router.push('/patient/home')">返回首页</button>
        </div>

        <!-- 医生列表 -->
        <div v-else class="doctors-list">
          <div v-for="doctor in doctors" :key="doctor.id" class="doctor-card">
            <!-- 左侧医生信息 -->
            <div class="card-info">
              <div class="info-top">
                <img 
                  :src="doctor.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
                  :alt="doctor.name" 
                  class="avatar"
                />
              </div>
              <div class="info-details">
                <h3 class="doc-name">{{ doctor.name }}</h3>
                <p class="doc-title">{{ doctor.title }}</p>
                <p class="doc-dept">{{ doctor.departmentName || '暂无科室' }}</p>
                
                <div class="rating-row">
                  <div class="stars">
                    <svg v-for="i in 5" :key="i" class="star-icon" fill="#FFD300" viewBox="0 0 24 24">
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                    </svg>
                  </div>
                  <span class="score">5.0</span>
                  <span class="reviews">(0 评价)</span>
                </div>

                <p class="doc-specialty" v-if="doctor.specialty">
                  擅长：{{ doctor.specialty }}
                </p>
              </div>
            </div>

            <!-- 右侧排班日历 -->
            <div class="card-calendar">
              <div class="calendar-grid">
                <div 
                  v-for="(dayObj, idx) in headerDays.slice(0, 7)" 
                  :key="idx" 
                  class="day-cell"
                >
                  <div class="cell-header">
                    <span class="weekday">{{ dayObj.weekday }}</span>
                    <span class="date">{{ dayObj.shortDate }}</span>
                  </div>
                  <div class="cell-content">
                    <button 
                      v-if="hasSlots(doctor, dayObj.fullDate)"
                      class="slot-btn"
                      @click="selectDoctorAndDate(doctor, dayObj)"
                    >
                      <span class="slot-count">{{ getSlotCount(doctor, dayObj.fullDate) }}</span>
                      <span class="slot-label">号源</span>
                    </button>
                    <span v-else class="no-slot">暂无</span>
                  </div>
                </div>
              </div>
              
              <div class="more-column">
                <button class="btn-more" @click="selectDoctor(doctor)">
                  更多<br/>排班
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getDoctorsByDepartment, getAllDoctors, searchDoctors } from '@/api/doctor';
import { getMedicalItemById } from '@/api/medicalItem';
import { getAvailableDates, getTimeSlots } from '@/api/schedule';
import request from '@/utils/request';

const route = useRoute();
const router = useRouter();

const doctors = ref([]);
const loading = ref(true);
const medicalItem = ref(null);
const departments = ref([]);
const selectedDeptFilter = ref('');
const searchKeyword = ref('');
const dayOffset = ref(0);

// 生成日期头
const headerDays = computed(() => {
  const days = [];
  const today = new Date();
  for (let i = 0; i < 14; i++) {
    const targetDate = new Date(today);
    targetDate.setDate(today.getDate() + dayOffset.value + i);
    
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const weekday = weekdays[targetDate.getDay()];
    const month = targetDate.getMonth() + 1;
    const day = targetDate.getDate();
    const shortDate = `${month}月${day}日`;
    
    const year = targetDate.getFullYear();
    const fullDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    const displayDate = `${year}年${month}月${day}日 ${weekday}`;

    days.push({ weekday, shortDate, fullDate, displayDate });
  }
  return days;
});

const dateRangeText = computed(() => {
  if (headerDays.value.length === 0) return '';
  return `${headerDays.value[0].shortDate} - ${headerDays.value[6].shortDate}`;
});

const prevDatePage = () => {
  if (dayOffset.value >= 7) dayOffset.value -= 7;
};

const nextDatePage = () => {
  dayOffset.value += 7;
  refreshSlots();
};

onMounted(async () => {
  const { deptId, medicalItemId } = route.query;
  selectedDeptFilter.value = deptId || '';

  // 获取检查项目详情
  if (medicalItemId) {
    try {
      const itemData = await getMedicalItemById(medicalItemId);
      medicalItem.value = itemData;
    } catch (e) {
      console.error('Failed to fetch medical item details:', e);
    }
  }

  // 获取科室列表
  try {
    const deptData = await request.get('/api/departments');
    departments.value = Array.isArray(deptData) ? deptData : [];
  } catch (e) {
    console.error('Failed to fetch departments:', e);
  }

  // 获取医生列表
  await fetchDoctors();
});

const fetchDoctors = async () => {
  loading.value = true;
  try {
    let data;
    if (selectedDeptFilter.value) {
      data = await searchDoctors({ departmentId: selectedDeptFilter.value });
    } else {
      data = await getAllDoctors();
    }
    
    const doctorList = data?.content || (Array.isArray(data) ? data : []);
    doctors.value = doctorList.map(doc => ({ ...doc, availabilityMap: {} }));
    
    // 获取每个医生的排班
    for (const doctor of doctors.value) {
      await fetchSlotsForDoctor(doctor);
    }
  } catch (error) {
    console.error('Failed to fetch doctors:', error);
    doctors.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchSlotsForDoctor = async (doctor) => {
  try {
    const availableDates = await getAvailableDates(doctor.id);
    const dates = Array.isArray(availableDates) ? availableDates : [];
    
    for (const dayObj of headerDays.value.slice(0, 7)) {
      const dateStr = dayObj.fullDate;
      const isAvailable = dates.some(d => (d.date || d) === dateStr && d.available !== false);
      
      if (isAvailable) {
        try {
          const slotsData = await getTimeSlots(doctor.id, dateStr);
          doctor.availabilityMap[dateStr] = Array.isArray(slotsData) ? slotsData : [];
        } catch {
          doctor.availabilityMap[dateStr] = [];
        }
      } else {
        doctor.availabilityMap[dateStr] = [];
      }
    }
  } catch (e) {
    console.error('Failed to fetch slots for doctor:', doctor.id, e);
  }
};

const refreshSlots = async () => {
  for (const doctor of doctors.value) {
    await fetchSlotsForDoctor(doctor);
  }
};

const hasSlots = (doctor, dateStr) => {
  const slots = doctor.availabilityMap?.[dateStr] || [];
  return slots.length > 0;
};

const getSlotCount = (doctor, dateStr) => {
  const slots = doctor.availabilityMap?.[dateStr] || [];
  return slots.length;
};

const filterByDepartment = (deptId) => {
  selectedDeptFilter.value = deptId;
  fetchDoctors();
};

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search-results',
      query: { specialty: searchKeyword.value }
    });
  }
};

const selectDoctor = (doctor) => {
  router.push({
    path: '/search-results',
    query: {
      doctorId: doctor.id,
      medicalItemId: route.query.medicalItemId
    }
  });
};

const selectDoctorAndDate = (doctor, dayObj) => {
  router.push({
    path: '/search-results',
    query: {
      doctorId: doctor.id,
      medicalItemId: route.query.medicalItemId,
      date: dayObj.fullDate
    }
  });
};
</script>

<style scoped>
/* 页面整体 */
.doctors-page {
  background-color: #fff;
  min-height: 100vh;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #2A2A2A;
}

/* 顶部导航 */
.top-bar {
  background: #fff;
  height: 72px;
  border-bottom: 1px solid #E0E0E0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-box {
  background: #FFD300;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  border-radius: 0 50% 50% 50%;
  font-size: 16px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
}

.search-bar-inline {
  display: flex;
  align-items: center;
  border: 1px solid #CCC;
  border-radius: 4px;
  height: 44px;
  background: #F8F8F8;
}

.search-input-group {
  display: flex;
  align-items: center;
  padding: 0 12px;
  flex: 1;
}

.search-icon {
  width: 18px;
  height: 18px;
  color: #666;
  margin-right: 8px;
}

.search-input-group input {
  border: none;
  background: transparent;
  outline: none;
  font-size: 14px;
  width: 200px;
}

.search-btn {
  background: #FFD300;
  border: none;
  height: 100%;
  width: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #fff;
  border: 1px solid #DDD;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-back:hover {
  background: #F8F8F8;
}

/* 筛选栏 */
.filter-bar {
  padding: 12px 24px;
  border-bottom: 1px solid #EEE;
  display: flex;
  gap: 10px;
  overflow-x: auto;
  background: #fff;
}

.filter-pill {
  padding: 8px 16px;
  border: 1px solid #CCC;
  border-radius: 20px;
  background: #fff;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}

.filter-pill.active {
  background: #2A2A2A;
  color: #fff;
  border-color: #2A2A2A;
}

/* 容器 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 项目信息 */
.item-info-section {
  background: #FFF9E5;
  padding: 24px 0;
  border-bottom: 1px solid #E5E5E5;
}

.item-info-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.item-icon-wrapper {
  width: 64px;
  height: 64px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.item-icon-wrapper svg {
  width: 32px;
  height: 32px;
}

.item-badge {
  font-size: 12px;
  background: #FFD300;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.item-name {
  font-size: 20px;
  font-weight: 700;
  margin: 6px 0;
}

.item-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.item-meta {
  display: flex;
  gap: 12px;
  align-items: center;
}

.item-price {
  font-size: 18px;
  font-weight: 700;
}

.item-category {
  font-size: 12px;
  background: #fff;
  padding: 4px 10px;
  border-radius: 4px;
}

/* 主内容 */
.main-content {
  padding: 24px 0 60px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h2 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}

.date-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-arrow {
  width: 28px;
  height: 28px;
  border: 1px solid #CCC;
  border-radius: 50%;
  background: #fff;
  font-size: 16px;
  cursor: pointer;
}

.nav-arrow:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.date-range {
  font-size: 14px;
  color: #666;
}

/* 状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 0;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #E5E5E5;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  color: #CCC;
}

.btn-outline {
  background: #fff;
  border: 1px solid #2A2A2A;
  padding: 10px 24px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 16px;
}

/* 医生列表 */
.doctors-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 医生卡片 - Zocdoc 风格 */
.doctor-card {
  display: flex;
  border: 1px solid #E0E0E0;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
}

.card-info {
  flex: 0 0 280px;
  padding: 20px;
  border-right: 1px solid #F0F0F0;
}

.info-top {
  margin-bottom: 12px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #F0F0F0;
}

.doc-name {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 4px;
  color: #2A2A2A;
}

.doc-title {
  font-size: 14px;
  color: #2A2A2A;
  margin: 0 0 2px;
}

.doc-dept {
  font-size: 13px;
  color: #666;
  margin: 0 0 8px;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.stars { display: flex; }
.star-icon { width: 14px; height: 14px; }
.score { font-weight: 700; }
.reviews { color: #888; }

.doc-specialty {
  font-size: 13px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

/* 排班日历 */
.card-calendar {
  flex: 1;
  display: flex;
  background: #FAFAFA;
}

.calendar-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.day-cell {
  border-right: 1px solid #E0E0E0;
  display: flex;
  flex-direction: column;
}

.day-cell:last-child {
  border-right: none;
}

.cell-header {
  background: #F5F5F5;
  padding: 8px 4px;
  text-align: center;
  border-bottom: 1px solid #E0E0E0;
}

.weekday {
  display: block;
  font-size: 11px;
  color: #666;
}

.date {
  display: block;
  font-size: 12px;
  font-weight: 600;
}

.cell-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 4px;
  background: #fff;
  min-height: 60px;
}

.slot-btn {
  width: 100%;
  height: 100%;
  background: #FFD300;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px;
  transition: background 0.2s;
}

.slot-btn:hover {
  background: #F4CA00;
}

.slot-count {
  font-size: 16px;
  font-weight: 800;
}

.slot-label {
  font-size: 10px;
}

.no-slot {
  font-size: 11px;
  color: #999;
}

.more-column {
  width: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-left: 1px solid #E0E0E0;
}

.btn-more {
  border: 1px solid #DDD;
  background: #fff;
  padding: 12px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  text-align: center;
  line-height: 1.3;
}

.btn-more:hover {
  background: #F8F8F8;
}

/* 响应式 */
@media (max-width: 900px) {
  .doctor-card {
    flex-direction: column;
  }
  
  .card-info {
    flex: none;
    border-right: none;
    border-bottom: 1px solid #F0F0F0;
  }
  
  .card-calendar {
    overflow-x: auto;
  }
  
  .calendar-grid {
    min-width: 500px;
  }
}
</style>
