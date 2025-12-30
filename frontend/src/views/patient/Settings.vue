<template>
  <div class="settings-page">
    <div class="page-header">
      <h1>设置</h1>
      <p class="subtitle">管理您的账户和偏好设置</p>
    </div>

    <div class="settings-grid">
      <!-- 账户安全 -->
      <div class="settings-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0110 0v4"></path>
            </svg>
          </div>
          <h2>账户安全</h2>
        </div>
        <div class="setting-items">
          <div class="setting-item" @click="showChangePassword = true">
            <div class="item-info">
              <span class="item-label">修改密码</span>
              <span class="item-desc">定期更换密码以保护账户安全</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
          <div class="setting-item" @click="showBindPhone = true">
            <div class="item-info">
              <span class="item-label">绑定手机</span>
              <span class="item-desc">{{ maskedPhone || '暂未绑定' }}</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="settings-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
              <path d="M13.73 21a2 2 0 01-3.46 0"></path>
            </svg>
          </div>
          <h2>通知设置</h2>
        </div>
        <div class="setting-items">
          <div class="setting-item toggle">
            <div class="item-info">
              <span class="item-label">预约提醒</span>
              <span class="item-desc">就诊前一天发送短信提醒</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.appointmentReminder" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
          <div class="setting-item toggle">
            <div class="item-info">
              <span class="item-label">消息推送</span>
              <span class="item-desc">接收系统消息和活动通知</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.pushNotification" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 隐私设置 -->
      <div class="settings-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
          </div>
          <h2>隐私设置</h2>
        </div>
        <div class="setting-items">
          <div class="setting-item toggle">
            <div class="item-info">
              <span class="item-label">隐藏就诊记录</span>
              <span class="item-desc">不向其他医生展示历史就诊记录</span>
            </div>
            <label class="switch">
              <input type="checkbox" v-model="settings.hideMedicalRecords" @change="saveSettings" />
              <span class="slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="settings-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
              <circle cx="12" cy="12" r="3"></circle>
              <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"></path>
            </svg>
          </div>
          <h2>其他</h2>
        </div>
        <div class="setting-items">
          <div class="setting-item" @click="clearCache">
            <div class="item-info">
              <span class="item-label">清除缓存</span>
              <span class="item-desc">清除本地缓存数据</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
          <div class="setting-item" @click="showAbout">
            <div class="item-info">
              <span class="item-label">关于我们</span>
              <span class="item-desc">版本 1.0.0</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
          <div class="setting-item danger" @click="handleLogout">
            <div class="item-info">
              <span class="item-label">退出登录</span>
              <span class="item-desc">退出当前账号</span>
            </div>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <transition name="modal">
      <div v-if="showChangePassword" class="modal-overlay" @click.self="showChangePassword = false">
        <div class="modal-content">
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
              <input type="password" v-model="passwordForm.newPassword" required minlength="6" />
            </div>
            <div class="form-group">
              <label>确认新密码</label>
              <input type="password" v-model="passwordForm.confirmPassword" required />
            </div>
            <button type="submit" class="btn-primary" :disabled="changingPassword">
              {{ changingPassword ? '修改中...' : '确认修改' }}
            </button>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const router = useRouter();
const userStore = useUserStore();

const showChangePassword = ref(false);
const showBindPhone = ref(false);
const changingPassword = ref(false);

const settings = ref({
  appointmentReminder: true,
  pushNotification: true,
  hideMedicalRecords: false
});

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const maskedPhone = computed(() => {
  const phone = userStore.userInfo?.phone;
  if (!phone) return null;
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
});

onMounted(() => {
  // 从本地存储读取设置
  const savedSettings = localStorage.getItem('userSettings');
  if (savedSettings) {
    try {
      settings.value = { ...settings.value, ...JSON.parse(savedSettings) };
    } catch (e) {
      console.error('加载设置失败');
    }
  }
});

const saveSettings = () => {
  localStorage.setItem('userSettings', JSON.stringify(settings.value));
  ElMessage.success('设置已保存');
};

const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  
  changingPassword.value = true;
  try {
    await request.post('/api/auth/change-password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    });
    ElMessage.success('密码修改成功');
    showChangePassword.value = false;
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' };
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '密码修改失败');
  } finally {
    changingPassword.value = false;
  }
};

const clearCache = () => {
  ElMessageBox.confirm('确定要清除本地缓存吗？', '清除缓存', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.clear();
    ElMessage.success('缓存已清除');
  }).catch(() => {});
};

const showAbout = () => {
  ElMessageBox.alert(
    '医院预约挂号系统 v1.0.0\n\n致力于为您提供便捷的医疗预约服务',
    '关于我们',
    {
      confirmButtonText: '知道了'
    }
  );
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    userStore.logout();
    router.push('/login');
    ElMessage.success('已退出登录');
  } catch (e) {
    // 用户取消
  }
};
</script>

<style scoped>
.settings-page {
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

.settings-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.settings-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FFD300, #f4ca00);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2a2a2a;
}

.card-header h2 {
  font-size: 16px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0;
}

.setting-items {
  display: flex;
  flex-direction: column;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  cursor: pointer;
  transition: background 0.2s;
}

.setting-item:not(:last-child) {
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:hover {
  background: #fafafa;
}

.setting-item.toggle {
  cursor: default;
}

.setting-item.danger .item-label {
  color: #dc2626;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-label {
  font-size: 15px;
  font-weight: 600;
  color: #2a2a2a;
}

.item-desc {
  font-size: 13px;
  color: #9ca3af;
}

.setting-item svg {
  color: #9ca3af;
}

/* Switch 开关 */
.switch {
  position: relative;
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
  background-color: #e5e7eb;
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
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

input:checked + .slider {
  background-color: #FFD300;
}

input:checked + .slider:before {
  transform: translateX(22px);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  padding: 24px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #9ca3af;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
}

.form-group input:focus {
  outline: none;
  border-color: #FFD300;
  box-shadow: 0 0 0 3px rgba(255, 211, 0, 0.2);
}

.btn-primary {
  padding: 14px 24px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  margin-top: 8px;
}

.btn-primary:hover {
  background: #f4ca00;
}

.btn-primary:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* Modal 动画 */
.modal-enter-active, .modal-leave-active {
  transition: opacity 0.3s;
}

.modal-enter-from, .modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-content, .modal-leave-active .modal-content {
  transition: transform 0.3s;
}

.modal-enter-from .modal-content, .modal-leave-to .modal-content {
  transform: scale(0.95);
}
</style>

