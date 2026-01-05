<template>
  <div class="schedule-add-page">
    <div class="page-header">
      <button class="btn-back" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
        返回
      </button>
      <div>
        <h1 class="page-title">新增排班</h1>
        <p class="page-subtitle">设置您的门诊时间和号源数量</p>
      </div>
    </div>

    <div class="form-container">
      <!-- 日期选择 -->
      <div class="form-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          选择日期
        </h3>
        
        <div class="date-mode-toggle">
          <button 
            :class="['mode-btn', { active: dateMode === 'single' }]"
            @click="dateMode = 'single'"
          >单日排班</button>
          <button 
            :class="['mode-btn', { active: dateMode === 'range' }]"
            @click="dateMode = 'range'"
          >批量排班</button>
        </div>

        <div class="date-inputs">
          <div class="input-group">
            <label>{{ dateMode === 'single' ? '排班日期' : '开始日期' }}</label>
            <input type="date" v-model="form.startDate" :min="minDate" />
          </div>
          <div class="input-group" v-if="dateMode === 'range'">
            <label>结束日期</label>
            <input type="date" v-model="form.endDate" :min="form.startDate || minDate" />
          </div>
        </div>
      </div>

      <!-- 排班类型快速选择 -->
      <div class="form-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
          排班模板
        </h3>
        
        <div class="schedule-types">
          <div 
            :class="['type-card', { selected: form.scheduleType === 'TYPE1' }]"
            @click="selectScheduleType('TYPE1')"
          >
            <div class="type-header">
              <span class="type-name">标准排班</span>
              <span class="type-badge">推荐</span>
            </div>
            <div class="type-details">
              <div class="detail-row">
                <span class="period morning">上午</span>
                <span class="slots">10 号源</span>
              </div>
              <div class="detail-row">
                <span class="period afternoon">下午</span>
                <span class="slots">5 号源</span>
              </div>
            </div>
          </div>
          
          <div 
            :class="['type-card', { selected: form.scheduleType === 'TYPE2' }]"
            @click="selectScheduleType('TYPE2')"
          >
            <div class="type-header">
              <span class="type-name">高峰排班</span>
            </div>
            <div class="type-details">
              <div class="detail-row">
                <span class="period morning">上午</span>
                <span class="slots">20 号源</span>
              </div>
              <div class="detail-row">
                <span class="period afternoon">下午</span>
                <span class="slots">15 号源</span>
              </div>
            </div>
          </div>
          
          <div 
            :class="['type-card', { selected: form.scheduleType === 'CUSTOM' }]"
            @click="selectScheduleType('CUSTOM')"
          >
            <div class="type-header">
              <span class="type-name">自定义</span>
            </div>
            <div class="type-details">
              <p class="custom-hint">自由设置时间和号源</p>
            </div>
          </div>
        </div>
      </div>
      <!-- 时间段配置 -->
      <div class="form-section" v-if="form.scheduleType === 'CUSTOM'">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <circle cx="12" cy="12" r="10"></circle>
            <polyline points="12 6 12 12 16 14"></polyline>
          </svg>
          时间段配置
        </h3>

        <!-- 上午 -->
        <div class="period-config">
          <div class="period-header">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.hasMorning" />
              <span class="period-badge morning">上午门诊</span>
            </label>
          </div>
          <div class="period-fields" v-if="form.hasMorning">
            <div class="time-range">
              <div class="input-group small">
                <label>开始时间</label>
                <input type="time" v-model="form.morningStartTime" />
              </div>
              <span class="time-separator">至</span>
              <div class="input-group small">
                <label>结束时间</label>
                <input type="time" v-model="form.morningEndTime" />
              </div>
            </div>
            <div class="input-group small">
              <label>号源数量</label>
              <input type="number" v-model.number="form.morningSlots" min="1" max="50" />
            </div>
          </div>
        </div>

        <!-- 下午 -->
        <div class="period-config">
          <div class="period-header">
            <label class="checkbox-label">
              <input type="checkbox" v-model="form.hasAfternoon" />
              <span class="period-badge afternoon">下午门诊</span>
            </label>
          </div>
          <div class="period-fields" v-if="form.hasAfternoon">
            <div class="time-range">
              <div class="input-group small">
                <label>开始时间</label>
                <input type="time" v-model="form.afternoonStartTime" />
              </div>
              <span class="time-separator">至</span>
              <div class="input-group small">
                <label>结束时间</label>
                <input type="time" v-model="form.afternoonEndTime" />
              </div>
            </div>
            <div class="input-group small">
              <label>号源数量</label>
              <input type="number" v-model.number="form.afternoonSlots" min="1" max="50" />
            </div>
          </div>
        </div>
      </div>

      <!-- 预览 -->
      <div class="form-section preview-section">
        <h3 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
            <circle cx="12" cy="12" r="3"></circle>
          </svg>
          排班预览
        </h3>
        <div class="preview-card">
          <div class="preview-dates">
            <span class="preview-label">日期范围：</span>
            <span class="preview-value">
              {{ form.startDate || '未选择' }}
              <template v-if="dateMode === 'range' && form.endDate">
                至 {{ form.endDate }} ({{ dayCount }}天)
              </template>
            </span>
          </div>
          <div class="preview-slots">
            <div class="preview-slot" v-if="previewMorning">
              <span class="period-badge morning">上午</span>
              <span>{{ previewMorningTime }}</span>
              <span class="slot-count">{{ previewMorningSlots }} 号源</span>
            </div>
            <div class="preview-slot" v-if="previewAfternoon">
              <span class="period-badge afternoon">下午</span>
              <span>{{ previewAfternoonTime }}</span>
              <span class="slot-count">{{ previewAfternoonSlots }} 号源</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="form-actions">
        <button class="btn-cancel" @click="$router.back()">取消</button>
        <button class="btn-submit" @click="submitSchedule" :disabled="!canSubmit || submitting">
          <span v-if="submitting">提交中...</span>
          <span v-else>确认新增</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();

const dateMode = ref('single');
const submitting = ref(false);

const form = reactive({
  startDate: '',
  endDate: '',
  scheduleType: 'TYPE1',
  hasMorning: true,
  hasAfternoon: true,
  morningStartTime: '08:00',
  morningEndTime: '12:00',
  morningSlots: 20,
  afternoonStartTime: '14:00',
  afternoonEndTime: '17:30',
  afternoonSlots: 15
});

const minDate = computed(() => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return tomorrow.toISOString().split('T')[0];
});

const dayCount = computed(() => {
  if (!form.startDate || !form.endDate) return 0;
  const start = new Date(form.startDate);
  const end = new Date(form.endDate);
  return Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
});

const previewMorning = computed(() => {
  if (form.scheduleType === 'CUSTOM') return form.hasMorning;
  return true;
});

const previewAfternoon = computed(() => {
  if (form.scheduleType === 'CUSTOM') return form.hasAfternoon;
  return true;
});

const previewMorningSlots = computed(() => {
  if (form.scheduleType === 'TYPE1') return 10;
  if (form.scheduleType === 'TYPE2') return 20;
  return form.morningSlots;
});

const previewAfternoonSlots = computed(() => {
  if (form.scheduleType === 'TYPE1') return 5;
  if (form.scheduleType === 'TYPE2') return 15;
  return form.afternoonSlots;
});

const previewMorningTime = computed(() => {
  if (form.scheduleType === 'CUSTOM') {
    return `${form.morningStartTime} - ${form.morningEndTime}`;
  }
  return '08:00 - 12:00';
});

const previewAfternoonTime = computed(() => {
  if (form.scheduleType === 'CUSTOM') {
    return `${form.afternoonStartTime} - ${form.afternoonEndTime}`;
  }
  return '14:00 - 17:30';
});

const canSubmit = computed(() => {
  if (!form.startDate) return false;
  if (dateMode.value === 'range' && !form.endDate) return false;
  if (form.scheduleType === 'CUSTOM' && !form.hasMorning && !form.hasAfternoon) return false;
  return true;
});

const selectScheduleType = (type) => {
  form.scheduleType = type;
};

const submitSchedule = async () => {
  if (!canSubmit.value || submitting.value) return;
  
  submitting.value = true;
  try {
    const payload = {
      startDate: form.startDate,
      endDate: dateMode.value === 'range' ? form.endDate : null,
      scheduleType: form.scheduleType === 'CUSTOM' ? null : form.scheduleType,
      hasMorning: form.scheduleType === 'CUSTOM' ? form.hasMorning : true,
      hasAfternoon: form.scheduleType === 'CUSTOM' ? form.hasAfternoon : true
    };
    
    if (form.scheduleType === 'CUSTOM') {
      if (form.hasMorning) {
        payload.morningStartTime = form.morningStartTime;
        payload.morningEndTime = form.morningEndTime;
        payload.morningSlots = form.morningSlots;
      }
      if (form.hasAfternoon) {
        payload.afternoonStartTime = form.afternoonStartTime;
        payload.afternoonEndTime = form.afternoonEndTime;
        payload.afternoonSlots = form.afternoonSlots;
      }
    }
    
    const result = await request.post('/api/doctor/schedules', payload);
    alert(result.message || '排班创建成功');
    router.push('/doctor/schedule');
  } catch (e) {
    alert(e.message || '创建排班失败');
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.schedule-add-page {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 32px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover {
  border-color: #FFD300;
  background: #FFF9E5;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.form-container {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 12px;
  padding: 24px;
}

.form-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #F0F0F0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 16px 0;
}

.date-mode-toggle {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.mode-btn {
  padding: 8px 16px;
  background: #F5F5F5;
  border: 1px solid #E8E8E8;
  border-radius: 6px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn.active {
  background: #FFD300;
  border-color: #FFD300;
  color: #2A2A2A;
  font-weight: 600;
}

.date-inputs {
  display: flex;
  gap: 16px;
}

.input-group {
  flex: 1;
}

.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #666;
  margin-bottom: 6px;
}

.input-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E8E8E8;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.input-group input:focus {
  outline: none;
  border-color: #FFD300;
}

.input-group.small {
  flex: none;
  width: auto;
}

.input-group.small input {
  width: 120px;
}

.schedule-types {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.type-card {
  padding: 16px;
  border: 2px solid #E8E8E8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.type-card:hover {
  border-color: #FFD300;
}

.type-card.selected {
  border-color: #FFD300;
  background: #FFF9E5;
}

.type-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.type-name {
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
}

.type-badge {
  padding: 2px 8px;
  background: #E8F5E9;
  color: #2E7D32;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}

.type-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
}

.period {
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.period.morning {
  background: #E3F2FD;
  color: #1976D2;
}

.period.afternoon {
  background: #FCE4EC;
  color: #C2185B;
}

.slots {
  color: #666;
}

.custom-hint {
  font-size: 12px;
  color: #999;
  margin: 0;
  text-align: center;
}

.period-config {
  background: #FAFAFA;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.period-header {
  margin-bottom: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input {
  width: 16px;
  height: 16px;
}

.period-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
}

.period-badge.morning {
  background: #E3F2FD;
  color: #1976D2;
}

.period-badge.afternoon {
  background: #FCE4EC;
  color: #C2185B;
}

.period-fields {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.time-range {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.time-separator {
  padding-bottom: 10px;
  color: #999;
}

.preview-section {
  background: #F8F9FA;
  border-radius: 8px;
  padding: 20px;
  margin: 0 -24px -24px;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.preview-card {
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  padding: 16px;
}

.preview-dates {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}

.preview-label {
  font-size: 13px;
  color: #666;
}

.preview-value {
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
}

.preview-slots {
  display: flex;
  gap: 16px;
}

.preview-slot {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.slot-count {
  font-weight: 600;
  color: #2A2A2A;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #F0F0F0;
}

.btn-cancel {
  padding: 12px 24px;
  background: #fff;
  border: 1px solid #E8E8E8;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #F5F5F5;
}

.btn-submit {
  padding: 12px 32px;
  background: #FFD300;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #2A2A2A;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #F4CA00;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .schedule-types {
    grid-template-columns: 1fr;
  }
  
  .date-inputs {
    flex-direction: column;
  }
  
  .period-fields {
    flex-direction: column;
    align-items: stretch;
  }
  
  .time-range {
    flex-wrap: wrap;
  }
  
  .input-group.small {
    width: 100%;
  }
  
  .input-group.small input {
    width: 100%;
  }
}
</style>
