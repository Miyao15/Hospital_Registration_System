<template>
  <div class="admin-leaves">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">请假审批</h1>
      <p class="page-subtitle">审批医生的请假申请</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="fetchLeaves">
        <el-radio-button label="PENDING">待审批</el-radio-button>
        <el-radio-button label="APPROVED">已批准</el-radio-button>
        <el-radio-button label="REJECTED">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 请假列表 -->
    <div class="leaves-list" v-loading="loading">
      <div 
        v-for="leave in leaves" 
        :key="leave.id"
        class="leave-card"
      >
        <div class="leave-header">
          <div class="doctor-info">
            <img :src="defaultAvatar" class="doctor-avatar" />
            <div class="doctor-detail">
              <h3 class="doctor-name">{{ leave.doctorName }}</h3>
              <p class="doctor-dept">{{ leave.departmentName }}</p>
            </div>
          </div>
          <el-tag :type="getStatusType(leave.status)" size="large">
            {{ getStatusText(leave.status) }}
          </el-tag>
        </div>

        <div class="leave-content">
          <div class="leave-info-row">
            <div class="info-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span class="info-label">请假时间:</span>
              <span class="info-value">{{ formatDate(leave.startDate) }} 至 {{ formatDate(leave.endDate) }}</span>
            </div>
            <div class="info-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
              <span class="info-label">请假天数:</span>
              <span class="info-value">{{ leave.days }} 天</span>
            </div>
          </div>

          <div class="reason-section">
            <div class="reason-label">请假原因:</div>
            <div class="reason-content">{{ leave.reason || '无' }}</div>
          </div>

          <div class="leave-meta">
            <span class="meta-text">申请时间: {{ formatDateTime(leave.createdAt) }}</span>
          </div>

          <div v-if="leave.reviewNote" class="review-note">
            <div class="note-label">审批意见:</div>
            <div class="note-content">{{ leave.reviewNote }}</div>
          </div>
        </div>

        <div v-if="leave.status === 'PENDING'" class="leave-actions">
          <el-button 
            type="success" 
            @click="handleApprove(leave)"
            :loading="actionLoading[leave.id]"
          >
            批准
          </el-button>
          <el-button 
            type="danger" 
            @click="handleReject(leave)"
            :loading="actionLoading[leave.id]"
          >
            拒绝
          </el-button>
        </div>
      </div>

      <div v-if="!loading && leaves.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="48" height="48">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无请假记录</p>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchLeaves"
        @current-change="fetchLeaves"
      />
    </div>

    <!-- 拒绝原因对话框 -->
    <el-dialog 
      v-model="rejectDialogVisible" 
      title="拒绝请假"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input 
            v-model="rejectReason" 
            type="textarea" 
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">
          确认拒绝
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { getAllLeaves, approveLeave, rejectLeave } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const leaves = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const filterStatus = ref('PENDING');

const rejectDialogVisible = ref(false);
const rejectReason = ref('');
const selectedLeave = ref(null);
const rejecting = ref(false);

const actionLoading = reactive({});

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

onMounted(() => {
  fetchLeaves();
});

const fetchLeaves = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };
    
    // 根据筛选状态添加status参数
    if (filterStatus.value) {
      params.status = filterStatus.value;
    }
    
    const response = await getAllLeaves(params);
    const data = response?.data || response;
    leaves.value = data?.content || [];
    total.value = data?.totalElements || 0;
  } catch (error) {
    console.error('获取请假列表失败:', error);
    ElMessage.error('获取请假列表失败');
    leaves.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const handleApprove = async (leave) => {
  try {
    await ElMessageBox.confirm(
      `确定批准"${leave.doctorName}"的请假申请吗？`,
      '审批确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    actionLoading[leave.id] = true;
    await approveLeave(leave.id);
    ElMessage.success('已批准');
    await fetchLeaves();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批准失败:', error);
      ElMessage.error('批准失败');
    }
  } finally {
    actionLoading[leave.id] = false;
  }
};

const handleReject = (leave) => {
  selectedLeave.value = leave;
  rejectReason.value = '';
  rejectDialogVisible.value = true;
};

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因');
    return;
  }

  rejecting.value = true;
  try {
    await rejectLeave(selectedLeave.value.id, rejectReason.value);
    ElMessage.success('已拒绝');
    rejectDialogVisible.value = false;
    await fetchLeaves();
  } catch (error) {
    console.error('拒绝失败:', error);
    ElMessage.error('拒绝失败');
  } finally {
    rejecting.value = false;
  }
};

const getStatusText = (status) => {
  const statusMap = {
    PENDING: '待审批',
    APPROVED: '已批准',
    REJECTED: '已拒绝',
    CANCELLED: '已撤销'
  };
  return statusMap[status] || status;
};

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    CANCELLED: 'info'
  };
  return typeMap[status] || '';
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

const formatDateTime = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>

<style scoped>
.admin-leaves {
  max-width: 1000px;
  margin: 0 auto;
}

/* ========== 页面标题 ========== */
.page-header {
  margin-bottom: 24px;
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

/* ========== 筛选栏 ========== */
.filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

/* ========== 请假列表 ========== */
.leaves-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 200px;
}

.leave-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.leave-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.leave-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #F0F0F0;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.doctor-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.doctor-detail {
  flex: 1;
}

.doctor-name {
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 4px 0;
}

.doctor-dept {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.leave-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.leave-info-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.info-item svg {
  color: #FFD300;
  flex-shrink: 0;
}

.info-label {
  color: #999;
}

.info-value {
  color: #2A2A2A;
  font-weight: 500;
}

.reason-section {
  background: #FAFAFA;
  border-radius: 8px;
  padding: 12px;
}

.reason-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.reason-content {
  font-size: 14px;
  color: #2A2A2A;
  line-height: 1.6;
}

.leave-meta {
  font-size: 13px;
  color: #999;
}

.review-note {
  background: #FFF9E5;
  border-radius: 8px;
  padding: 12px;
  border-left: 3px solid #FFD300;
}

.note-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.note-content {
  font-size: 14px;
  color: #2A2A2A;
  line-height: 1.6;
}

.leave-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #F0F0F0;
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state svg {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 14px;
  margin: 0;
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .leave-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .leave-info-row {
    flex-direction: column;
    gap: 12px;
  }
  
  .leave-actions {
    flex-direction: column;
  }
  
  .leave-actions .el-button {
    width: 100%;
  }
}
</style>

