<template>
  <div class="reviews-page">
    <!-- 评分概览 -->
    <div class="rating-overview">
      <div class="rating-main">
        <div class="rating-score">
          <span class="score">{{ averageRating.toFixed(1) }}</span>
          <div class="rating-stars">
            <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" 
              :fill="i <= Math.round(averageRating) ? '#FFD300' : 'none'" 
              :stroke="i <= Math.round(averageRating) ? '#FFD300' : '#d1d5db'" 
              stroke-width="2" width="24" height="24">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
          </div>
          <span class="total-reviews">共 {{ totalReviews }} 条评价</span>
        </div>
      </div>
      
      <div class="rating-distribution">
        <div v-for="n in 5" :key="n" class="rating-bar">
          <span class="bar-label">{{ 6 - n }}星</span>
          <div class="bar-track">
            <div class="bar-fill" :style="{ width: getDistributionPercent(6 - n) + '%' }"></div>
          </div>
          <span class="bar-count">{{ ratingDistribution[6 - n] || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 评价列表 -->
    <div class="reviews-container">
      <div class="section-header">
        <h3>患者评价</h3>
        <div class="filter-tabs">
          <button 
            v-for="tab in filterTabs" 
            :key="tab.value"
            class="tab"
            :class="{ active: currentFilter === tab.value }"
            @click="filterReviews(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
      </div>

      <div v-else-if="reviews.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="64" height="64">
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
        </svg>
        <h3>暂无评价</h3>
        <p>患者评价后将显示在这里</p>
      </div>

      <div v-else class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="patient-avatar">
              {{ review.patientName?.charAt(0) || '患' }}
            </div>
            <div class="patient-info">
              <span class="patient-name">{{ maskName(review.patientName) }}</span>
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <div class="rating-stars small">
              <svg v-for="i in 5" :key="i" viewBox="0 0 24 24" 
                :fill="i <= review.rating ? '#FFD300' : 'none'" 
                :stroke="i <= review.rating ? '#FFD300' : '#d1d5db'" 
                stroke-width="2" width="18" height="18">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
          </div>
          
          <div class="review-content">
            <p>{{ review.content }}</p>
          </div>
          
          <div class="review-tags" v-if="review.tags && review.tags.length > 0">
            <span v-for="tag in review.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="page-btn" 
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          class="page-btn" 
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '@/utils/request';

const loading = ref(true);
const reviews = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const totalReviews = ref(0);
const averageRating = ref(5.0);
const ratingDistribution = ref({});
const currentFilter = ref('all');

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '好评', value: 'positive' },
  { label: '中评', value: 'neutral' },
  { label: '差评', value: 'negative' }
];

onMounted(() => {
  fetchReviews();
  fetchStats();
});

const fetchReviews = async () => {
  loading.value = true;
  try {
    const data = await request.get('/api/doctor/reviews/my', {
      params: { 
        page: currentPage.value, 
        size: 10,
        filter: currentFilter.value !== 'all' ? currentFilter.value : undefined
      }
    });
    reviews.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
    totalReviews.value = data?.totalElements || 0;
  } catch (e) {
    console.error('获取评价失败:', e);
    reviews.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchStats = async () => {
  try {
    const data = await request.get('/api/doctor/reviews/stats');
    if (data) {
      averageRating.value = data.averageRating || 5.0;
      ratingDistribution.value = data.distribution || {};
    }
  } catch (e) {
    console.error('获取评价统计失败:', e);
  }
};

const filterReviews = (filter) => {
  currentFilter.value = filter;
  currentPage.value = 0;
  fetchReviews();
};

const changePage = (page) => {
  currentPage.value = page;
  fetchReviews();
};

const getDistributionPercent = (star) => {
  if (totalReviews.value === 0) return 0;
  const count = ratingDistribution.value[star] || 0;
  return (count / totalReviews.value) * 100;
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
.reviews-page {
  max-width: 900px;
  margin: 0 auto;
}

/* 评分概览 */
.rating-overview {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 32px;
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.rating-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-right: 1px solid #f1f5f9;
}

.rating-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score {
  font-size: 56px;
  font-weight: 800;
  color: #1a1a2e;
  line-height: 1;
}

.rating-stars {
  display: flex;
  gap: 4px;
  margin: 12px 0;
}

.total-reviews {
  font-size: 14px;
  color: #64748b;
}

.rating-distribution {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  font-size: 14px;
  color: #64748b;
  width: 40px;
}

.bar-track {
  flex: 1;
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border-radius: 4px;
  transition: width 0.3s;
}

.bar-count {
  font-size: 14px;
  color: #64748b;
  width: 30px;
  text-align: right;
}

/* 评价列表 */
.reviews-container {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab {
  padding: 6px 16px;
  border: none;
  background: #f3f4f6;
  border-radius: 20px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.tab:hover {
  background: #e5e7eb;
}

.tab.active {
  background: #FFD300;
  color: #1a1a2e;
  font-weight: 600;
}

/* 加载和空状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
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

/* 评价卡片 */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  padding: 20px;
  background: #f8fafc;
  border-radius: 16px;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.patient-avatar {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.patient-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.patient-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.review-date {
  font-size: 13px;
  color: #94a3b8;
}

.rating-stars.small {
  display: flex;
  gap: 2px;
}

.review-content {
  margin-bottom: 12px;
}

.review-content p {
  font-size: 15px;
  color: #475569;
  line-height: 1.6;
  margin: 0;
}

.review-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 12px;
  background: #e0f2fe;
  color: #0369a1;
  border-radius: 20px;
  font-size: 12px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.page-btn {
  padding: 8px 20px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #64748b;
}

@media (max-width: 768px) {
  .rating-overview {
    grid-template-columns: 1fr;
  }
  
  .rating-main {
    border-right: none;
    border-bottom: 1px solid #f1f5f9;
    padding-bottom: 24px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}
</style>

