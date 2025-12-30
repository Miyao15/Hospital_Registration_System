<template>
  <div class="leaves-page">
    <!-- 头部操作区 -->
    <div class="page-header">
      <div class="header-info">
        <h2>请假记录</h2>
        <p>管理您的请假申请</p>
      </div>
      <button class="btn-apply" @click="showApplyModal = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        申请请假
      </button>
    </div>

    <!-- 请假列表 -->
    <div class="leaves-container">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="leaves.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="64" height="64">
          <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"></path>
          <polyline points="14 2 14 8 20 8"></polyline>
        </svg>
        <h3>暂无请假记录</h3>
        <p>点击上方按钮申请请假</p>
      </div>

      <div v-else class="leaves-list">
        <div v-for="leave in leaves" :key="leave.id" class="leave-card">
          <div class="leave-header">
            <div class="leave-type" :class="leave.type?.toLowerCase()">
              {{ getLeaveTypeText(leave.type) }}
            </div>
            <div class="leave-status" :class="leave.status?.toLowerCase()">
              {{ getStatusText(leave.status) }}
            </div>
          </div>
          
          <div class="leave-dates">
            <div class="date-item">
              <span class="label">开始日期</span>
              <span class="value">{{ formatDate(leave.startDate) }}</span>
            </div>
            <div class="date-arrow">→</div>
            <div class="date-item">
              <span class="label">结束日期</span>
              <span class="value">{{ formatDate(leave.endDate) }}</span>
            </div>
            <div class="date-item">
              <span class="label">共计</span>
              <span class="value highlight">{{ leave.days || 1 }}天</span>
            </div>
          </div>
          
          <div class="leave-reason" v-if="leave.reason">
            <span class="label">请假原因：</span>
            <span class="value">{{ leave.reason }}</span>
          </div>
          
          <div class="leave-footer">
            <span class="apply-time">申请时间：{{ formatDateTime(leave.createdAt) }}</span>
            <button 
              v-if="leave.status === 'PENDING'" 
              class="btn-cancel"
              @click="cancelLeave(leave)"
            >
              撤销申请
            </button>
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

    <!-- 申请请假弹窗 -->
    <div class="modal-overlay" v-if="showApplyModal" @click.self="showApplyModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>申请请假</h3>
          <button class="close-btn" @click="showApplyModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        
        <form @submit.prevent="submitLeave" class="modal-form">
          <div class="form-group">
            <label>请假类型</label>
            <select v-model="leaveForm.type" required>
              <option value="">请选择</option>
              <option value="SICK">病假</option>
              <option value="PERSONAL">事假</option>
              <option value="ANNUAL">年假</option>
              <option value="OTHER">其他</option>
            </select>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>开始日期</label>
              <input type="date" v-model="leaveForm.startDate" :min="minDate" required />
            </div>
            <div class="form-group">
              <label>结束日期</label>
              <input type="date" v-model="leaveForm.endDate" :min="leaveForm.startDate || minDate" required />
            </div>
          </div>
          
          <div class="form-group">
            <label>请假原因</label>
            <textarea v-model="leaveForm.reason" placeholder="请填写请假原因..." rows="4"></textarea>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="showApplyModal = false">取消</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const loading = ref(true);
const leaves = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);

const showApplyModal = ref(false);
const submitting = ref(false);
const leaveForm = ref({
  type: '',
  startDate: '',
  endDate: '',
  reason: ''
});

const minDate = computed(() => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().split('T')[0];
});

onMounted(() => {
  fetchLeaves();
});

const fetchLeaves = async () => {
  loading.value = true;
  try {
    const data = await request.get('/api/doctor/leaves/my', {
      params: { page: currentPage.value, size: 10 }
    });
    leaves.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
  } catch (e) {
    console.error('获取请假记录失败:', e);
    leaves.value = [];
  } finally {
    loading.value = false;
  }
};

const submitLeave = async () => {
  if (!leaveForm.value.type || !leaveForm.value.startDate || !leaveForm.value.endDate) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  
  submitting.value = true;
  try {
    await request.post('/api/doctor/leaves', leaveForm.value);
    ElMessage.success('请假申请已提交');
    showApplyModal.value = false;
    leaveForm.value = { type: '', startDate: '', endDate: '', reason: '' };
    fetchLeaves();
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败');
  } finally {
    submitting.value = false;
  }
};

const cancelLeave = async (leave) => {
  try {
    await ElMessageBox.confirm('确定要撤销该请假申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await request.post(`/api/doctor/leaves/${leave.id}/cancel`);
    ElMessage.success('已撤销');
    fetchLeaves();
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('撤销失败');
    }
  }
};

const changePage = (page) => {
  currentPage.value = page;
  fetchLeaves();
};

const getLeaveTypeText = (type) => {
  const map = {
    'SICK': '病假',
    'PERSONAL': '事假',
    'ANNUAL': '年假',
    'OTHER': '其他'
  };
  return map[type] || type;
};

const getStatusText = (status) => {
  const map = {
    'PENDING': '审批中',
    'APPROVED': '已批准',
    'REJECTED': '已拒绝',
    'CANCELLED': '已撤销'
  };
  return map[status] || status;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};
</script>

<style scoped>
.leaves-page {
  max-width: 900px;
  margin: 0 auto;
}

/* 头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-info h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.header-info p {
  font-size: 14px;
  color: #64748b;
  margin: 4px 0 0;
}

.btn-apply {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-apply:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255,211,0,0.4);
}

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

/* 请假列表 */
.leaves-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.leave-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.leave-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.leave-type {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.leave-type.sick {
  background: #fee2e2;
  color: #991b1b;
}

.leave-type.personal {
  background: #dbeafe;
  color: #1d4ed8;
}

.leave-type.annual {
  background: #d1fae5;
  color: #065f46;
}

.leave-type.other {
  background: #f3f4f6;
  color: #6b7280;
}

.leave-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.leave-status.pending {
  background: #fef3c7;
  color: #92400e;
}

.leave-status.approved {
  background: #d1fae5;
  color: #065f46;
}

.leave-status.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.leave-status.cancelled {
  background: #f3f4f6;
  color: #6b7280;
}

.leave-dates {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 16px;
}

.date-item {
  display: flex;
  flex-direction: column;
}

.date-item .label {
  font-size: 12px;
  color: #64748b;
}

.date-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.date-item .value.highlight {
  color: #10b981;
}

.date-arrow {
  font-size: 18px;
  color: #94a3b8;
}

.leave-reason {
  margin-bottom: 16px;
}

.leave-reason .label {
  font-size: 14px;
  color: #64748b;
}

.leave-reason .value {
  font-size: 14px;
  color: #1a1a2e;
}

.leave-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.apply-time {
  font-size: 13px;
  color: #94a3b8;
}

.btn-cancel {
  padding: 6px 16px;
  background: #f3f4f6;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  color: #6b7280;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 20px;
  background: #fff;
  border: 1px solid #e2e8f0;
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

/* 弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
}

.modal-form {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 15px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #FFD300;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-secondary {
  flex: 1;
  padding: 12px;
  background: #f3f4f6;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
}

.btn-primary {
  flex: 1;
  padding: 12px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  cursor: pointer;
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>

