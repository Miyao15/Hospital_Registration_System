<template>
  <div class="booking-page">
    <!-- 顶部导航 -->
    <header class="top-header">
      <div class="header-left">
        <div class="logo" @click="router.push('/')">
          <div class="logo-icon">优</div>
          <span class="logo-text">优医预约</span>
        </div>
      </div>
      <div class="header-right">
        <span class="secure-badge">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
          </svg>
          安全加密
        </span>
      </div>
    </header>

    <main class="main-content">
      <!-- 预约摘要卡片 -->
      <div class="booking-card">
        <div class="doctor-info">
          <img 
            :src="doctorInfo.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
            :alt="doctorInfo.name" 
            class="doctor-avatar"
          />
          <div class="doctor-details">
            <h2 class="doctor-name">{{ doctorInfo.name }}</h2>
            <p class="doctor-title">{{ doctorInfo.title }}</p>
            <p class="appointment-time">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              {{ formattedDateTime }}
            </p>
            <p class="department-info" v-if="doctorInfo.departmentName">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                <polyline points="9 22 9 12 15 12 15 22"></polyline>
              </svg>
              {{ doctorInfo.departmentName }}
            </p>
            <p class="medical-item" v-if="medicalItem">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
              </svg>
              {{ medicalItem.name }}
            </p>
          </div>
        </div>
      </div>

      <!-- 表单区域 -->
      <div class="form-section">
        <h1 class="form-title">请填写就诊人信息</h1>
        <p class="form-subtitle">为确保您的预约顺利进行，请提供以下信息</p>

        <form @submit.prevent="handleSubmit" class="patient-form">
          <!-- 姓名 -->
          <div class="form-row">
            <div class="form-group">
              <label for="name">姓名 <span class="required">*</span></label>
              <input 
                type="text" 
                id="name" 
                v-model="form.name" 
                placeholder="请输入真实姓名"
                :class="{ 'error': errors.name }"
              />
              <span v-if="errors.name" class="error-text">{{ errors.name }}</span>
            </div>
          </div>

          <!-- 手机号和身份证 -->
          <div class="form-row two-cols">
            <div class="form-group">
              <label for="phone">手机号 <span class="required">*</span></label>
              <input 
                type="tel" 
                id="phone" 
                v-model="form.phone" 
                placeholder="请输入手机号"
                :class="{ 'error': errors.phone }"
              />
              <span v-if="errors.phone" class="error-text">{{ errors.phone }}</span>
            </div>
            <div class="form-group">
              <label for="idCard">身份证号 <span class="required">*</span></label>
              <input 
                type="text" 
                id="idCard" 
                v-model="form.idCard" 
                placeholder="请输入身份证号"
                :class="{ 'error': errors.idCard }"
              />
              <span v-if="errors.idCard" class="error-text">{{ errors.idCard }}</span>
            </div>
          </div>

          <!-- 性别和出生日期 -->
          <div class="form-row two-cols">
            <div class="form-group">
              <label for="gender">性别 <span class="required">*</span></label>
              <select id="gender" v-model="form.gender" :class="{ 'error': errors.gender }">
                <option value="" disabled>请选择性别</option>
                <option value="MALE">男</option>
                <option value="FEMALE">女</option>
              </select>
              <span v-if="errors.gender" class="error-text">{{ errors.gender }}</span>
            </div>
            <div class="form-group">
              <label for="birthDate">出生日期 <span class="required">*</span></label>
              <input 
                type="date" 
                id="birthDate" 
                v-model="form.birthDate"
                :class="{ 'error': errors.birthDate }"
              />
              <span v-if="errors.birthDate" class="error-text">{{ errors.birthDate }}</span>
            </div>
          </div>

          <!-- 病情描述 -->
          <div class="form-row">
            <div class="form-group">
              <label for="symptom">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16" style="display: inline-block; vertical-align: middle; margin-right: 6px;">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                </svg>
                病情描述
                <span class="optional-hint">（选填，但有助于医生更好地了解您的情况）</span>
              </label>
              <textarea 
                id="symptom" 
                v-model="form.symptom" 
                placeholder="请详细描述您的症状、不适部位、持续时间、疼痛程度等，或说明就诊原因。例如：头痛3天，伴有恶心，疼痛程度中等..."
                rows="5"
                class="symptom-textarea"
              ></textarea>
              <div class="form-hint">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14">
                  <circle cx="12" cy="12" r="10"></circle>
                  <line x1="12" y1="16" x2="12" y2="12"></line>
                  <line x1="12" y1="8" x2="12.01" y2="8"></line>
                </svg>
                详细描述有助于医生提前了解您的病情，提高就诊效率
              </div>
            </div>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <button type="button" class="btn-back" @click="router.back()">返回修改</button>
            <button type="submit" class="btn-submit" :disabled="submitting">
              {{ submitting ? '提交中...' : '确认预约' }}
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getDoctorById } from '@/api/doctor';
import { getMedicalItemById } from '@/api/medicalItem';
import { createAppointment } from '@/api/appointment';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 医生和预约信息
const doctorInfo = ref({
  id: '',
  name: '',
  title: '',
  departmentName: '',
  avatarUrl: ''
});

const bookingInfo = ref({
  date: '',
  time: '',
  slotId: '',
  period: ''
});

const medicalItem = ref(null);
const submitting = ref(false);

// 表单数据
const form = ref({
  name: '',
  phone: '',
  idCard: '',
  gender: '',
  birthDate: '',
  symptom: ''
});

const errors = ref({});

// 格式化日期时间显示
const formattedDateTime = computed(() => {
  if (bookingInfo.value.date && bookingInfo.value.time) {
    const date = new Date(bookingInfo.value.date);
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const weekday = weekdays[date.getDay()];
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${year}年${month}月${day}日 ${weekday} ${bookingInfo.value.time}`;
  }
  return '预约时间待确认';
});

onMounted(async () => {
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行预约');
    // 保存当前路由参数到 localStorage
    localStorage.setItem('pendingBooking', JSON.stringify(route.query));
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }

  // 优先从路由参数获取，如果没有则从 localStorage 恢复
  let bookingParams = { ...route.query };
  const pendingBooking = localStorage.getItem('pendingBooking');
  if (pendingBooking && Object.keys(route.query).length === 0) {
    try {
      const savedBooking = JSON.parse(pendingBooking);
      bookingParams = savedBooking;
      // 清除已恢复的预约信息
      localStorage.removeItem('pendingBooking');
    } catch (e) {
      console.error('恢复预约信息失败:', e);
    }
  }

  const { doctorId, doctorName, doctorTitle, departmentName, date, time, slotId, period, medicalItemId } = bookingParams;
  
  doctorInfo.value = {
    id: doctorId,
    name: doctorName || '',
    title: doctorTitle || '',
    departmentName: departmentName || ''
  };
  
  bookingInfo.value = {
    date: date || '',
    time: time || '',
    slotId: slotId || '',
    period: period || ''
  };

  // 获取医生详细信息
  if (doctorId && !doctorName) {
    try {
      const data = await getDoctorById(doctorId);
      if (data) {
        doctorInfo.value.name = data.name;
        doctorInfo.value.title = data.title;
        doctorInfo.value.departmentName = data.departmentName;
        doctorInfo.value.avatarUrl = data.avatarUrl;
      }
    } catch (e) {
      console.error('获取医生信息失败:', e);
    }
  }

  // 获取检查项目信息
  if (medicalItemId) {
    try {
      const data = await getMedicalItemById(medicalItemId);
      if (data) {
        medicalItem.value = data;
      }
    } catch (e) {
      console.error('获取检查项目失败:', e);
    }
  }

  // 如果用户已登录，预填信息
  if (userStore.isLoggedIn && userStore.userInfo) {
    form.value.phone = userStore.userInfo.phone || '';
  }
});

// 表单验证
const validateForm = () => {
  errors.value = {};
  
  if (!form.value.name.trim()) {
    errors.value.name = '请输入姓名';
  }
  
  if (!form.value.phone.trim()) {
    errors.value.phone = '请输入手机号';
  } else if (!/^1[3-9]\d{9}$/.test(form.value.phone)) {
    errors.value.phone = '手机号格式不正确';
  }
  
  if (!form.value.idCard.trim()) {
    errors.value.idCard = '请输入身份证号';
  } else if (!/^\d{17}[\dXx]$/.test(form.value.idCard)) {
    errors.value.idCard = '身份证号格式不正确';
  }
  
  if (!form.value.gender) {
    errors.value.gender = '请选择性别';
  }
  
  if (!form.value.birthDate) {
    errors.value.birthDate = '请选择出生日期';
  }
  
  return Object.keys(errors.value).length === 0;
};

// 提交预约
const handleSubmit = async () => {
  if (!validateForm()) {
    ElMessage.warning('请填写完整的就诊人信息');
    return;
  }

  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行预约');
    // 保存当前页面信息到 localStorage
    localStorage.setItem('pendingBooking', JSON.stringify({
      ...route.query,
      form: form.value
    }));
    router.push('/login');
    return;
  }

  submitting.value = true;

  try {
    const appointmentData = {
      doctorId: doctorInfo.value.id,
      timeSlotId: bookingInfo.value.slotId,
      appointmentDate: bookingInfo.value.date,
      period: bookingInfo.value.period || 'MORNING',
      patientName: form.value.name,
      patientPhone: form.value.phone,
      symptomDesc: form.value.symptom || '',
      medicalItemId: route.query.medicalItemId || null
    };

    const result = await createAppointment(appointmentData);
    
    ElMessage.success('预约成功！您可以在"我的预约"中查看详情。');
    
    // 跳转到患者首页
    router.push('/patient/home');
    
  } catch (error) {
    console.error('预约失败:', error);
    ElMessage.error(error.message || '预约失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.booking-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

/* 顶部导航 */
.top-header {
  background: #fff;
  padding: 16px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: #FFD300;
  border-radius: 0 50% 50% 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #2a2a2a;
}

.secure-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 14px;
}

/* 主内容区 */
.main-content {
  max-width: 680px;
  margin: 0 auto;
  padding: 40px 24px;
}

/* 预约卡片 */
.booking-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.doctor-info {
  display: flex;
  gap: 20px;
}

.doctor-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.doctor-details {
  flex: 1;
}

.doctor-name {
  font-size: 20px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 4px 0;
}

.doctor-title {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px 0;
}

.appointment-time,
.department-info,
.medical-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #2a2a2a;
  margin: 6px 0;
}

.appointment-time svg,
.department-info svg,
.medical-item svg {
  color: #666;
  flex-shrink: 0;
}

/* 表单区域 */
.form-section {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0 0 32px 0;
}

.patient-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row.two-cols .form-group {
  flex: 1;
}

.form-group {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #2a2a2a;
  margin-bottom: 8px;
}

.required {
  color: #dc2626;
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
  background: #fff;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #FFD300;
  box-shadow: 0 0 0 3px rgba(255, 211, 0, 0.2);
}

.form-group input.error,
.form-group select.error {
  border-color: #dc2626;
}

.form-group input::placeholder,
.form-group textarea::placeholder {
  color: #9ca3af;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group textarea.symptom-textarea {
  min-height: 120px;
  font-family: inherit;
  line-height: 1.6;
}

.optional-hint {
  font-size: 12px;
  font-weight: normal;
  color: #666;
  margin-left: 4px;
}

.form-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 13px;
  color: #666;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #FFD300;
}

.form-hint svg {
  color: #FFD300;
  flex-shrink: 0;
}

.error-text {
  font-size: 12px;
  color: #dc2626;
  margin-top: 4px;
}

/* 按钮 */
.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.btn-back {
  flex: 1;
  padding: 14px 24px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2a2a2a;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-back:hover {
  background: #f3f4f6;
}

.btn-submit {
  flex: 2;
  padding: 14px 24px;
  background: #FFD300;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #2a2a2a;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover {
  background: #f4ca00;
}

.btn-submit:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 640px) {
  .main-content {
    padding: 24px 16px;
  }
  
  .form-row.two-cols {
    flex-direction: column;
    gap: 24px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn-back,
  .btn-submit {
    flex: none;
  }
}
</style>
