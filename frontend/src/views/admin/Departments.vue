<template>
  <div class="admin-departments">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">科室管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          添加科室
        </el-button>
      </div>
    </div>

    <!-- 科室列表 -->
    <div class="departments-grid" v-loading="loading">
      <div 
        v-for="dept in departments" 
        :key="dept.id"
        class="dept-card"
        :class="{ disabled: !dept.enabled }"
      >
        <div class="dept-header">
          <div class="dept-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
          </div>
          <el-tag :type="dept.enabled ? 'success' : 'info'" size="small">
            {{ dept.enabled ? '正常' : '已禁用' }}
          </el-tag>
        </div>

        <div class="dept-info">
          <h3 class="dept-name">{{ dept.name }}</h3>
          <div class="dept-category">{{ dept.category }}</div>
          <p class="dept-description">{{ dept.description || '暂无描述' }}</p>
          
          <div class="dept-details">
            <div class="detail-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <span>{{ dept.location || '未设置' }}</span>
            </div>
            <div class="detail-item" v-if="dept.phone">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z"></path>
              </svg>
              <span>{{ dept.phone }}</span>
            </div>
          </div>
        </div>

        <div class="dept-actions">
          <el-button type="primary" size="small" @click="handleEdit(dept)">
            编辑
          </el-button>
          <el-button 
            v-if="dept.enabled" 
            type="warning" 
            size="small" 
            @click="handleDisable(dept)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            type="success" 
            size="small" 
            @click="handleEnable(dept)"
          >
            启用
          </el-button>
        </div>
      </div>

      <div v-if="!loading && departments.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="48" height="48">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无科室数据</p>
      </div>
    </div>

    <!-- 添加/编辑科室对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑科室' : '添加科室'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef"
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择科室分类" style="width: 100%;">
            <el-option label="内科系" value="内科系" />
            <el-option label="外科系" value="外科系" />
            <el-option label="妇产科系" value="妇产科系" />
            <el-option label="儿科系" value="儿科系" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入科室描述"
          />
        </el-form-item>
        <el-form-item label="科室位置" prop="location">
          <el-input v-model="formData.location" placeholder="例如: 门诊大楼三层A区" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="1" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllDepartments, createDepartment, updateDepartment, deleteDepartment, enableDepartment } from '@/api/admin';
import { ElMessage, ElMessageBox } from 'element-plus';

const loading = ref(false);
const departments = ref([]);

const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref(null);
const submitting = ref(false);

const formData = ref({
  name: '',
  category: '',
  description: '',
  location: '',
  phone: '',
  sortOrder: 1
});

const formRules = {
  name: [
    { required: true, message: '请输入科室名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择科室分类', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入科室位置', trigger: 'blur' }
  ]
};

onMounted(() => {
  fetchDepartments();
});

const fetchDepartments = async () => {
  loading.value = true;
  try {
    const response = await getAllDepartments();
    departments.value = response?.data || response || [];
  } catch (error) {
    console.error('获取科室列表失败:', error);
    ElMessage.error('获取科室列表失败');
  } finally {
    loading.value = false;
  }
};

const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    name: '',
    category: '',
    description: '',
    location: '',
    phone: '',
    sortOrder: 1
  };
  dialogVisible.value = true;
};

const handleEdit = (dept) => {
  isEdit.value = true;
  formData.value = {
    id: dept.id,
    name: dept.name,
    category: dept.category,
    description: dept.description,
    location: dept.location,
    phone: dept.phone,
    sortOrder: dept.sortOrder
  };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    submitting.value = true;
    try {
      if (isEdit.value) {
        await updateDepartment(formData.value.id, formData.value);
        ElMessage.success('更新成功');
      } else {
        await createDepartment(formData.value);
        ElMessage.success('创建成功');
      }
      dialogVisible.value = false;
      await fetchDepartments();
    } catch (error) {
      console.error('提交失败:', error);
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败');
    } finally {
      submitting.value = false;
    }
  });
};

const handleDisable = async (dept) => {
  try {
    await ElMessageBox.confirm(
      `确定要禁用科室"${dept.name}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    await deleteDepartment(dept.id);
    ElMessage.success('禁用成功');
    await fetchDepartments();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败:', error);
      ElMessage.error('禁用失败');
    }
  }
};

const handleEnable = async (dept) => {
  try {
    await enableDepartment(dept.id);
    ElMessage.success('启用成功');
    await fetchDepartments();
  } catch (error) {
    console.error('启用失败:', error);
    ElMessage.error('启用失败');
  }
};
</script>

<style scoped>
.admin-departments {
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

.header-actions .el-button svg {
  margin-right: 4px;
}

/* ========== 科室卡片网格 ========== */
.departments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.dept-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
}

.dept-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.dept-card.disabled {
  opacity: 0.6;
}

.dept-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.dept-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #FFF9E5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFD300;
}

.dept-info {
  flex: 1;
  margin-bottom: 16px;
}

.dept-name {
  font-size: 18px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 4px 0;
}

.dept-category {
  font-size: 13px;
  color: #666;
  margin-bottom: 12px;
}

.dept-description {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.dept-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.detail-item svg {
  color: #999;
  flex-shrink: 0;
}

.dept-actions {
  display: flex;
  gap: 8px;
}

.dept-actions .el-button {
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
  
  .departments-grid {
    grid-template-columns: 1fr;
  }
}
</style>

