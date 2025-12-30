<template>
  <div class="admin-users">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-subtitle">管理系统中的所有用户账户</p>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <el-select v-model="filterRole" placeholder="所有角色" @change="handleFilterChange" clearable>
          <el-option label="所有角色" value="" />
          <el-option label="患者" value="PATIENT" />
          <el-option label="医生" value="DOCTOR" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>

        <el-select v-model="filterStatus" placeholder="所有状态" @change="handleFilterChange" clearable>
          <el-option label="所有状态" value="" />
          <el-option label="正常" value="ACTIVE" />
          <el-option label="待审批" value="PENDING" />
          <el-option label="已锁定" value="LOCKED" />
        </el-select>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="users-table-card">
      <el-table 
        :data="filteredUsers" 
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#FAFAFA', color: '#2A2A2A', fontWeight: '600' }"
      >
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="realName" label="姓名" width="120">
          <template #default="{ row }">
            {{ row.realName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录" width="180">
          <template #default="{ row }">
            {{ row.lastLoginAt ? formatDate(row.lastLoginAt) : '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button 
                v-if="row.status === 'ACTIVE'" 
                type="warning" 
                size="small" 
                @click="handleToggleStatus(row, 'LOCKED')"
                :loading="actionLoading[row.id]"
              >
                锁定
              </el-button>
              <el-button 
                v-if="row.status === 'LOCKED'" 
                type="success" 
                size="small" 
                @click="handleToggleStatus(row, 'ACTIVE')"
                :loading="actionLoading[row.id]"
              >
                解锁
              </el-button>
              <el-button 
                type="primary" 
                size="small" 
                @click="handleViewDetail(row)"
              >
                详情
              </el-button>
            </div>
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
          @size-change="fetchUsers"
          @current-change="fetchUsers"
        />
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="用户详情" 
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedUser" class="user-detail">
        <div class="detail-row">
          <span class="detail-label">用户ID:</span>
          <span class="detail-value">{{ selectedUser.id }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">手机号:</span>
          <span class="detail-value">{{ selectedUser.phone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">姓名:</span>
          <span class="detail-value">{{ selectedUser.realName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">角色:</span>
          <span class="detail-value">{{ getRoleText(selectedUser.role) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态:</span>
          <el-tag :type="getStatusType(selectedUser.status)" size="small">
            {{ getStatusText(selectedUser.status) }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">注册时间:</span>
          <span class="detail-value">{{ formatDate(selectedUser.createdAt) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">最后登录:</span>
          <span class="detail-value">{{ selectedUser.lastLoginAt ? formatDate(selectedUser.lastLoginAt) : '从未登录' }}</span>
        </div>
        <div class="detail-row" v-if="selectedUser.role === 'PATIENT'">
          <el-button type="primary" @click="handleEditPatient(selectedUser)">修改患者信息</el-button>
        </div>
        <div class="detail-row">
          <el-button type="warning" @click="handleChangePassword(selectedUser)">修改密码</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 修改患者信息对话框 -->
    <el-dialog 
      v-model="editPatientDialogVisible" 
      title="修改患者信息" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="patientForm" label-width="120px">
        <el-form-item label="姓名" required>
          <el-input v-model="patientForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="patientForm.gender" placeholder="请选择性别" style="width: 100%;">
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="patientForm.birthDate"
            type="date"
            placeholder="选择出生日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="病史">
          <el-input
            v-model="patientForm.medicalHistory"
            type="textarea"
            :rows="3"
            placeholder="请输入病史"
          />
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input
            v-model="patientForm.allergyHistory"
            type="textarea"
            :rows="3"
            placeholder="请输入过敏史"
          />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="patientForm.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话">
          <el-input v-model="patientForm.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editPatientDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePatient">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="changePasswordDialogVisible" 
      title="修改用户密码" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" label-width="120px">
        <el-form-item label="用户">
          <span>{{ changingPasswordUser?.realName || changingPasswordUser?.phone }}</span>
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { listUsers, updateUserStatus, updatePatientInfo, adminChangeUserPassword } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const users = ref([]);
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);

const filterRole = ref('');
const filterStatus = ref('');

const detailDialogVisible = ref(false);
const selectedUser = ref(null);
const editPatientDialogVisible = ref(false);
const editingPatient = ref(null);
const patientForm = ref({
  name: '',
  gender: '',
  birthDate: '',
  medicalHistory: '',
  allergyHistory: '',
  emergencyContact: '',
  emergencyPhone: ''
});

const changePasswordDialogVisible = ref(false);
const changingPasswordUser = ref(null);
const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
});

const actionLoading = reactive({});

// 过滤后的用户列表
const filteredUsers = computed(() => {
  let result = users.value;
  
  if (filterRole.value) {
    result = result.filter(u => u.role === filterRole.value);
  }
  
  if (filterStatus.value) {
    result = result.filter(u => u.status === filterStatus.value);
  }
  
  return result;
});

onMounted(() => {
  fetchUsers();
});

const fetchUsers = async () => {
  loading.value = true;
  try {
    const data = await listUsers({
      page: currentPage.value - 1,
      size: pageSize.value
    });
    users.value = data.content || [];
    total.value = data.totalElements || 0;
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

const handleFilterChange = () => {
  currentPage.value = 1;
};

const handleToggleStatus = async (user, newStatus) => {
  const action = newStatus === 'LOCKED' ? '锁定' : '解锁';
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户"${getRealName(user) || user.phone}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    actionLoading[user.id] = true;
    await updateUserStatus(user.id, newStatus);
    ElMessage.success(`${action}成功`);
    await fetchUsers();
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}用户失败:`, error);
      ElMessage.error(`${action}失败`);
    }
  } finally {
    actionLoading[user.id] = false;
  }
};

const handleViewDetail = (user) => {
  selectedUser.value = user;
  detailDialogVisible.value = true;
};

const handleEditPatient = async (user) => {
  if (user.role !== 'PATIENT') {
    ElMessage.warning('只能修改患者信息');
    return;
  }
  
  // 这里可以调用接口获取患者详细信息，暂时使用基本信息
  editingPatient.value = user;
  patientForm.value = {
    name: user.realName || '',
    gender: '',
    birthDate: '',
    medicalHistory: '',
    allergyHistory: '',
    emergencyContact: '',
    emergencyPhone: ''
  };
  editPatientDialogVisible.value = true;
  detailDialogVisible.value = false;
};

const handleSavePatient = async () => {
  if (!editingPatient.value) return;
  
  try {
    await updatePatientInfo(editingPatient.value.id, patientForm.value);
    ElMessage.success('患者信息更新成功');
    editPatientDialogVisible.value = false;
    await fetchUsers();
  } catch (error) {
    console.error('更新患者信息失败:', error);
    ElMessage.error('更新患者信息失败');
  }
};

const handleChangePassword = (user) => {
  changingPasswordUser.value = user;
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  };
  changePasswordDialogVisible.value = true;
  detailDialogVisible.value = false;
};

const handleSavePassword = async () => {
  if (!changingPasswordUser.value) return;
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  
  try {
    await adminChangeUserPassword(changingPasswordUser.value.id, passwordForm.value.newPassword);
    ElMessage.success('密码修改成功');
    changePasswordDialogVisible.value = false;
  } catch (error) {
    console.error('修改密码失败:', error);
    ElMessage.error(error.response?.data?.message || '修改密码失败');
  }
};

const getRealName = (user) => {
  return user.realName || '';
};

const getRoleText = (role) => {
  const roleMap = {
    PATIENT: '患者',
    DOCTOR: '医生',
    ADMIN: '管理员'
  };
  return roleMap[role] || role;
};

const getRoleType = (role) => {
  const typeMap = {
    PATIENT: 'info',
    DOCTOR: 'success',
    ADMIN: 'danger'
  };
  return typeMap[role] || '';
};

const getStatusText = (status) => {
  const statusMap = {
    ACTIVE: '正常',
    PENDING: '待审批',
    LOCKED: '已锁定',
    INACTIVE: '未激活'
  };
  return statusMap[status] || status;
};

const getStatusType = (status) => {
  const typeMap = {
    ACTIVE: 'success',
    PENDING: 'warning',
    LOCKED: 'danger',
    INACTIVE: 'info'
  };
  return typeMap[status] || '';
};

const formatDate = (dateString) => {
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
.admin-users {
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
.users-table-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* ========== 分页 ========== */
.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* ========== 用户详情 ========== */
.user-detail {
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
  
  .users-table-card {
    padding: 16px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
