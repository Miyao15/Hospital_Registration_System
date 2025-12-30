<template>
  <div class="profile-page">
    <!-- 个人信息卡 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar">
            <img :src="profile.avatarUrl || defaultAvatar" alt="头像" />
            <button class="avatar-edit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"></path>
                <circle cx="12" cy="13" r="4"></circle>
              </svg>
            </button>
          </div>
          <div class="profile-title">
            <h2>{{ profile.name || '医生' }}</h2>
            <div class="title-badges">
              <span class="badge title">{{ getTitleText(profile.title) }}</span>
              <span class="badge dept">{{ profile.departmentName || '科室' }}</span>
            </div>
          </div>
        </div>
        <div class="profile-stats">
          <div class="stat">
            <span class="value">{{ profile.experience || 0 }}</span>
            <span class="label">年经验</span>
          </div>
          <div class="stat">
            <span class="value">{{ profile.patientCount || 0 }}</span>
            <span class="label">服务患者</span>
          </div>
          <div class="stat">
            <span class="value">{{ profile.rating || '5.0' }}</span>
            <span class="label">评分</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 信息编辑表单 -->
    <div class="edit-section">
      <div class="section-header">
        <h3>基本信息</h3>
        <button class="btn-edit" @click="toggleEdit" v-if="!isEditing">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"></path>
            <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"></path>
          </svg>
          编辑
        </button>
      </div>

      <form @submit.prevent="saveProfile" class="profile-form">
        <div class="form-grid">
          <div class="form-group">
            <label>姓名</label>
            <input 
              type="text" 
              v-model="formData.name" 
              :disabled="!isEditing"
              placeholder="请输入姓名"
            />
          </div>
          
          <div class="form-group">
            <label>工号</label>
            <input 
              type="text" 
              v-model="formData.employeeId" 
              disabled
            />
          </div>

          <div class="form-group">
            <label>职称</label>
            <select v-model="formData.title" :disabled="!isEditing">
              <option value="RESIDENT">住院医师</option>
              <option value="ATTENDING">主治医师</option>
              <option value="ASSOCIATE_CHIEF">副主任医师</option>
              <option value="CHIEF">主任医师</option>
            </select>
          </div>

          <div class="form-group">
            <label>科室</label>
            <input 
              type="text" 
              v-model="formData.departmentName" 
              disabled
            />
          </div>

          <div class="form-group">
            <label>执业证号</label>
            <input 
              type="text" 
              v-model="formData.licenseNumber" 
              :disabled="!isEditing"
              placeholder="请输入执业证号"
            />
          </div>

          <div class="form-group">
            <label>手机号码</label>
            <input 
              type="tel" 
              v-model="formData.phone" 
              disabled
            />
          </div>
        </div>

        <div class="form-group full-width">
          <label>擅长领域</label>
          <input 
            type="text" 
            v-model="formData.specialty" 
            :disabled="!isEditing"
            placeholder="请输入擅长领域，如：高血压、冠心病的诊断与治疗"
          />
        </div>

        <div class="form-group full-width">
          <label>个人简介</label>
          <textarea 
            v-model="formData.introduction" 
            :disabled="!isEditing"
            placeholder="请输入个人简介..."
            rows="4"
          ></textarea>
        </div>

        <div class="form-group full-width">
          <label>教育背景</label>
          <textarea 
            v-model="formData.education" 
            :disabled="!isEditing"
            placeholder="请输入教育背景..."
            rows="3"
          ></textarea>
        </div>

        <div class="form-actions" v-if="isEditing">
          <button type="button" class="btn-cancel" @click="cancelEdit">取消</button>
          <button type="submit" class="btn-save" :disabled="saving">
            {{ saving ? '保存中...' : '保存更改' }}
          </button>
        </div>
      </form>
    </div>

    <!-- 出诊信息 -->
    <div class="schedule-section">
      <div class="section-header">
        <h3>出诊信息</h3>
      </div>
      <div class="schedule-info">
        <p v-if="profile.scheduleInfo">{{ profile.scheduleInfo }}</p>
        <p v-else class="empty-text">暂无出诊信息</p>
        <router-link to="/doctor/schedule" class="view-schedule">
          查看完整排班 →
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

const userStore = useUserStore();
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const isEditing = ref(false);
const saving = ref(false);
const profile = ref({});
const formData = reactive({
  name: '',
  employeeId: '',
  title: '',
  departmentName: '',
  licenseNumber: '',
  phone: '',
  specialty: '',
  introduction: '',
  education: ''
});

const originalData = ref({});

onMounted(() => {
  fetchProfile();
});

const fetchProfile = async () => {
  try {
    const data = await request.get('/api/doctor/profile');
    profile.value = data || {};
    
    // 填充表单数据
    Object.keys(formData).forEach(key => {
      formData[key] = data?.[key] || '';
    });
    formData.phone = userStore.userInfo?.phone || '';
    
    originalData.value = { ...formData };
  } catch (e) {
    console.error('获取个人资料失败:', e);
  }
};

const toggleEdit = () => {
  isEditing.value = true;
};

const cancelEdit = () => {
  // 恢复原始数据
  Object.keys(formData).forEach(key => {
    formData[key] = originalData.value[key] || '';
  });
  isEditing.value = false;
};

const saveProfile = async () => {
  saving.value = true;
  try {
    await request.put('/api/doctor/profile', {
      name: formData.name,
      title: formData.title,
      licenseNumber: formData.licenseNumber,
      specialty: formData.specialty,
      introduction: formData.introduction,
      education: formData.education
    });
    
    ElMessage.success('保存成功');
    originalData.value = { ...formData };
    isEditing.value = false;
    fetchProfile();
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败');
  } finally {
    saving.value = false;
  }
};

const getTitleText = (title) => {
  const map = {
    'RESIDENT': '住院医师',
    'ATTENDING': '主治医师',
    'ASSOCIATE_CHIEF': '副主任医师',
    'DEPUTY_CHIEF_PHYSICIAN': '副主任医师',
    'CHIEF': '主任医师'
  };
  return map[title] || title || '医师';
};
</script>

<style scoped>
.profile-page {
  max-width: 900px;
  margin: 0 auto;
}

/* 个人信息卡 */
.profile-card {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  color: #fff;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar {
  position: relative;
  width: 100px;
  height: 100px;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  object-fit: cover;
  border: 3px solid #FFD300;
}

.avatar-edit {
  position: absolute;
  bottom: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  background: #FFD300;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #1a1a2e;
}

.profile-title h2 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.title-badges {
  display: flex;
  gap: 10px;
}

.badge {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.badge.title {
  background: rgba(255,211,0,0.2);
  color: #FFD300;
}

.badge.dept {
  background: rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.9);
}

.profile-stats {
  display: flex;
  gap: 40px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat .value {
  font-size: 32px;
  font-weight: 800;
  color: #FFD300;
}

.stat .label {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
}

/* 编辑表单 */
.edit-section, .schedule-section {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.btn-edit {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit:hover {
  background: #e5e7eb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  margin-bottom: 20px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.2s;
  background: #fff;
}

.form-group input:disabled,
.form-group select:disabled,
.form-group textarea:disabled {
  background: #f8fafc;
  color: #64748b;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #FFD300;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.btn-cancel {
  padding: 12px 24px;
  background: #f3f4f6;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  color: #475569;
  cursor: pointer;
}

.btn-save {
  padding: 12px 32px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255,211,0,0.4);
}

.btn-save:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* 出诊信息 */
.schedule-info {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.schedule-info p {
  font-size: 15px;
  color: #475569;
  margin: 0 0 12px 0;
  line-height: 1.6;
}

.empty-text {
  color: #94a3b8;
}

.view-schedule {
  display: inline-block;
  font-size: 14px;
  color: #FFD300;
  text-decoration: none;
  font-weight: 600;
}

.view-schedule:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    gap: 24px;
    align-items: flex-start;
  }
  
  .profile-stats {
    width: 100%;
    justify-content: space-around;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
