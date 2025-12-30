<template>
  <div class="admin-doctors">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">医生审批</h1>
      <p class="page-subtitle">审批医生注册申请并管理医生资料</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-radio-group v-model="filterStatus" @change="handleFilterChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待审批</el-radio-button>
        <el-radio-button label="ACTIVE">已通过</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 医生列表 -->
    <div class="doctors-grid" v-loading="loading">
      <div 
        v-for="doctor in filteredDoctors" 
        :key="doctor.id"
        class="doctor-card"
      >
        <div class="doctor-avatar-section">
          <img :src="doctor.avatarUrl || defaultAvatar" class="doctor-avatar" />
          <el-tag :type="getStatusType(doctor.status)" size="small" class="status-tag">
            {{ getStatusText(doctor.status) }}
          </el-tag>
        </div>

        <div class="doctor-info">
          <h3 class="doctor-name">{{ doctor.name }}</h3>
          <div class="doctor-meta">
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              {{ getTitleText(doctor.title) }}
            </span>
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
              </svg>
              {{ doctor.departmentName || '未分配科室' }}
            </span>
          </div>
          <div class="doctor-detail">
            <span class="detail-label">工号:</span>
            <span class="detail-value">{{ doctor.employeeId }}</span>
          </div>
          <div class="doctor-detail">
            <span class="detail-label">执业证号:</span>
            <span class="detail-value">{{ doctor.licenseNumber || '-' }}</span>
          </div>
          <div class="doctor-detail">
            <span class="detail-label">专长:</span>
            <span class="detail-value">{{ doctor.specialty || '-' }}</span>
          </div>
        </div>

        <div class="doctor-actions">
          <el-button 
            v-if="doctor.status === 'PENDING'" 
            type="success" 
            @click="handleApprove(doctor)"
            :loading="actionLoading[doctor.id]"
          >
            通过审批
          </el-button>
          <el-button 
            type="primary" 
            @click="handleViewDetail(doctor)"
          >
            查看详情
          </el-button>
        </div>
      </div>

      <div v-if="!loading && filteredDoctors.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="48" height="48">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无医生数据</p>
      </div>
    </div>

    <!-- 医生详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="医生详情" 
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedDoctor" class="doctor-detail-panel">
        <div class="detail-header">
          <img :src="selectedDoctor.avatarUrl || defaultAvatar" class="detail-avatar" />
          <div class="detail-header-info">
            <h3>{{ selectedDoctor.name }}</h3>
            <p>{{ getTitleText(selectedDoctor.title) }} · {{ selectedDoctor.departmentName }}</p>
            <el-tag :type="getStatusType(selectedDoctor.status)" size="small">
              {{ getStatusText(selectedDoctor.status) }}
            </el-tag>
          </div>
        </div>

        <div class="detail-content">
          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="item-label">工号:</span>
                <span class="item-value">{{ selectedDoctor.employeeId }}</span>
              </div>
              <div class="detail-item">
                <span class="item-label">执业证号:</span>
                <span class="item-value">{{ selectedDoctor.licenseNumber || '-' }}</span>
              </div>
              <div class="detail-item">
                <span class="item-label">专长领域:</span>
                <span class="item-value">{{ selectedDoctor.specialty || '-' }}</span>
              </div>
              <div class="detail-item">
                <span class="item-label">手机号:</span>
                <span class="item-value">{{ selectedDoctor.phone || '-' }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section" v-if="selectedDoctor.introduction">
            <h4 class="section-title">个人简介</h4>
            <p class="introduction-text">{{ selectedDoctor.introduction }}</p>
          </div>

          <div class="detail-section" v-if="selectedDoctor.education">
            <h4 class="section-title">教育背景</h4>
            <p class="introduction-text">{{ selectedDoctor.education }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="selectedDoctor && selectedDoctor.status === 'PENDING'" 
          type="success" 
          @click="handleApprove(selectedDoctor)"
          :loading="actionLoading[selectedDoctor.id]"
        >
          通过审批
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { listAllDoctors } from '@/api/doctor';
import { approveDoctor } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const doctors = ref([]);
const filterStatus = ref('');

const detailDialogVisible = ref(false);
const selectedDoctor = ref(null);

const actionLoading = reactive({});

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

// 过滤后的医生列表
const filteredDoctors = computed(() => {
  if (!filterStatus.value) return doctors.value;
  return doctors.value.filter(d => d.status === filterStatus.value);
});

onMounted(() => {
  fetchDoctors();
});

const fetchDoctors = async () => {
  loading.value = true;
  try {
    const data = await listAllDoctors({ page: 0, size: 1000 });
    // 后端返回的是分页数据，需要取 content 字段
    doctors.value = data.content || [];
  } catch (error) {
    console.error('获取医生列表失败:', error);
    ElMessage.error('获取医生列表失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  // 筛选状态改变
};

const handleApprove = async (doctor) => {
  try {
    await ElMessageBox.confirm(
      `确定通过医生"${doctor.name}"的审批吗？`,
      '审批确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    actionLoading[doctor.id] = true;
    await approveDoctor(doctor.id);
    ElMessage.success('审批成功');
    detailDialogVisible.value = false;
    await fetchDoctors();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审批失败:', error);
      ElMessage.error('审批失败');
    }
  } finally {
    actionLoading[doctor.id] = false;
  }
};

const handleViewDetail = (doctor) => {
  selectedDoctor.value = doctor;
  detailDialogVisible.value = true;
};

const getTitleText = (title) => {
  const titleMap = {
    RESIDENT: '住院医师',
    ATTENDING: '主治医师',
    ASSOCIATE_CHIEF: '副主任医师',
    CHIEF: '主任医师'
  };
  return titleMap[title] || title;
};

const getStatusText = (status) => {
  const statusMap = {
    PENDING: '待审批',
    ACTIVE: '正常',
    LOCKED: '已锁定'
  };
  return statusMap[status] || status;
};

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    ACTIVE: 'success',
    LOCKED: 'danger'
  };
  return typeMap[status] || '';
};
</script>

<style scoped>
.admin-doctors {
  max-width: 1400px;
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

/* ========== 医生卡片网格 ========== */
.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.doctor-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
}

.doctor-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.doctor-avatar-section {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.doctor-avatar {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  object-fit: cover;
}

.status-tag {
  margin-left: auto;
}

.doctor-info {
  flex: 1;
  margin-bottom: 16px;
}

.doctor-name {
  font-size: 18px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 12px 0;
}

.doctor-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.meta-item svg {
  color: #999;
}

.doctor-detail {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.detail-label {
  color: #999;
  min-width: 80px;
}

.detail-value {
  color: #2A2A2A;
  flex: 1;
}

.doctor-actions {
  display: flex;
  gap: 8px;
}

.doctor-actions .el-button {
  flex: 1;
}

/* ========== 空状态 ========== */
.empty-state {
  grid-column: 1 / -1;
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

/* ========== 医生详情面板 ========== */
.doctor-detail-panel {
  padding: 12px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 24px;
  border-bottom: 1px solid #F0F0F0;
  margin-bottom: 24px;
}

.detail-avatar {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
}

.detail-header-info h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 8px 0;
}

.detail-header-info p {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px 0;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 16px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-label {
  font-size: 13px;
  color: #999;
}

.item-value {
  font-size: 14px;
  color: #2A2A2A;
}

.introduction-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .doctors-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

