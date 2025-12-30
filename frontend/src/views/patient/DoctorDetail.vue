<template>
  <div class="doctor-detail-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="$router.back()">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
        <line x1="19" y1="12" x2="5" y2="12"></line>
        <polyline points="12 19 5 12 12 5"></polyline>
      </svg>
      返回
    </button>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载医生信息...</p>
    </div>

    <template v-else-if="doctor">
      <!-- 医生信息卡片 -->
      <div class="doctor-card">
        <div class="doctor-avatar">
          <img :src="doctor.avatarUrl || defaultAvatar" alt="医生头像" />
        </div>
        <div class="doctor-info">
          <h1 class="doctor-name">{{ doctor.name }}</h1>
          <p class="doctor-title">{{ doctor.titleName }} · {{ doctor.departmentName }}</p>
          <div class="doctor-meta">
            <div class="meta-item">
              <span class="label">专长：</span>
              <span class="value">{{ doctor.specialty || '暂无' }}</span>
            </div>
            <div class="meta-item" v-if="doctor.education">
              <span class="label">学历：</span>
              <span class="value">{{ doctor.education }}</span>
            </div>
          </div>
          <div class="rating-section">
            <div class="stars">
              <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" 
                :fill="i <= Math.round(doctor.rating || 0) ? '#FFD300' : 'none'" 
                :stroke="i <= Math.round(doctor.rating || 0) ? '#FFD300' : '#d1d5db'" 
                stroke-width="2" width="20" height="20">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
            <span class="rating-text">{{ (doctor.rating || 0).toFixed(1) }} 分</span>
            <span class="review-count">（{{ doctor.reviewCount || 0 }} 条评价）</span>
          </div>
        </div>
        <div class="doctor-action">
          <button class="btn-primary" @click="goBooking">立即预约</button>
        </div>
      </div>

      <!-- 详细信息 -->
      <div class="detail-sections">
        <!-- 医生介绍 -->
        <div class="section-card">
          <h2 class="section-title">医生简介</h2>
          <p class="intro-text">{{ doctor.introduction || '暂无介绍' }}</p>
        </div>

        <!-- 出诊信息 -->
        <div class="section-card">
          <h2 class="section-title">出诊信息</h2>
          <div class="schedule-info">
            <div class="info-item">
              <span class="label">所属科室</span>
              <span class="value">{{ doctor.departmentName }}</span>
            </div>
            <div class="info-item" v-if="doctor.departmentLocation">
              <span class="label">诊室位置</span>
              <span class="value">{{ doctor.departmentLocation }}</span>
            </div>
            <div class="info-item">
              <span class="label">排班信息</span>
              <span class="value">{{ doctor.scheduleInfo || '请查看预约页面' }}</span>
            </div>
          </div>
        </div>

        <!-- 患者评价 -->
        <div class="section-card">
          <div class="section-header">
            <h2 class="section-title">患者评价</h2>
            <span class="review-total">共 {{ reviews.length }} 条</span>
          </div>

          <div v-if="loadingReviews" class="loading-reviews">
            <div class="spinner small"></div>
          </div>
          <div v-else-if="reviews.length === 0" class="no-reviews">
            <p>暂无患者评价</p>
          </div>
          <div v-else class="reviews-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <div class="reviewer-info">
                  <span class="reviewer-name">{{ maskName(review.patientName) }}</span>
                  <div class="review-stars">
                    <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" 
                      :fill="i <= review.rating ? '#FFD300' : 'none'" 
                      :stroke="i <= review.rating ? '#FFD300' : '#d1d5db'" 
                      stroke-width="2" width="14" height="14">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                    </svg>
                  </div>
                </div>
                <span class="review-time">{{ formatDate(review.createdAt) }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
            </div>
          </div>

          <!-- 加载更多评价 -->
          <div v-if="hasMoreReviews" class="load-more">
            <button class="btn-text" @click="loadMoreReviews">加载更多评价</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 未找到医生 -->
    <div v-else class="not-found">
      <h2>未找到该医生</h2>
      <p>请返回重新选择</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getDoctorById, getDoctorReviews } from '@/api/doctor';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const doctor = ref(null);
const reviews = ref([]);
const loading = ref(true);
const loadingReviews = ref(false);
const reviewPage = ref(0);
const hasMoreReviews = ref(false);

onMounted(async () => {
  const doctorId = route.params.id;
  if (!doctorId) {
    loading.value = false;
    return;
  }
  
  try {
    const data = await getDoctorById(doctorId);
    doctor.value = data;
    await loadReviews();
  } catch (e) {
    console.error('获取医生信息失败:', e);
    ElMessage.error('获取医生信息失败');
  } finally {
    loading.value = false;
  }
});

const loadReviews = async () => {
  loadingReviews.value = true;
  try {
    const data = await getDoctorReviews(route.params.id, {
      page: reviewPage.value,
      size: 5
    });
    if (reviewPage.value === 0) {
      reviews.value = data?.content || [];
    } else {
      reviews.value = [...reviews.value, ...(data?.content || [])];
    }
    hasMoreReviews.value = !data?.last;
  } catch (e) {
    console.error('获取评价失败:', e);
  } finally {
    loadingReviews.value = false;
  }
};

const loadMoreReviews = () => {
  reviewPage.value++;
  loadReviews();
};

const goBooking = () => {
  router.push({
    path: '/search-results',
    query: { doctorId: doctor.value.id }
  });
};

const maskName = (name) => {
  if (!name || name.length < 2) return name || '匿名';
  return name.charAt(0) + '*'.repeat(name.length - 1);
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};
</script>

<style scoped>
.doctor-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 24px;
}

.back-btn:hover {
  background: #f3f4f6;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

.spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* 医生卡片 */
.doctor-card {
  display: flex;
  gap: 24px;
  padding: 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 24px;
}

.doctor-avatar {
  flex-shrink: 0;
}

.doctor-avatar img {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  object-fit: cover;
  border: 3px solid #FFD300;
}

.doctor-info {
  flex: 1;
}

.doctor-name {
  font-size: 28px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.doctor-title {
  font-size: 16px;
  color: #666;
  margin: 0 0 16px 0;
}

.doctor-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.meta-item {
  font-size: 14px;
}

.meta-item .label {
  color: #9ca3af;
}

.meta-item .value {
  color: #2a2a2a;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  display: flex;
  gap: 2px;
}

.rating-text {
  font-size: 16px;
  font-weight: 700;
  color: #2a2a2a;
}

.review-count {
  font-size: 14px;
  color: #9ca3af;
}

.doctor-action {
  display: flex;
  align-items: flex-start;
}

.btn-primary {
  padding: 14px 32px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.btn-primary:hover {
  background: #f4ca00;
}

/* 详细信息区域 */
.detail-sections {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #FFD300;
  display: inline-block;
}

.section-header .section-title {
  margin-bottom: 0;
}

.review-total {
  font-size: 14px;
  color: #9ca3af;
}

.intro-text {
  font-size: 15px;
  color: #4b5563;
  line-height: 1.8;
  margin: 0;
}

.schedule-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 13px;
  color: #9ca3af;
}

.info-item .value {
  font-size: 15px;
  color: #2a2a2a;
  font-weight: 500;
}

/* 评价列表 */
.loading-reviews {
  display: flex;
  justify-content: center;
  padding: 24px;
}

.no-reviews {
  text-align: center;
  padding: 32px;
  color: #9ca3af;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.review-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-name {
  font-weight: 600;
  color: #2a2a2a;
}

.review-stars {
  display: flex;
  gap: 2px;
}

.review-time {
  font-size: 12px;
  color: #9ca3af;
}

.review-content {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.btn-text {
  padding: 10px 24px;
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.btn-text:hover {
  background: #f3f4f6;
}

.not-found {
  text-align: center;
  padding: 80px 20px;
}

.not-found h2 {
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.not-found p {
  color: #666;
}

@media (max-width: 640px) {
  .doctor-card {
    flex-direction: column;
    text-align: center;
  }
  
  .doctor-avatar {
    display: flex;
    justify-content: center;
  }
  
  .doctor-action {
    justify-content: center;
  }
  
  .rating-section {
    justify-content: center;
  }
}
</style>

