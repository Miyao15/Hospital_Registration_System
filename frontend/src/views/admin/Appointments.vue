<template>
  <div class="admin-appointments">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">预约管理</h1>
      <p class="page-subtitle">查看和管理所有患者的预约记录</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <el-select v-model="filterStatus" placeholder="所有状态" @change="handleFilterChange" clearable>
          <el-option label="所有状态" value="" />
          <el-option label="待就诊" value="PENDING" />
          <el-option label="已签到" value="CHECKED_IN" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
          <el-option label="已过期" value="EXPIRED" />
        </el-select>

        <el-date-picker
          v-model="filterDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleFilterChange"
          clearable
        />
      </div>
    </div>

    <!-- 预约列表 -->
    <div class="appointments-table-card">
      <el-table 
        :data="appointments" 
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#FAFAFA', color: '#2A2A2A', fontWeight: '600' }"
      >
        <el-table-column prop="appointmentNo" label="预约号" width="150" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="patientPhone" label="患者电话" width="140">
          <template #default="{ row }">
            {{ maskPhone(row.patientPhone) }}
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column prop="departmentName" label="科室" width="120" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.appointmentDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="periodName" label="时段" width="100" />
        <el-table-column prop="symptomDesc" label="病情描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING' || row.status === 'CHECKED_IN'"
              type="warning" 
              size="small" 
              @click="handleEdit(row)"
            >
              修改
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING' || row.status === 'CHECKED_IN'"
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAppointments"
          @current-change="fetchAppointments"
        />
      </div>
    </div>

    <!-- 预约详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="预约详情" 
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedAppointment" class="appointment-detail">
        <div class="detail-row">
          <span class="detail-label">预约号:</span>
          <span class="detail-value">{{ selectedAppointment.appointmentNo }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">患者姓名:</span>
          <span class="detail-value">{{ selectedAppointment.patientName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">患者电话:</span>
          <span class="detail-value">{{ selectedAppointment.patientPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">医生:</span>
          <span class="detail-value">{{ selectedAppointment.doctorName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">科室:</span>
          <span class="detail-value">{{ selectedAppointment.departmentName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预约日期:</span>
          <span class="detail-value">{{ formatDate(selectedAppointment.appointmentDate) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">时段:</span>
          <span class="detail-value">{{ selectedAppointment.periodName }} {{ selectedAppointment.timeRange }}</span>
        </div>
        <div class="detail-row" v-if="selectedAppointment.symptomDesc">
          <span class="detail-label">病情描述:</span>
          <span class="detail-value">{{ selectedAppointment.symptomDesc }}</span>
        </div>
        <div class="detail-row" v-if="selectedAppointment.medicalItemName">
          <span class="detail-label">检查项目:</span>
          <span class="detail-value">{{ selectedAppointment.medicalItemName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态:</span>
          <el-tag :type="getStatusType(selectedAppointment.status)" size="small">
            {{ getStatusText(selectedAppointment.status) }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">创建时间:</span>
          <span class="detail-value">{{ formatDateTime(selectedAppointment.createdAt) }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 修改预约对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="修改预约信息" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="患者姓名">
          <el-input v-model="editForm.patientName" placeholder="请输入患者姓名" />
        </el-form-item>
        <el-form-item label="患者电话">
          <el-input v-model="editForm.patientPhone" placeholder="请输入患者电话" />
        </el-form-item>
        <el-form-item label="病情描述">
          <el-input
            v-model="editForm.symptomDesc"
            type="textarea"
            :rows="4"
            placeholder="请输入病情描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllAppointments, adminCancelAppointment, adminUpdateAppointment } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const appointments = ref([]);
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);

const filterStatus = ref('');
const filterDate = ref('');

const detailDialogVisible = ref(false);
const selectedAppointment = ref(null);
const editDialogVisible = ref(false);
const editingAppointment = ref(null);
const editForm = ref({
  symptomDesc: '',
  patientName: '',
  patientPhone: ''
});

onMounted(() => {
  fetchAppointments();
});

const fetchAppointments = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };
    if (filterStatus.value) {
      params.status = filterStatus.value;
    }
    if (filterDate.value) {
      params.appointmentDate = filterDate.value;
    }
    
    const data = await getAllAppointments(params);
    appointments.value = data?.content || [];
    total.value = data?.totalElements || 0;
  } catch (error) {
    console.error('获取预约列表失败:', error);
    ElMessage.error('获取预约列表失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1;
  fetchAppointments();
};

const handleViewDetail = (appointment) => {
  selectedAppointment.value = appointment;
  detailDialogVisible.value = true;
};

const handleEdit = (appointment) => {
  editingAppointment.value = appointment;
  editForm.value = {
    symptomDesc: appointment.symptomDesc || '',
    patientName: appointment.patientName || '',
    patientPhone: appointment.patientPhone || ''
  };
  editDialogVisible.value = true;
};

const handleSaveEdit = async () => {
  if (!editingAppointment.value) return;
  
  try {
    await adminUpdateAppointment(editingAppointment.value.id, editForm.value);
    ElMessage.success('预约信息更新成功');
    editDialogVisible.value = false;
    await fetchAppointments();
  } catch (error) {
    console.error('更新预约信息失败:', error);
    ElMessage.error(error.response?.data?.message || '更新预约信息失败');
  }
};

const handleDelete = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除预约"${appointment.appointmentNo}"吗？此操作将取消该预约。`,
      '删除预约',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await adminCancelAppointment(appointment.id, '管理员删除');
    ElMessage.success('预约已删除');
    await fetchAppointments();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除预约失败:', error);
      ElMessage.error(error.response?.data?.message || '删除预约失败');
    }
  }
};

const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

const getStatusText = (status) => {
  const statusMap = {
    PENDING: '待就诊',
    CHECKED_IN: '已签到',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    EXPIRED: '已过期'
  };
  return statusMap[status] || status;
};

const getStatusType = (status) => {
  const typeMap = {
    PENDING: 'warning',
    CHECKED_IN: 'info',
    COMPLETED: 'success',
    CANCELLED: 'info',
    EXPIRED: 'danger'
  };
  return typeMap[status] || '';
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
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
.admin-appointments {
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

.filter-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-group .el-select {
  width: 160px;
}

/* ========== 表格卡片 ========== */
.appointments-table-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* ========== 预约详情 ========== */
.appointment-detail {
  padding: 12px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #F0F0F0;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  width: 120px;
  font-weight: 600;
  color: #2A2A2A;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #666;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
  
  .filter-group .el-select {
    width: 100%;
  }
  
  .appointments-table-card {
    padding: 16px;
  }
}
</style>

