<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人信息</h1>
      <p class="subtitle">查看和编辑您的个人资料</p>
    </div>

    <div class="content-card">
      <div class="card-header">
        <div class="avatar-section">
          <img :src="userInfo.avatarUrl || defaultAvatar" class="avatar" />
          <button class="btn-change-avatar">更换头像</button>
        </div>
      </div>

      <form @submit.prevent="handleSave" class="profile-form">
        <div class="form-section">
          <h3>基本信息</h3>
          
          <div class="form-row">
            <div class="form-group">
              <label>姓名</label>
              <input type="text" v-model="form.name" placeholder="请输入姓名" />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input type="tel" v-model="form.phone" disabled class="disabled" />
              <span class="hint">手机号不可修改</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>身份证号</label>
              <input type="text" v-model="form.idCard" placeholder="请输入身份证号" />
            </div>
            <div class="form-group">
              <label>性别</label>
              <select v-model="form.gender">
                <option value="">请选择</option>
                <option value="MALE">男</option>
                <option value="FEMALE">女</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>出生日期</label>
              <input type="date" v-model="form.birthDate" />
            </div>
            <div class="form-group">
              <label>紧急联系人</label>
              <input type="text" v-model="form.emergencyContact" placeholder="紧急联系人姓名" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>紧急联系电话</label>
              <input type="tel" v-model="form.emergencyPhone" placeholder="紧急联系人电话" />
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>健康信息</h3>
          
          <div class="form-row">
            <div class="form-group full-width">
              <label>病史</label>
              <textarea v-model="form.medicalHistory" placeholder="请描述您的既往病史（如有）" rows="3"></textarea>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group full-width">
              <label>过敏史</label>
              <textarea v-model="form.allergyHistory" placeholder="请描述您的过敏史（如有）" rows="3"></textarea>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="resetForm">取消修改</button>
          <button type="submit" class="btn-save" :disabled="saving">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const userStore = useUserStore();
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const userInfo = ref({});
const saving = ref(false);

const form = ref({
  name: '',
  phone: '',
  idCard: '',
  gender: '',
  birthDate: '',
  emergencyContact: '',
  emergencyPhone: '',
  medicalHistory: '',
  allergyHistory: ''
});

const originalForm = ref({});

onMounted(async () => {
  // 从 store 获取基本信息
  if (userStore.userInfo) {
    form.value.phone = userStore.userInfo.phone || '';
  }
  
  // 获取患者详细信息
  try {
    const data = await request.get('/api/patients/profile');
    if (data) {
      form.value = {
        name: data.name || '',
        phone: userStore.userInfo?.phone || '',
        idCard: data.idCard || '',
        gender: data.gender || '',
        birthDate: data.birthDate || '',
        emergencyContact: data.emergencyContact || '',
        emergencyPhone: data.emergencyPhone || '',
        medicalHistory: data.medicalHistory || '',
        allergyHistory: data.allergyHistory || ''
      };
      originalForm.value = { ...form.value };
    }
  } catch (e) {
    console.error('获取个人信息失败:', e);
  }
});

const resetForm = () => {
  form.value = { ...originalForm.value };
};

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入姓名');
    return;
  }
  
  saving.value = true;
  try {
    await request.put('/api/patients/profile', {
      name: form.value.name,
      idCard: form.value.idCard,
      gender: form.value.gender,
      birthDate: form.value.birthDate,
      emergencyContact: form.value.emergencyContact,
      emergencyPhone: form.value.emergencyPhone,
      medicalHistory: form.value.medicalHistory,
      allergyHistory: form.value.allergyHistory
    });
    ElMessage.success('保存成功');
    originalForm.value = { ...form.value };
  } catch (e) {
    ElMessage.error('保存失败，请稍后重试');
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
.profile-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header {
  margin-bottom: 32px;
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

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.card-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #FFD300;
}

.btn-change-avatar {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.btn-change-avatar:hover {
  background: #f3f4f6;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.form-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2a2a2a;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  flex: 1 1 100%;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  color: #2a2a2a;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #FFD300;
  box-shadow: 0 0 0 3px rgba(255, 211, 0, 0.2);
}

.form-group input.disabled {
  background: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
}

.hint {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel {
  padding: 12px 24px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #f3f4f6;
}

.btn-save {
  padding: 12px 32px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.btn-save:hover {
  background: #f4ca00;
}

.btn-save:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .form-row {
    flex-direction: column;
    gap: 20px;
  }
}
</style>
