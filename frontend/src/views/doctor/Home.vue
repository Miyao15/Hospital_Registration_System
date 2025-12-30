<template>
  <div class="doctor-home">
    <!-- Hero区域 - Oatmeal风格 -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">早上好，{{ doctorName }} 医生</h1>
          <p class="hero-subtitle">{{ todayDate }} · 今天又是充满希望的一天</p>
        </div>
        <div class="hero-image">
          <div class="hero-image-overlay"></div>
          <img src="https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=500&h=400&fit=crop&q=80" alt="医疗健康" />
        </div>
      </div>
    </div>

    <!-- 统计卡片 - Zocdoc 风格 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">今日预约</span>
          <div class="stat-icon today">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
        </div>
        <div class="stat-value">{{ stats.todayCount }}</div>
        <div class="stat-footer" v-if="stats.pendingCount > 0">
          <span class="stat-detail">待就诊 {{ stats.pendingCount }} 人</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">本周完成</span>
          <div class="stat-icon completed">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <path d="M22 11.08V12a10 10 0 11-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
        </div>
        <div class="stat-value">{{ stats.completedCount }}</div>
        <div class="stat-footer">
          <span class="stat-detail positive">较上周 +12%</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">患者评分</span>
          <div class="stat-icon rating">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
          </div>
        </div>
        <div class="stat-value">{{ stats.rating || '5.0' }}</div>
        <div class="stat-footer">
          <span class="stat-detail">{{ stats.reviewCount || 0 }} 条评价</span>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-header">
          <span class="stat-label">本月预约</span>
          <div class="stat-icon month">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"></path>
            </svg>
          </div>
        </div>
        <div class="stat-value">{{ stats.weekCount }}</div>
        <div class="stat-footer">
          <span class="stat-detail">环比 +8%</span>
        </div>
      </div>
    </div>

    <!-- 今日预约 - Zocdoc 风格 -->
    <div class="appointments-section">
      <div class="section-header">
        <h2 class="section-title">今日预约</h2>
        <router-link to="/doctor/appointments" class="link-more">查看全部 ›</router-link>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="todayAppointments.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="40" height="40">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
        <p>今日暂无预约</p>
      </div>

      <div v-else class="appointments-list">
        <div 
          v-for="apt in todayAppointments" 
          :key="apt.id" 
          class="appointment-card"
          :class="apt.status?.toLowerCase()"
        >
          <div class="appointment-time">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            <span>{{ apt.periodName || '上午' }}</span>
          </div>
          <div class="appointment-info">
            <div class="patient-row">
              <span class="patient-name">{{ apt.patientName }}</span>
              <span class="status-tag" :class="apt.status?.toLowerCase()">
                {{ getStatusText(apt.status) }}
              </span>
            </div>
            <p class="symptom-text" v-if="apt.symptomDesc">{{ apt.symptomDesc }}</p>
            <p class="medical-item-text" v-if="apt.medicalItemName">
              检查项目：{{ apt.medicalItemName }}
              <span v-if="apt.medicalItemPrice">(¥{{ apt.medicalItemPrice }})</span>
            </p>
          </div>
          <div class="appointment-actions" v-if="apt.status === 'PENDING'">
            <button class="btn-sm check-in" @click="handleCheckIn(apt)">签到</button>
            <button class="btn-sm complete" @click="handleComplete(apt)">完成</button>
            <button class="btn-sm cancel" @click="handleNoShow(apt)">爽约</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能卡片区域 - Oatmeal风格 -->
    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card" @click="$router.push('/doctor/appointments')">
          <div class="feature-icon" style="background: #FFF9E5;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
          </div>
          <h3 class="feature-title">预约管理</h3>
          <p class="feature-desc">查看和管理患者预约，处理就诊流程</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/doctor/schedule')">
          <div class="feature-icon" style="background: #E8F5E9;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
          </div>
          <h3 class="feature-title">排班日历</h3>
          <p class="feature-desc">设置和管理您的出诊时间安排</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/doctor/leaves')">
          <div class="feature-icon" style="background: #E3F2FD;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
            </svg>
          </div>
          <h3 class="feature-title">请假管理</h3>
          <p class="feature-desc">申请和管理您的请假申请</p>
          <div class="feature-link">查看详情 →</div>
        </div>

        <div class="feature-card" @click="$router.push('/doctor/reviews')">
          <div class="feature-icon" style="background: #FFF8E1;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
          </div>
          <h3 class="feature-title">我的评价</h3>
          <p class="feature-desc">查看患者对您的评价和反馈</p>
          <div class="feature-link">查看详情 →</div>
        </div>
      </div>
    </div>

    <!-- 最近评价 -->
    <div class="reviews-section">
      <div class="reviews-card">
        <div class="card-header">
          <h3 class="card-title">最近评价</h3>
          <router-link to="/doctor/reviews" class="link-more">查看全部 ›</router-link>
        </div>
        <div v-if="recentReviews.length === 0" class="no-data">
          <p>暂无评价</p>
        </div>
        <div v-else class="reviews-list">
          <div v-for="review in recentReviews" :key="review.id" class="review-item">
            <div class="review-header">
              <div class="stars">
                <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" 
                  :fill="i <= review.rating ? '#FFD300' : 'none'" 
                  :stroke="i <= review.rating ? '#FFD300' : '#DDD'" 
                  stroke-width="2" width="12" height="12">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                </svg>
              </div>
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <p class="review-text">{{ review.content }}</p>
          </div>
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
const recentReviews = ref([]);
const stats = ref({
  todayCount: 0,
  pendingCount: 0,
  completedCount: 0,
  weekCount: 0,
  rating: '5.0',
  reviewCount: 0
});

const doctorName = computed(() => userStore.userInfo?.realName || '医生');

const todayDate = computed(() => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const day = now.getDate();
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  return `${year}年${month}月${day}日 ${weekdays[now.getDay()]}`;
});

onMounted(async () => {
  await Promise.all([
    fetchTodayAppointments(),
    fetchRecentReviews(),
    fetchReviewStats()
  ]);
});

const fetchTodayAppointments = async () => {
  loading.value = true;
  try {
    const data = await request.get('/api/doctor/work/appointments/today');
    todayAppointments.value = data || [];
    
    // 计算统计数据
    stats.value.todayCount = todayAppointments.value.length;
    stats.value.pendingCount = todayAppointments.value.filter(a => a.status === 'PENDING').length;
    stats.value.completedCount = todayAppointments.value.filter(a => a.status === 'COMPLETED').length;
  } catch (e) {
    console.error('获取今日预约失败:', e);
    todayAppointments.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchRecentReviews = async () => {
  try {
    const data = await request.get('/api/doctor/reviews/my', { params: { page: 0, size: 3 } });
    recentReviews.value = data?.content || [];
    stats.value.reviewCount = data?.totalElements || 0;
  } catch (e) {
    console.error('获取评价失败:', e);
  }
};

const fetchReviewStats = async () => {
  try {
    const data = await request.get('/api/doctor/reviews/stats');
    if (data) {
      stats.value.rating = data.averageRating ? data.averageRating.toFixed(1) : '5.0';
      stats.value.reviewCount = data.totalCount || 0;
    }
  } catch (e) {
    console.error('获取评分统计失败:', e);
  }
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

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}/${date.getDate()}`;
};

const handleCheckIn = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/check-in`);
    ElMessage.success('签到成功');
    apt.status = 'CHECKED_IN';
  } catch (e) {
    ElMessage.error('签到失败');
  }
};

const handleComplete = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/complete`);
    ElMessage.success('已完成就诊');
    apt.status = 'COMPLETED';
    stats.value.completedCount++;
    stats.value.pendingCount--;
  } catch (e) {
    ElMessage.error('操作失败');
  }
};

const handleNoShow = async (apt) => {
  try {
    await request.post(`/api/doctor/work/appointments/${apt.id}/no-show`);
    ElMessage.warning('已标记为爽约');
    apt.status = 'NO_SHOW';
    stats.value.pendingCount--;
  } catch (e) {
    ElMessage.error('操作失败');
  }
};
</script>

<style scoped>
/* ========== Oatmeal风格首页 ========== */
.doctor-home {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Hero区域 */
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

/* 统计卡片 - Zocdoc 风格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  transform: translateY(-2px);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stat-label {
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.stat-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.today {
  background: #FFF9E5;
  color: #FFD300;
}

.stat-icon.completed {
  background: #E8F5E9;
  color: #2E7D32;
}

.stat-icon.rating {
  background: #FFF8E1;
  color: #F57C00;
}

.stat-icon.month {
  background: #E3F2FD;
  color: #1976D2;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #2A2A2A;
  margin-bottom: 8px;
}

.stat-footer {
  display: flex;
  align-items: center;
}

.stat-detail {
  font-size: 12px;
  color: #999;
}

.stat-detail.positive {
  color: #2E7D32;
}

/* 今日预约部分 */
.appointments-section {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0;
}

.link-more {
  font-size: 13px;
  color: #2A2A2A;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}

.link-more:hover {
  color: #FFD300;
}

/* 加载和空状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  color: #999;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #F0F0F0;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-state p, .loading-state p {
  margin: 12px 0 0 0;
  font-size: 14px;
}

/* 预约列表 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.appointment-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #FAFAFA;
  border: 1px solid #F0F0F0;
  border-radius: 6px;
  transition: all 0.2s;
}

.appointment-card:hover {
  background: #F5F5F5;
  border-color: #E8E8E8;
}

.appointment-time {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 80px;
  color: #666;
  font-size: 13px;
  font-weight: 500;
}

.appointment-time svg {
  color: #999;
}

.appointment-info {
  flex: 1;
}

.patient-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.patient-name {
  font-size: 15px;
  font-weight: 600;
  color: #2A2A2A;
}

.status-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 12px;
  font-weight: 600;
}

.status-tag.pending {
  background: #FFF8E1;
  color: #F57C00;
}

.status-tag.checked_in {
  background: #E3F2FD;
  color: #1976D2;
}

.status-tag.completed {
  background: #E8F5E9;
  color: #2E7D32;
}

.status-tag.no_show {
  background: #FFEBEE;
  color: #C62828;
}

.symptom-text {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.appointment-actions {
  display: flex;
  gap: 8px;
}

.btn-sm {
  padding: 6px 14px;
  border: 1px solid #E8E8E8;
  background: #fff;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #2A2A2A;
}

.btn-sm.check-in {
  background: #E3F2FD;
  border-color: #E3F2FD;
  color: #1976D2;
}

.btn-sm.check-in:hover {
  background: #BBDEFB;
}

.btn-sm.complete {
  background: #E8F5E9;
  border-color: #E8F5E9;
  color: #2E7D32;
}

.btn-sm.complete:hover {
  background: #C8E6C9;
}

.btn-sm.cancel {
  background: #F5F5F5;
  border-color: #E8E8E8;
}

.btn-sm.cancel:hover {
  background: #E8E8E8;
}

/* 功能卡片区域 */
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

/* 评价卡片 */
.reviews-card {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.no-data {
  text-align: center;
  padding: 32px 20px;
  color: #999;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-item {
  padding: 14px;
  background: #FAFAFA;
  border-radius: 6px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stars {
  display: flex;
  gap: 2px;
}

.review-date {
  font-size: 11px;
  color: #999;
}

.review-text {
  font-size: 13px;
  color: #666;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 响应式 */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
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
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
