<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人信息</h1>
      <p class="subtitle">查看和编辑您的个人资料</p>
    </div>

    <div class="content-card">
      <div class="card-header">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img :src="userInfo.avatarUrl || defaultAvatar" class="avatar" />
            <label class="avatar-upload-btn" for="avatar-input">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"></path>
                <circle cx="12" cy="13" r="4"></circle>
              </svg>
            </label>
            <input 
              type="file" 
              id="avatar-input" 
              accept="image/*" 
              @change="handleAvatarChange" 
              style="display: none"
            />
          </div>
          <div class="avatar-info">
            <span class="avatar-hint">点击图标更换头像</span>
            <span class="avatar-size">支持 JPG、PNG 格式，最大 5MB</span>
          </div>
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
              <input type="date" v-model="form.birthDate" lang="en" />
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
const uploadingAvatar = ref(false);

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
      userInfo.value = data;
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

// 压缩图片函数
const compressImage = (file, maxWidth = 200, quality = 0.8) => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        
        // 按比例缩放
        if (width > maxWidth) {
          height = (height * maxWidth) / width;
          width = maxWidth;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob((blob) => {
          resolve(new File([blob], file.name, { type: 'image/jpeg' }));
        }, 'image/jpeg', quality);
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  });
};

const handleAvatarChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件');
    return;
  }
  
  // 验证文件大小 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB');
    return;
  }
  
  uploadingAvatar.value = true;
  try {
    // 压缩图片（头像只需要200px宽度）
    const compressedFile = await compressImage(file, 200, 0.8);
    console.log(`图片压缩: ${(file.size/1024).toFixed(1)}KB -> ${(compressedFile.size/1024).toFixed(1)}KB`);
    
    // 上传文件
    const formData = new FormData();
    formData.append('file', compressedFile);
    
    const uploadRes = await request.post('/api/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    
    if (uploadRes && uploadRes.url) {
      // 更新用户头像
      await request.put('/api/patients/profile', {
        ...form.value,
        avatarUrl: uploadRes.url
      });
      
      userInfo.value.avatarUrl = uploadRes.url;
      // 缓存头像到localStorage
      localStorage.setItem('user_avatar', uploadRes.url);
      ElMessage.success('头像更新成功');
    }
  } catch (e) {
    console.error('上传头像失败:', e);
    ElMessage.error('上传头像失败，请稍后重试');
  } finally {
    uploadingAvatar.value = false;
    event.target.value = ''; // 清空input
  }
};

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

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #FFD300;
}

.avatar-upload-btn {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 28px;
  height: 28px;
  background: #FFD300;
  border: 2px solid #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #2a2a2a;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.avatar-upload-btn:hover {
  background: #f4ca00;
  transform: scale(1.1);
}

.avatar-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.avatar-hint {
  font-size: 14px;
  color: #2a2a2a;
  font-weight: 500;
}

.avatar-size {
  font-size: 12px;
  color: #9ca3af;
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
