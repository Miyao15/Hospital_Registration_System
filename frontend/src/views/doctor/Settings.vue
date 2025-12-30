<template>
  <div class="settings-page">
    <div class="page-header">
      <h2>设置</h2>
      <p>管理您的账户和偏好设置</p>
    </div>

    <div class="settings-container">
      <!-- 账户设置 -->
      <div class="settings-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          账户设置
        </h3>
        
        <div class="settings-list">
          <div class="setting-item" @click="showChangePassword = true">
            <div class="setting-info">
              <span class="setting-label">修改密码</span>
              <span class="setting-desc">定期更换密码以确保账户安全</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">手机号码</span>
              <span class="setting-desc">{{ maskPhone(userPhone) }}</span>
            </div>
            <button class="btn-change">更换</button>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="settings-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 01-3.46 0"></path>
          </svg>
          通知设置
        </h3>
        
        <div class="settings-list">
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">预约提醒</span>
              <span class="setting-desc">有新预约时接收通知</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.appointmentNotify" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">评价通知</span>
              <span class="setting-desc">收到患者评价时接收通知</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.reviewNotify" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">系统通知</span>
              <span class="setting-desc">接收系统公告和更新通知</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.systemNotify" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="settings-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <circle cx="12" cy="12" r="3"></circle>
            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"></path>
          </svg>
          其他
        </h3>
        
        <div class="settings-list">
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">关于我们</span>
              <span class="setting-desc">查看应用信息和版本</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
          
          <div class="setting-item">
            <div class="setting-info">
              <span class="setting-label">帮助中心</span>
              <span class="setting-desc">常见问题和使用指南</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
        </div>
      </div>

      <!-- 退出登录 -->
      <button class="btn-logout" @click="handleLogout">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
          <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"></path>
          <polyline points="16 17 21 12 16 7"></polyline>
          <line x1="21" y1="12" x2="9" y2="12"></line>
        </svg>
        退出登录
      </button>
    </div>

    <!-- 修改密码弹窗 -->
    <div class="modal-overlay" v-if="showChangePassword" @click.self="showChangePassword = false">
      <div class="modal">
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-btn" @click="showChangePassword = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        
        <form @submit.prevent="changePassword" class="modal-form">
          <div class="form-group">
            <label>当前密码</label>
            <input type="password" v-model="passwordForm.oldPassword" required />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input type="password" v-model="passwordForm.newPassword" required />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input type="password" v-model="passwordForm.confirmPassword" required />
          </div>
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="showChangePassword = false">取消</button>
            <button type="submit" class="btn-primary">确认修改</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

const userPhone = ref(userStore.userInfo?.phone || '');
const showChangePassword = ref(false);

const settings = reactive({
  appointmentNotify: true,
  reviewNotify: true,
  systemNotify: true
});

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
};

const saveSettings = () => {
  // TODO: 保存设置到后端
  ElMessage.success('设置已保存');
};

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致');
    return;
  }
  
  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('新密码长度至少6位');
    return;
  }
  
  // TODO: 调用修改密码API
  ElMessage.success('密码修改成功');
  showChangePassword.value = false;
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    userStore.logout();
    router.push('/login');
  } catch (e) {
    // 用户取消
  }
};
</script>

<style scoped>
.settings-page {
  max-width: 700px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.page-header p {
  font-size: 14px;
  color: #64748b;
  margin: 8px 0 0;
}

.settings-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.settings-list {
  display: flex;
  flex-direction: column;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  display: flex;
  flex-direction: column;
}

.setting-label {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a2e;
}

.setting-desc {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 2px;
}

.btn-change {
  padding: 6px 16px;
  background: #f3f4f6;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 26px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #e2e8f0;
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #FFD300;
}

input:checked + .slider:before {
  transform: translateX(22px);
}

.btn-logout {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 100%;
  padding: 16px;
  background: #fff;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  color: #ef4444;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-logout:hover {
  background: #fef2f2;
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
  max-width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.modal-header h3 {
  font-size: 18px;
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
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 15px;
}

.form-group input:focus {
  outline: none;
  border-color: #FFD300;
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
</style>

