<template>
  <div class="appointments-page">
    <div class="page-header">
      <h1>我的预约</h1>
      <p class="subtitle">查看和管理您的预约记录</p>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-tabs">
      <button 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="tab-btn"
        :class="{ active: currentStatus === tab.value }"
        @click="filterByStatus(tab.value)"
      >
        {{ tab.label }}
        <span class="count" v-if="tab.count > 0">{{ tab.count }}</span>
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载预约记录...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="appointments.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
      </div>
      <h3>暂无预约记录</h3>
      <p>您还没有任何预约，去预约一个检查项目吧</p>
      <button class="btn-primary" @click="$router.push('/patient/home')">去预约</button>
    </div>

    <!-- 预约列表 -->
    <div v-else class="appointments-list">
      <div 
        v-for="apt in appointments" 
        :key="apt.id" 
        class="appointment-card"
        :class="getStatusClass(apt.status)"
      >
        <div class="card-left">
          <div class="status-badge" :class="apt.status">
            {{ apt.statusName }}
          </div>
          <div class="appointment-info">
            <h3 class="doctor-name">{{ apt.doctorName }}</h3>
            <p class="doctor-title">{{ apt.doctorTitle }} · {{ apt.departmentName }}</p>
            <div class="time-info">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span>{{ formatDate(apt.appointmentDate) }} {{ apt.periodName }}</span>
            </div>
            <div class="location-info" v-if="apt.departmentLocation">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <span>{{ apt.departmentLocation }}</span>
            </div>
            <div class="medical-item-info" v-if="apt.medicalItemName">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
              <span>检查项目：{{ apt.medicalItemName }}</span>
              <span v-if="apt.medicalItemPrice" class="item-price">¥{{ apt.medicalItemPrice }}</span>
            </div>
          </div>
        </div>

        <div class="card-right">
          <div class="appointment-no">
            预约号：{{ apt.appointmentNo }}
          </div>
          <div class="card-actions" v-if="apt.status === 'PENDING'">
            <button class="btn-outline" @click="openReschedule(apt)">改签</button>
            <button class="btn-outline danger" @click="cancelAppointment(apt)">取消预约</button>
          </div>
          <div class="card-actions" v-else-if="apt.status === 'COMPLETED'">
            <button class="btn-outline" @click="viewDetail(apt)">查看详情</button>
            <button class="btn-primary-small" @click="openReview(apt)" v-if="!apt.hasReviewed">评价医生</button>
            <span class="reviewed-badge" v-else>已评价</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价弹窗 -->
    <transition name="modal">
      <div v-if="showReviewModal" class="modal-overlay" @click.self="closeReviewModal">
        <div class="modal-content review-modal">
          <div class="modal-header">
            <h3>评价医生</h3>
            <button class="close-btn" @click="closeReviewModal">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>
          <div class="review-doctor-info" v-if="reviewingAppointment">
            <span class="doctor-name">{{ reviewingAppointment.doctorName }}</span>
            <span class="visit-date">就诊日期：{{ formatDate(reviewingAppointment.appointmentDate) }}</span>
          </div>
          <div class="rating-input">
            <span class="rating-label">评分：</span>
            <div class="stars-input">
              <svg v-for="i in 5" :key="i" 
                @click="reviewForm.rating = i"
                @mouseenter="hoverRating = i"
                @mouseleave="hoverRating = 0"
                viewBox="0 0 24 24" 
                :fill="i <= (hoverRating || reviewForm.rating) ? '#FFD300' : 'none'" 
                :stroke="i <= (hoverRating || reviewForm.rating) ? '#FFD300' : '#d1d5db'" 
                stroke-width="2" width="32" height="32"
                class="star-btn">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
            <span class="rating-text">{{ ratingTexts[reviewForm.rating - 1] || '请选择' }}</span>
          </div>
          <div class="review-input">
            <label>评价内容：</label>
            <textarea v-model="reviewForm.content" placeholder="分享您的就诊体验，帮助其他患者..." rows="4"></textarea>
          </div>
          <div class="modal-actions">
            <button class="btn-cancel" @click="closeReviewModal">取消</button>
            <button class="btn-primary" @click="submitReview" :disabled="submittingReview">
              {{ submittingReview ? '提交中...' : '提交评价' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 改签弹窗 -->
    <transition name="modal">
      <div v-if="showRescheduleModal" class="modal-overlay" @click.self="closeRescheduleModal">
        <div class="modal-content reschedule-modal">
          <div class="modal-header">
            <h3>改签预约</h3>
            <button class="close-btn" @click="closeRescheduleModal">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
          </div>
          <div class="reschedule-info" v-if="reschedulingAppointment">
            <p>当前预约：<strong>{{ reschedulingAppointment.doctorName }}</strong></p>
            <p>原日期：<strong>{{ formatDate(reschedulingAppointment.appointmentDate) }} {{ reschedulingAppointment.periodName }}</strong></p>
          </div>
          <div class="reschedule-form">
            <div class="form-group">
              <label>选择新日期：</label>
              <input type="date" v-model="rescheduleForm.newDate" :min="minDate" />
            </div>
            <div class="form-group">
              <label>选择时段：</label>
              <select v-model="rescheduleForm.newPeriod">
                <option value="">请选择</option>
                <option value="MORNING">上午</option>
                <option value="AFTERNOON">下午</option>
              </select>
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn-cancel" @click="closeRescheduleModal">取消</button>
            <button class="btn-primary" @click="submitReschedule" :disabled="submittingReschedule">
              {{ submittingReschedule ? '提交中...' : '确认改签' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">上一页</button>
      <span>第 {{ currentPage + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { getMyAppointments, cancelAppointment as cancelApt, rescheduleAppointment } from '@/api/appointment';
import { createReview } from '@/api/review';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();

const appointments = ref([]);
const loading = ref(true);
const currentStatus = ref('');
const currentPage = ref(0);
const totalPages = ref(0);
const totalElements = ref(0);

// 评价相关
const showReviewModal = ref(false);
const reviewingAppointment = ref(null);
const submittingReview = ref(false);
const hoverRating = ref(0);
const reviewForm = ref({
  rating: 0,
  content: ''
});
const ratingTexts = ['非常差', '较差', '一般', '满意', '非常满意'];

// 改签相关
const showRescheduleModal = ref(false);
const reschedulingAppointment = ref(null);
const submittingReschedule = ref(false);
const rescheduleForm = ref({
  newDate: '',
  newPeriod: ''
});

const minDate = computed(() => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().split('T')[0];
});

const statusTabs = ref([
  { label: '全部', value: '', count: 0 },
  { label: '待就诊', value: 'PENDING', count: 0 },
  { label: '已完成', value: 'COMPLETED', count: 0 },
  { label: '已取消', value: 'CANCELLED', count: 0 }
]);

onMounted(() => {
  fetchAppointments();
});

const fetchAppointments = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: 10
    };
    if (currentStatus.value) {
      params.status = currentStatus.value;
    }
    
    const data = await getMyAppointments(params);
    appointments.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
    totalElements.value = data?.totalElements || 0;
  } catch (e) {
    console.error('获取预约记录失败:', e);
    appointments.value = [];
  } finally {
    loading.value = false;
  }
};

const filterByStatus = (status) => {
  currentStatus.value = status;
  currentPage.value = 0;
  fetchAppointments();
};

const changePage = (page) => {
  currentPage.value = page;
  fetchAppointments();
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  const weekday = weekdays[date.getDay()];
  return `${year}年${month}月${day}日 ${weekday}`;
};

const getStatusClass = (status) => {
  return status?.toLowerCase() || '';
};

const cancelAppointment = async (apt) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消 ${apt.doctorName} 在 ${formatDate(apt.appointmentDate)} 的预约吗？`,
      '取消预约',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    );
    
    await cancelApt(apt.id, '用户主动取消');
    ElMessage.success('预约已取消');
    fetchAppointments();
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '取消失败，请稍后重试');
    }
  }
};

const viewDetail = (apt) => {
  router.push(`/doctor/${apt.doctorId}`);
};

// 评价功能
const openReview = (apt) => {
  reviewingAppointment.value = apt;
  reviewForm.value = { rating: 0, content: '' };
  showReviewModal.value = true;
};

const closeReviewModal = () => {
  showReviewModal.value = false;
  reviewingAppointment.value = null;
};

const submitReview = async () => {
  if (reviewForm.value.rating === 0) {
    ElMessage.warning('请选择评分');
    return;
  }
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请填写评价内容');
    return;
  }
  
  submittingReview.value = true;
  try {
    await createReview({
      doctorId: reviewingAppointment.value.doctorId,
      appointmentId: reviewingAppointment.value.id,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    });
    ElMessage.success('评价成功，感谢您的反馈！');
    closeReviewModal();
    // 标记已评价
    const apt = appointments.value.find(a => a.id === reviewingAppointment.value.id);
    if (apt) apt.hasReviewed = true;
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '评价失败，请稍后重试');
  } finally {
    submittingReview.value = false;
  }
};

// 改签功能
const openReschedule = (apt) => {
  reschedulingAppointment.value = apt;
  rescheduleForm.value = { newDate: '', newPeriod: '' };
  showRescheduleModal.value = true;
};

const closeRescheduleModal = () => {
  showRescheduleModal.value = false;
  reschedulingAppointment.value = null;
};

const submitReschedule = async () => {
  if (!rescheduleForm.value.newDate) {
    ElMessage.warning('请选择新日期');
    return;
  }
  if (!rescheduleForm.value.newPeriod) {
    ElMessage.warning('请选择时段');
    return;
  }
  
  submittingReschedule.value = true;
  try {
    await rescheduleAppointment(reschedulingAppointment.value.id, {
      newDate: rescheduleForm.value.newDate,
      newPeriod: rescheduleForm.value.newPeriod
    });
    ElMessage.success('改签成功！');
    closeRescheduleModal();
    fetchAppointments();
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '改签失败，请稍后重试');
  } finally {
    submittingReschedule.value = false;
  }
};
</script>

<style scoped>
.appointments-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.tab-btn {
  padding: 10px 20px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.tab-btn:hover {
  border-color: #FFD300;
}

.tab-btn.active {
  background: #FFD300;
  border-color: #FFD300;
  font-weight: 600;
}

.tab-btn .count {
  background: rgba(0,0,0,0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

/* 加载和空状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
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

@keyframes spin { to { transform: rotate(360deg); } }

.empty-icon {
  width: 80px;
  height: 80px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #666;
  margin: 0 0 24px 0;
}

.btn-primary {
  padding: 12px 32px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.btn-primary:hover {
  background: #f4ca00;
}

/* 预约卡片 */
.appointments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.appointment-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  border-left: 4px solid #FFD300;
}

.appointment-card.cancelled {
  border-left-color: #9ca3af;
  opacity: 0.7;
}

.appointment-card.completed {
  border-left-color: #10b981;
}

.card-left {
  display: flex;
  gap: 16px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  height: fit-content;
}

.status-badge.PENDING {
  background: #FEF3C7;
  color: #92400E;
}

.status-badge.COMPLETED {
  background: #D1FAE5;
  color: #065F46;
}

.status-badge.CANCELLED {
  background: #F3F4F6;
  color: #6B7280;
}

.status-badge.EXPIRED {
  background: #FEE2E2;
  color: #991B1B;
}

.appointment-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.doctor-name {
  font-size: 18px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0;
}

.doctor-title {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.time-info, .location-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #2a2a2a;
}

.time-info svg, .location-info svg {
  color: #666;
}

.card-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.appointment-no {
  font-size: 12px;
  color: #9ca3af;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.btn-outline {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.btn-outline:hover {
  background: #f3f4f6;
}

.btn-outline.danger {
  color: #dc2626;
  border-color: #fecaca;
}

.btn-outline.danger:hover {
  background: #fef2f2;
}

.btn-primary-small {
  padding: 8px 16px;
  background: #FFD300;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary-small:hover {
  background: #f4ca00;
}

.reviewed-badge {
  padding: 8px 16px;
  background: #d1fae5;
  color: #065f46;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

/* Modal 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 480px;
  padding: 24px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
}

/* 评价弹窗 */
.review-doctor-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.review-doctor-info .doctor-name {
  font-size: 16px;
  font-weight: 600;
  color: #2a2a2a;
}

.review-doctor-info .visit-date {
  font-size: 13px;
  color: #666;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.rating-label {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
}

.stars-input {
  display: flex;
  gap: 4px;
}

.star-btn {
  cursor: pointer;
  transition: transform 0.15s;
}

.star-btn:hover {
  transform: scale(1.1);
}

.rating-text {
  font-size: 14px;
  color: #666;
}

.review-input {
  margin-bottom: 20px;
}

.review-input label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
  margin-bottom: 8px;
}

.review-input textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  box-sizing: border-box;
}

.review-input textarea:focus {
  outline: none;
  border-color: #FFD300;
  box-shadow: 0 0 0 3px rgba(255, 211, 0, 0.2);
}

/* 改签弹窗 */
.reschedule-info {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.reschedule-info p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
}

.reschedule-info p:last-child {
  margin-bottom: 0;
}

.reschedule-info strong {
  color: #2a2a2a;
}

.reschedule-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
}

.form-group input,
.form-group select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #FFD300;
  box-shadow: 0 0 0 3px rgba(255, 211, 0, 0.2);
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 12px 24px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #f3f4f6;
}

.modal-actions .btn-primary {
  padding: 12px 24px;
}

.modal-actions .btn-primary:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* Modal 动画 */
.modal-enter-active, .modal-leave-active {
  transition: opacity 0.3s;
}

.modal-enter-from, .modal-leave-to {
  opacity: 0;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.pagination button {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #666;
}

@media (max-width: 640px) {
  .appointment-card {
    flex-direction: column;
    gap: 16px;
  }
  
  .card-right {
    align-items: flex-start;
    width: 100%;
  }
  
  .filter-tabs {
    flex-wrap: wrap;
  }
}
</style>

