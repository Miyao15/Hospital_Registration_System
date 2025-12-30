<template>
  <div class="admin-schedules">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">排班管理</h1>
      <el-button type="primary" @click="dialogVisible = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        批量创建排班
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="selectedDepartment" placeholder="选择科室" clearable @change="fetchDoctors">
        <el-option 
          v-for="dept in departments" 
          :key="dept.id"
          :label="dept.name" 
          :value="dept.id" 
        />
      </el-select>
      <el-select v-model="selectedDoctor" placeholder="选择医生" clearable>
        <el-option 
          v-for="doctor in doctors" 
          :key="doctor.id"
          :label="doctor.name" 
          :value="doctor.id" 
        />
      </el-select>
    </div>

    <!-- 提示信息 -->
    <el-alert
      title="排班说明"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
    >
      <p style="margin: 0; font-size: 13px; line-height: 1.6;">
        • 可以批量为医生创建多日排班<br>
        • 支持设置上午、下午、晚上时段<br>
        • 每个时段可以单独设置号源数量<br>
        • 医生请假期间无法创建排班
      </p>
    </el-alert>

    <!-- 排班列表 -->
    <div class="schedule-list" v-loading="loading">
      <el-table :data="schedules" stripe style="width: 100%">
        <el-table-column prop="scheduleDate" label="日期" width="120" />
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column prop="departmentName" label="科室" width="120" />
        <el-table-column label="时段" min-width="200">
          <template #default="{ row }">
            <div v-if="row.timeSlots && row.timeSlots.length > 0">
              <el-tag 
                v-for="slot in row.timeSlots" 
                :key="slot.id"
                :type="slot.available ? 'success' : 'info'"
                style="margin-right: 8px; margin-bottom: 4px;"
              >
                {{ slot.periodName }}: {{ slot.timeRange }} ({{ slot.remainingSlots }}/{{ slot.totalSlots }})
              </el-tag>
            </div>
            <span v-else style="color: #999;">无排班</span>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentCount" label="已预约" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isWorking ? 'success' : 'info'">
              {{ row.isWorking ? '正常' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="row.isWorking"
              type="danger" 
              size="small" 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>

      <div v-if="!loading && schedules.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="48" height="48">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无排班数据</p>
      </div>
    </div>

    <!-- 创建排班对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="批量创建排班"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef"
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="选择医生" prop="doctorId">
          <el-select v-model="formData.doctorId" placeholder="请选择医生" style="width: 100%;">
            <el-option 
              v-for="doctor in allDoctors" 
              :key="doctor.id"
              :label="`${doctor.name} - ${doctor.departmentName}`" 
              :value="doctor.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="formData.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="工作日">
          <el-checkbox-group v-model="formData.weekdays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="时段设置">
          <div class="time-slot-config">
            <el-card shadow="never" class="slot-card">
              <div class="slot-header">
                <el-checkbox v-model="formData.morningEnabled">上午</el-checkbox>
                <span class="slot-time">09:00 - 12:00</span>
              </div>
              <div v-if="formData.morningEnabled" class="slot-content">
                <el-input-number 
                  v-model="formData.morningSlots" 
                  :min="1" 
                  :max="100"
                  placeholder="号源数量"
                />
              </div>
            </el-card>

            <el-card shadow="never" class="slot-card">
              <div class="slot-header">
                <el-checkbox v-model="formData.afternoonEnabled">下午</el-checkbox>
                <span class="slot-time">14:00 - 17:00</span>
              </div>
              <div v-if="formData.afternoonEnabled" class="slot-content">
                <el-input-number 
                  v-model="formData.afternoonSlots" 
                  :min="1" 
                  :max="100"
                  placeholder="号源数量"
                />
              </div>
            </el-card>

            <el-card shadow="never" class="slot-card">
              <div class="slot-header">
                <el-checkbox v-model="formData.eveningEnabled">晚上</el-checkbox>
                <span class="slot-time">18:00 - 21:00</span>
              </div>
              <div v-if="formData.eveningEnabled" class="slot-content">
                <el-input-number 
                  v-model="formData.eveningSlots" 
                  :min="1" 
                  :max="100"
                  placeholder="号源数量"
                />
              </div>
            </el-card>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          创建排班
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { listAllDepartments } from '@/api/department';
import { listAllDoctors } from '@/api/doctor';
import { getSchedules, batchCreateSchedules, cancelSchedule } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const departments = ref([]);
const allDoctors = ref([]);
const doctors = ref([]);

const selectedDepartment = ref('');
const selectedDoctor = ref('');

const dialogVisible = ref(false);
const formRef = ref(null);
const submitting = ref(false);

// 排班列表相关
const loading = ref(false);
const schedules = ref([]);
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);
const totalPages = ref(0);

const formData = ref({
  doctorId: '',
  dateRange: [],
  weekdays: ['1', '2', '3', '4', '5'], // 默认周一至周五
  morningEnabled: true,
  morningSlots: 20,
  afternoonEnabled: true,
  afternoonSlots: 20,
  eveningEnabled: false,
  eveningSlots: 15
});

const formRules = {
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'change' }
  ],
  dateRange: [
    { required: true, message: '请选择日期范围', trigger: 'change' }
  ]
};

onMounted(() => {
  fetchDepartments();
  fetchAllDoctors();
  fetchSchedules();
});

// 监听筛选条件变化
watch([selectedDepartment, selectedDoctor], () => {
  currentPage.value = 1;
  fetchSchedules();
});

const fetchDepartments = async () => {
  try {
    const data = await listAllDepartments();
    departments.value = data || [];
  } catch (error) {
    console.error('获取科室列表失败:', error);
  }
};

const fetchAllDoctors = async () => {
  try {
    const data = await listAllDoctors({ page: 0, size: 1000 });
    const doctorsList = data.content || [];
    allDoctors.value = doctorsList.filter(d => d.status === 'ACTIVE') || [];
    doctors.value = allDoctors.value;
  } catch (error) {
    console.error('获取医生列表失败:', error);
  }
};

const fetchDoctors = () => {
  if (selectedDepartment.value) {
    doctors.value = allDoctors.value.filter(d => d.departmentId === selectedDepartment.value);
  } else {
    doctors.value = allDoctors.value;
  }
  selectedDoctor.value = '';
};

const disabledDate = (date) => {
  // 禁止选择今天之前的日期
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    // 检查是否至少选择了一个时段
    if (!formData.value.morningEnabled && !formData.value.afternoonEnabled && !formData.value.eveningEnabled) {
      ElMessage.warning('请至少选择一个时段');
      return;
    }

    // 检查是否选择了工作日
    if (formData.value.weekdays.length === 0) {
      ElMessage.warning('请至少选择一个工作日');
      return;
    }

    submitting.value = true;
    try {
      // 构建请求数据
      const timeSlots = [];
      if (formData.value.morningEnabled) {
        timeSlots.push({
          period: 'MORNING',
          startTime: '09:00:00',
          endTime: '12:00:00',
          totalSlots: formData.value.morningSlots
        });
      }
      if (formData.value.afternoonEnabled) {
        timeSlots.push({
          period: 'AFTERNOON',
          startTime: '14:00:00',
          endTime: '17:00:00',
          totalSlots: formData.value.afternoonSlots
        });
      }
      if (formData.value.eveningEnabled) {
        timeSlots.push({
          period: 'EVENING',
          startTime: '18:00:00',
          endTime: '21:00:00',
          totalSlots: formData.value.eveningSlots
        });
      }

      const requestData = {
        doctorId: formData.value.doctorId,
        startDate: formatDate(formData.value.dateRange[0]),
        endDate: formatDate(formData.value.dateRange[1]),
        weekdays: formData.value.weekdays.map(Number),
        timeSlots: timeSlots
      };

      await batchCreateSchedules(requestData);
      ElMessage.success('排班创建成功');
      dialogVisible.value = false;
      await fetchSchedules();
    } catch (error) {
      console.error('创建排班失败:', error);
      ElMessage.error(error.message || '创建排班失败');
    } finally {
      submitting.value = false;
    }
  });
};

const formatDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const fetchSchedules = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    };
    if (selectedDepartment.value) {
      params.departmentId = selectedDepartment.value;
    }
    if (selectedDoctor.value) {
      params.doctorId = selectedDoctor.value;
    }
    
    const response = await getSchedules(params);
    const data = response?.data || response;
    schedules.value = data?.content || [];
    total.value = data?.totalElements || 0;
    totalPages.value = data?.totalPages || 0;
  } catch (error) {
    console.error('获取排班列表失败:', error);
    ElMessage.error('获取排班列表失败');
    schedules.value = [];
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchSchedules();
};

const handleCancel = async (schedule) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消${schedule.doctorName}在${schedule.scheduleDate}的排班吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await cancelSchedule(schedule.doctorId, schedule.scheduleDate);
    ElMessage.success('排班已取消');
    await fetchSchedules();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消排班失败:', error);
      ElMessage.error('取消排班失败');
    }
  }
};
</script>

<style scoped>
.admin-schedules {
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 页面标题 ========== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0;
}

.page-header .el-button svg {
  margin-right: 4px;
}

/* ========== 筛选栏 ========== */
.filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  display: flex;
  gap: 12px;
}

.filter-bar .el-select {
  width: 200px;
}

/* ========== 时段配置 ========== */
.time-slot-config {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.slot-card {
  border: 1px solid #E8E8E8;
  border-radius: 8px;
}

.slot-card :deep(.el-card__body) {
  padding: 16px;
}

.slot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.slot-time {
  font-size: 13px;
  color: #666;
}

.slot-content {
  padding-left: 24px;
}

/* ========== 排班列表 ========== */
.schedule-list {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

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

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .filter-bar {
    flex-direction: column;
  }
  
  .filter-bar .el-select {
    width: 100%;
  }
}
</style>

