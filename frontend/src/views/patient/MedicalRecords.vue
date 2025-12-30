<template>
  <div class="records-page">
    <div class="page-header">
      <h1>就诊记录</h1>
      <p class="subtitle">查看您的历史就诊记录和诊断结果</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="search-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input type="text" v-model="searchKeyword" placeholder="搜索医生或科室..." @input="handleSearch" />
      </div>
      <div class="date-filter">
        <input type="date" v-model="startDate" @change="fetchRecords" />
        <span>至</span>
        <input type="date" v-model="endDate" @change="fetchRecords" />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载就诊记录...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="records.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </div>
      <h3>暂无就诊记录</h3>
      <p>您还没有完成过任何就诊</p>
    </div>

    <!-- 记录列表 -->
    <div v-else class="records-list">
      <div 
        v-for="record in records" 
        :key="record.id" 
        class="record-card"
        @click="expandRecord(record)"
      >
        <div class="record-header">
          <div class="date-badge">
            <div class="day">{{ formatDay(record.appointmentDate) }}</div>
            <div class="month">{{ formatMonth(record.appointmentDate) }}</div>
          </div>
          <div class="record-summary">
            <h3>{{ record.departmentName }}</h3>
            <p class="doctor-info">{{ record.doctorName }} · {{ record.doctorTitle }}</p>
            <div class="diagnosis-preview" v-if="record.diagnosis">
              <span class="label">诊断：</span>
              <span>{{ record.diagnosis }}</span>
            </div>
          </div>
          <div class="expand-icon" :class="{ expanded: expandedId === record.id }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </div>
        </div>

        <!-- 展开详情 -->
        <transition name="expand">
          <div v-if="expandedId === record.id" class="record-detail">
            <div class="detail-section">
              <h4>就诊信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="label">就诊日期</span>
                  <span class="value">{{ formatFullDate(record.appointmentDate) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">就诊时段</span>
                  <span class="value">{{ record.periodName }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">预约号</span>
                  <span class="value">{{ record.appointmentNo }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">科室位置</span>
                  <span class="value">{{ record.departmentLocation || '暂无' }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section" v-if="record.symptomDesc">
              <h4>病情描述</h4>
              <p class="symptom-text">{{ record.symptomDesc }}</p>
            </div>

            <div class="detail-section" v-if="record.diagnosis">
              <h4>诊断结果</h4>
              <p class="diagnosis-text">{{ record.diagnosis }}</p>
            </div>

            <div class="detail-section" v-if="record.prescription">
              <h4>处方信息</h4>
              <div class="prescription-box">
                <p>{{ record.prescription }}</p>
              </div>
            </div>

            <div class="detail-actions">
              <button class="btn-outline" @click.stop="downloadRecord(record)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                  <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3"></path>
                </svg>
                下载病历
              </button>
              <button class="btn-primary" @click.stop="viewReport(record)" v-if="record.hasReport">
                查看检查报告
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">上一页</button>
      <span>第 {{ currentPage + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getMyAppointments } from '@/api/appointment';
import { ElMessage } from 'element-plus';

const records = ref([]);
const loading = ref(true);
const expandedId = ref(null);
const currentPage = ref(0);
const totalPages = ref(0);
const searchKeyword = ref('');
const startDate = ref('');
const endDate = ref('');

let searchTimer = null;

onMounted(() => {
  fetchRecords();
});

const fetchRecords = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: 10,
      status: 'COMPLETED' // 只获取已完成的记录
    };
    
    const data = await getMyAppointments(params);
    records.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
  } catch (e) {
    console.error('获取就诊记录失败:', e);
    records.value = [];
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    currentPage.value = 0;
    fetchRecords();
  }, 300);
};

const changePage = (page) => {
  currentPage.value = page;
  fetchRecords();
};

const expandRecord = (record) => {
  expandedId.value = expandedId.value === record.id ? null : record.id;
};

const formatDay = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).getDate();
};

const formatMonth = (dateStr) => {
  if (!dateStr) return '';
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
  return months[new Date(dateStr).getMonth()];
};

const formatFullDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  const weekday = weekdays[date.getDay()];
  return `${year}年${month}月${day}日 ${weekday}`;
};

const downloadRecord = (record) => {
  ElMessage.info('病历下载功能开发中');
};

const viewReport = (record) => {
  ElMessage.info('查看报告功能开发中');
};
</script>

<style scoped>
.records-page {
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

/* 筛选区域 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 16px;
  flex: 1;
  max-width: 320px;
}

.search-box svg {
  color: #9ca3af;
}

.search-box input {
  border: none;
  outline: none;
  flex: 1;
  font-size: 14px;
}

.date-filter {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-filter input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.date-filter span {
  color: #666;
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
  margin: 0;
}

/* 记录卡片 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.record-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.record-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
}

.date-badge {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.date-badge .day {
  font-size: 22px;
  font-weight: 800;
  color: #2a2a2a;
  line-height: 1;
}

.date-badge .month {
  font-size: 12px;
  font-weight: 500;
  color: #2a2a2a;
}

.record-summary {
  flex: 1;
}

.record-summary h3 {
  font-size: 16px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 4px 0;
}

.doctor-info {
  font-size: 14px;
  color: #666;
  margin: 0 0 6px 0;
}

.diagnosis-preview {
  font-size: 13px;
  color: #666;
}

.diagnosis-preview .label {
  color: #9ca3af;
}

.expand-icon {
  color: #9ca3af;
  transition: transform 0.2s;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

/* 展开详情 */
.record-detail {
  padding: 0 24px 24px;
  border-top: 1px solid #f0f0f0;
  margin-top: -4px;
}

.detail-section {
  padding-top: 20px;
}

.detail-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
  margin: 0 0 12px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item .label {
  font-size: 12px;
  color: #9ca3af;
}

.detail-item .value {
  font-size: 14px;
  color: #2a2a2a;
}

.symptom-text, .diagnosis-text {
  font-size: 14px;
  color: #2a2a2a;
  margin: 0;
  line-height: 1.6;
}

.prescription-box {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  border-left: 3px solid #FFD300;
}

.prescription-box p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

.detail-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.btn-outline {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-outline:hover {
  background: #f3f4f6;
}

.btn-primary {
  padding: 10px 20px;
  background: #FFD300;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover {
  background: #f4ca00;
}

/* 动画 */
.expand-enter-active, .expand-leave-active {
  transition: all 0.3s ease;
}

.expand-enter-from, .expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to, .expand-leave-from {
  opacity: 1;
  max-height: 500px;
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
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    max-width: none;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

