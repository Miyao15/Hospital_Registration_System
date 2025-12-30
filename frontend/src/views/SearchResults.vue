<template>
  <div class="search-results-page">
    
    <header class="top-bar">
      <div class="nav-left">
        <div class="logo-z">Z</div>
        <div class="search-bar-composite" ref="searchBarRef">
          <div class="input-group search-input-wrapper">
            <svg class="icon-input" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
            <input 
              type="text" 
              placeholder="病情、科室..." 
              v-model="searchCondition" 
              @keyup.enter="handleSearch"
              @focus="showSearchSuggestions = true"
              @input="handleSearchInput"
            />
            <!-- 搜索建议下拉列表 -->
            <div v-if="showSearchSuggestions && (filteredSearchSuggestions.length > 0 || searchSuggestions.length > 0)" class="search-suggestions-dropdown">
              <div 
                class="suggestion-item" 
                v-for="(suggestion, index) in filteredSearchSuggestions.length > 0 ? filteredSearchSuggestions : searchSuggestions" 
                :key="index"
                @click="selectSearchSuggestion(suggestion)"
              >
                <svg class="suggestion-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
                <span class="suggestion-text">{{ suggestion.label || suggestion.name || suggestion }}</span>
                <span v-if="suggestion.type" class="suggestion-type">{{ suggestion.type }}</span>
              </div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="input-group">
            <input type="text" placeholder="地点" v-model="searchLocation" />
          </div>
          <button class="search-btn" @click="handleSearch">
            <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="3" fill="none"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          </button>
        </div>
      </div>
      <div class="nav-right">
        <template v-if="userStore.isLoggedIn">
          <span class="user-greeting">Hi, {{ userStore.userInfo?.username }}</span>
          <a href="#" class="nav-link" @click.prevent="userStore.logout()">退出</a>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="btn-signup">注册</router-link>
        </template>
      </div>
    </header>

    <div class="filter-bar" ref="filterBarRef">
      <div class="filter-item-wrapper">
        <button 
          class="filter-pill" 
          :class="{ active: activeFilter === 'time' || selectedTimeFilters.length > 0 }"
          @click="toggleFilterDropdown('time')"
        >
          📅 时间灵活
        </button>
        <div v-if="activeFilter === 'time'" class="filter-dropdown">
          <div class="filter-options">
            <label class="filter-option" v-for="timeOption in timeFilterOptions" :key="timeOption.value">
              <input 
                type="checkbox" 
                :value="timeOption.value"
                v-model="selectedTimeFilters"
              />
              <span class="option-label">{{ timeOption.label }}</span>
            </label>
          </div>
          <div class="filter-actions">
            <button class="btn-clear" @click="clearTimeFilters">清除</button>
            <button class="btn-apply" @click="applyFilters">应用</button>
          </div>
        </div>
      </div>

      <div class="filter-item-wrapper">
        <button 
          class="filter-pill" 
          :class="{ active: activeFilter === 'timeslot' || selectedTimeslotFilters.length > 0 }"
          @click="toggleFilterDropdown('timeslot')"
        >
          时间段
        </button>
        <div v-if="activeFilter === 'timeslot'" class="filter-dropdown">
          <div class="filter-options">
            <label class="filter-option" v-for="timeslotOption in timeslotFilterOptions" :key="timeslotOption.value">
              <input 
                type="checkbox" 
                :value="timeslotOption.value"
                v-model="selectedTimeslotFilters"
              />
              <span class="option-label">{{ timeslotOption.label }}</span>
            </label>
          </div>
          <div class="filter-actions">
            <button class="btn-clear" @click="clearTimeslotFilters">清除</button>
            <button class="btn-apply" @click="applyFilters">应用</button>
          </div>
        </div>
      </div>

      <div class="filter-item-wrapper">
        <button 
          class="filter-pill" 
          :class="{ active: activeFilter === 'department' || selectedDepartmentIds.length > 0 }"
          @click="toggleFilterDropdown('department')"
        >
          科室
        </button>
        <div v-if="activeFilter === 'department'" class="filter-dropdown">
          <div class="filter-options">
            <label class="filter-option" v-for="dept in departments" :key="dept.id">
              <input 
                type="checkbox" 
                :value="dept.id"
                v-model="selectedDepartmentIds"
              />
              <span class="option-label">{{ dept.name }}</span>
            </label>
          </div>
          <div class="filter-actions">
            <button class="btn-clear" @click="clearDepartmentFilters">清除</button>
            <button class="btn-apply" @click="applyFilters">应用</button>
          </div>
        </div>
      </div>

      <div class="filter-item-wrapper">
        <button 
          class="filter-pill" 
          :class="{ active: activeFilter === 'distance' || selectedDistanceFilter }"
          @click="toggleFilterDropdown('distance')"
        >
          距离
        </button>
        <div v-if="activeFilter === 'distance'" class="filter-dropdown">
          <div class="filter-options">
            <label class="filter-option" v-for="distanceOption in distanceFilterOptions" :key="distanceOption.value">
              <input 
                type="radio" 
                name="distance"
                :value="distanceOption.value"
                v-model="selectedDistanceFilter"
              />
              <span class="option-label">{{ distanceOption.label }}</span>
            </label>
          </div>
          <div class="filter-actions">
            <button class="btn-clear" @click="clearDistanceFilter">清除</button>
            <button class="btn-apply" @click="applyFilters">应用</button>
          </div>
        </div>
      </div>

      <button class="filter-pill" @click="handleFilterClick('more')">更多筛选</button>
    </div>

    <div class="main-layout">
      
      <div class="results-column">
        
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

        <div class="results-header">
          <h2>{{ doctors.length }} 位医生可用</h2>
          <div class="date-controls">
            <button class="nav-arrow" @click="prevDatePage" :disabled="dayOffset === 0">‹</button>
            <span class="date-range-text">{{ dateRangeText }}</span>
            <button class="nav-arrow" @click="nextDatePage">›</button>
          </div>
        </div>

        <div class="doctor-card" v-for="doctor in doctors" :key="doctor.id">
          
          <div class="card-info">
            <div class="info-top">
              <img :src="doctor.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="avatar" />
              <div class="badge-sponsored" v-if="doctor.sponsored">
                赞助 <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
              </div>
            </div>

            <div class="info-details">
              <h3 class="doc-name" @click="openBookingModal(doctor)">{{ doctor.name }}</h3>
              <p class="doc-title">{{ doctor.title }}</p>
              <p class="doc-dept">{{ doctor.departmentName }}</p>
              
              <div class="rating-row">
                <div class="stars">
                  <svg v-for="i in 5" :key="i" class="star-icon" fill="#FFD300" viewBox="0 0 24 24"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                </div>
                <span class="score">{{ doctor.rating || 5.0 }}</span>
                <span class="reviews">({{ doctor.reviewCount || 0 }})</span>
              </div>

              <div class="address-row">
                <svg class="pin-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                <span>{{ doctor.hospitalName || '暂无地址' }}</span>
              </div>
            </div>
          </div>

          <div class="card-calendar-container">
            <div class="calendar-grid">
              
              <div class="day-cell" v-for="(dayObj, dIndex) in headerDays" :key="dIndex">
                <div class="cell-header">
                  <span class="cell-weekday">{{ dayObj.weekday }}</span>
                  <span class="cell-date">{{ dayObj.shortDate }}</span>
                </div>

                <div class="cell-content">
                  <button 
                    v-if="hasSlots(doctor, dayObj.fullDate)"
                    class="slot-btn-yellow" 
                    @click="openBookingModal(doctor, dayObj)"
                  >
                    <div class="slot-count">{{ getRemainingSlotsCount(doctor, dayObj.fullDate) }}</div>
                    <div class="slot-label">号源</div>
                  </button>

                  <div class="slot-placeholder" v-else>
                    暂无
                  </div>
                </div>
              </div>

            </div>

            <div class="more-btn-column">
              <button class="btn-more" @click="openBookingModal(doctor)">
                更多<br>排班
              </button>
            </div>
          </div>

        </div>
      </div>

      <div class="map-column">
        <div class="map-container">
          <div class="map-bg"></div> 
          <div class="map-pin" v-for="(doc, i) in doctors" :key="doc.id" :style="getPinStyle(i)">
            {{ i + 1 }}
          </div>
          <div class="map-controls">
            <button>+</button>
            <button>-</button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
      <div class="modal-content">
        
        <button class="close-btn" @click="closeModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
        </button>

        <h2 class="modal-title">预约</h2>

        <div class="modal-doc-card">
          <img :src="selectedDoctor.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="modal-avatar" />
          <div class="modal-doc-info">
            <h3 class="doc-name">{{ selectedDoctor.name }}</h3>
            <p class="doc-specialty">{{ selectedDoctor.title }}</p>
            
            <div class="rating-row">
              <div class="stars">
                <svg v-for="i in 5" :key="i" class="star-icon" fill="#FFD300" viewBox="0 0 24 24"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
              </div>
              <span class="rating-text">{{ selectedDoctor.rating || '5.00' }} · {{ selectedDoctor.reviewCount || 0 }} 评论</span>
            </div>

            <div class="address-row">
              <svg class="pin-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
              <span class="address-text">{{ selectedDoctor.hospitalName || '暂无地址信息' }}</span>
            </div>
            
            <div class="network-row">
              <svg class="shield-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
              <a href="#">查看是否在医保范围内</a>
            </div>
          </div>
        </div>

        <div class="scheduling-details">
          <h4>预约详情</h4>
          
          <!-- New static display for pre-selected item -->
          <div v-if="preselectedMedicalItemId && selectedMedicalItem" class="fake-select non-interactive">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>
            <span>{{ selectedMedicalItem.name }}</span>
          </div>

          <!-- Original dropdown for when no item is pre-selected -->
          <div v-else class="custom-select-wrapper" ref="medicalItemSelectRef">
            <div class="fake-select" @click="toggleMedicalItemSelect" :class="{ 'active': showMedicalItemSelect }">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"></path></svg>
              <span>{{ selectedMedicalItem?.name || '选择诊疗项目' }}</span>
              <svg class="chevron-down" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </div>
            <div v-if="showMedicalItemSelect" class="custom-select-options">
              <div 
                class="option-item" 
                v-for="item in medicalItems" 
                :key="item.id" 
                @click="selectMedicalItem(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>

          <div class="fake-select">
            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path></svg>
            <span>查看是否在医保范围内</span>
          </div>
        </div>

        <div class="modal-availability">
          <h4>可预约时间</h4>
          <p class="date-label">{{ selectedDateDisplay }}</p>
          
          <div class="time-slots-grid" v-if="selectedDateSlots.length > 0">
            <button 
              class="modal-time-btn" 
              v-for="(slot, index) in selectedDateSlots" 
              :key="index"
              @click="confirmBooking(slot)"
            >
              {{ slot.displayTime }}
            </button>
          </div>
          <div v-else class="no-slots-msg">
            该日期暂无号源，请尝试其他日期。
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn-more-avail">更多可预约时间</button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { searchDoctors, getAllDoctors } from '@/api/doctor';
import { getAvailableDates, getTimeSlots } from '@/api/schedule';
import { getAllMedicalItems } from '@/api/medicalItem'; // Import medical item API
import { createAppointment } from '@/api/appointment'; // Import createAppointment API
import { getAllDepartments } from '@/api/department'; // Import department API
import { ElMessage } from 'element-plus'; // Import ElMessage for notifications

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// --- Constants ---
const DAYS_TO_SHOW = 12;

// --- 基础状态 ---
const searchCondition = ref('');
const searchLocation = ref('');
const doctors = ref([]);
const errorMessage = ref('');
const dayOffset = ref(0); // For date pagination
const medicalItems = ref([]); // State to store medical items
const selectedMedicalItem = ref(null); // To store the selected medical item
const preselectedMedicalItemId = ref(null); // To store ID from route
const showMedicalItemSelect = ref(false); // Controls visibility of custom select options
const medicalItemSelectRef = ref(null); // Ref for the custom select wrapper

// --- 筛选状态 ---
const activeFilter = ref(null); // 当前打开的筛选器 ('time', 'timeslot', 'department', 'distance')
const filterBarRef = ref(null); // 筛选栏的ref
const departments = ref([]); // 科室列表
const selectedDepartmentIds = ref([]); // 选中的科室ID列表
const selectedTimeFilters = ref([]); // 选中的时间筛选
const selectedTimeslotFilters = ref([]); // 选中的时间段筛选
const selectedDistanceFilter = ref(null); // 选中的距离筛选

// --- 搜索建议状态 ---
const showSearchSuggestions = ref(false); // 是否显示搜索建议
const searchBarRef = ref(null); // 搜索栏的ref
const searchSuggestions = ref([]); // 搜索建议列表
const filteredSearchSuggestions = ref([]); // 过滤后的搜索建议

// 筛选选项配置
const timeFilterOptions = [
  { value: 'flexible', label: '时间灵活' }
];

const timeslotFilterOptions = [
  { value: 'morning', label: '早晨 (8:00-12:00)' },
  { value: 'afternoon', label: '下午 (12:00-18:00)' },
  { value: 'evening', label: '晚上 (18:00-22:00)' }
];

const distanceFilterOptions = [
  { value: '1km', label: '1公里以内' },
  { value: '3km', label: '3公里以内' },
  { value: '5km', label: '5公里以内' },
  { value: '10km', label: '10公里以内' },
  { value: 'all', label: '不限距离' }
];

// Methods for custom medical item select
const toggleMedicalItemSelect = () => {
  // Do not allow opening if an item is pre-selected
  if (preselectedMedicalItemId.value) return;
  showMedicalItemSelect.value = !showMedicalItemSelect.value;
};

const selectMedicalItem = (item) => {
  selectedMedicalItem.value = item;
  showMedicalItemSelect.value = false;
  // 当选择检查项目时，更新 preselectedMedicalItemId 并触发搜索
  preselectedMedicalItemId.value = item.id;
  // 触发搜索，根据选择的检查项目筛选医生
  handleSearch();
};

// Handle click outside to close the dropdown
const handleClickOutside = (event) => {
  if (medicalItemSelectRef.value && !medicalItemSelectRef.value.contains(event.target)) {
    showMedicalItemSelect.value = false;
  }
  // 关闭搜索建议下拉列表
  if (searchBarRef.value && !searchBarRef.value.contains(event.target)) {
    showSearchSuggestions.value = false;
  }
};

// Handle click outside to close filter dropdown
const handleFilterClickOutside = (event) => {
  if (filterBarRef.value && !filterBarRef.value.contains(event.target)) {
    activeFilter.value = null;
  }
};

// 切换筛选下拉菜单
const toggleFilterDropdown = (filterType) => {
  if (activeFilter.value === filterType) {
    activeFilter.value = null; // 如果点击的是已打开的，则关闭
  } else {
    activeFilter.value = filterType; // 打开新的筛选器
  }
};

// 清除筛选
const clearTimeFilters = () => {
  selectedTimeFilters.value = [];
};

const clearTimeslotFilters = () => {
  selectedTimeslotFilters.value = [];
};

const clearDepartmentFilters = () => {
  selectedDepartmentIds.value = [];
};

const clearDistanceFilter = () => {
  selectedDistanceFilter.value = null;
};

// 应用筛选
const applyFilters = () => {
  activeFilter.value = null; // 关闭下拉菜单
  // 时间段筛选在前端实现，不需要重新搜索，只需要更新显示
  // 号源数量会根据 selectedTimeslotFilters 自动更新
  // 其他筛选（如科室）需要重新搜索
  // 总是调用 handleSearch，让它自己判断是搜索还是显示所有医生
  handleSearch();
};

// 获取科室列表
const fetchDepartments = async () => {
  try {
    const data = await getAllDepartments();
    departments.value = Array.isArray(data) ? data : (data?.content || []);
    // 更新搜索建议：添加科室选项
    updateSearchSuggestions();
  } catch (e) {
    console.error('Failed to fetch departments:', e);
    departments.value = [];
  }
};

// 更新搜索建议列表
const updateSearchSuggestions = () => {
  const suggestions = [];
  
  // 添加科室选项
  departments.value.forEach(dept => {
    suggestions.push({
      label: dept.name,
      value: dept.name,
      type: '科室',
      id: dept.id
    });
  });
  
  // 添加医疗项目选项
  medicalItems.value.forEach(item => {
    suggestions.push({
      label: item.name,
      value: item.name,
      type: '检查项目',
      id: item.id
    });
  });
  
  // 添加常见疾病/症状（可选）
  const commonConditions = [
    { label: '感冒', type: '常见症状' },
    { label: '发烧', type: '常见症状' },
    { label: '咳嗽', type: '常见症状' },
    { label: '头痛', type: '常见症状' },
    { label: '胃痛', type: '常见症状' },
    { label: '体检', type: '检查项目' },
    { label: '复查', type: '检查项目' }
  ];
  
  suggestions.push(...commonConditions);
  
  searchSuggestions.value = suggestions;
};

// 处理搜索输入
const handleSearchInput = () => {
  const query = searchCondition.value.trim().toLowerCase();
  if (query) {
    filteredSearchSuggestions.value = searchSuggestions.value.filter(item => {
      const label = (item.label || item.name || item).toLowerCase();
      return label.includes(query);
    });
  } else {
    filteredSearchSuggestions.value = [];
  }
};

// 选择搜索建议
const selectSearchSuggestion = (suggestion) => {
  const value = suggestion.label || suggestion.name || suggestion;
  searchCondition.value = value;
  showSearchSuggestions.value = false;
  
  // 如果选择的是科室，也可以设置科室筛选
  if (suggestion.type === '科室' && suggestion.id) {
    selectedDepartmentIds.value = [suggestion.id];
  }
  
  // 如果选择的是医疗项目，设置医疗项目筛选
  if (suggestion.type === '检查项目' && suggestion.id) {
    preselectedMedicalItemId.value = suggestion.id;
    const selectedItem = medicalItems.value.find(item => item.id === suggestion.id);
    if (selectedItem) {
      selectedMedicalItem.value = selectedItem;
    }
  }
  
  // 触发搜索
  handleSearch();
};

onMounted(async () => {
  const { specialty, departmentId, medicalItemId } = route.query;
  preselectedMedicalItemId.value = medicalItemId;

  const params = {};
  if (specialty) {
    searchCondition.value = specialty;
    params.keyword = specialty;
  }
  if (departmentId) {
    params.departmentId = departmentId;
    selectedDepartmentIds.value = [departmentId]; // 设置初始选中的科室
  }
  // 重要：如果有 medicalItemId，必须传递到搜索参数中
  if (medicalItemId) {
    params.medicalItemId = medicalItemId;
  }

  // 如果有任何搜索条件（包括 medicalItemId），使用 fetchDoctors
  if (Object.keys(params).length > 0) {
    fetchDoctors(params);
  } else {
    fetchAllDoctors();
  }
  
  fetchMedicalItems(); // Call to fetch medical items
  fetchDepartments(); // Fetch departments for filter
  
  document.addEventListener('click', handleClickOutside);
  document.addEventListener('click', handleFilterClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
  document.removeEventListener('click', handleFilterClickOutside);
});


const headerDays = computed(() => {
  const days = [];
  const today = new Date();
  for (let i = 0; i < DAYS_TO_SHOW; i++) {
    const targetDate = new Date(today);
    targetDate.setDate(today.getDate() + dayOffset.value + i);
    
    const weekday = targetDate.toLocaleDateString('zh-CN', { weekday: 'short' }); 
    const shortDate = targetDate.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }); 
    
    const year = targetDate.getFullYear();
    const month = String(targetDate.getMonth() + 1).padStart(2, '0');
    const day = String(targetDate.getDate()).padStart(2, '0');
    const fullDate = `${year}-${month}-${day}`;
    const displayDate = targetDate.toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

    days.push({ weekday, shortDate, fullDate, displayDate });
  }
  return days;
});

const dateRangeText = computed(() => {
  if (headerDays.value.length === 0) return '';
  const first = headerDays.value[0].shortDate;
  const last = headerDays.value[headerDays.value.length - 1].shortDate;
  return `${first} - ${last}`;
});

const nextDatePage = () => { dayOffset.value += 12; refreshSlotDisplay(); };
const prevDatePage = () => { if (dayOffset.value >= 12) { dayOffset.value -= 12; refreshSlotDisplay(); }};

// --- 弹窗逻辑 ---
const showModal = ref(false);
const selectedDoctor = ref({});
const selectedDateSlots = ref([]);
const selectedDateDisplay = ref('');
const selectedDateForBooking = ref(''); // 用于传递给预约页面的日期

const openBookingModal = (doctor, dayObj = null) => {
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再进行预约');
    // 保存当前预约信息到 localStorage，登录后可以恢复
    const bookingData = {
      doctorId: doctor.id,
      doctorName: doctor.name,
      doctorTitle: doctor.title,
      departmentName: doctor.departmentName,
      date: dayObj ? dayObj.fullDate : null,
      medicalItemId: preselectedMedicalItemId.value || selectedMedicalItem.value?.id || null
    };
    localStorage.setItem('pendingBooking', JSON.stringify(bookingData));
    // 跳转到登录页
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  
  selectedDoctor.value = doctor;
  
  if (dayObj) {
    selectedDateDisplay.value = dayObj.displayDate;
    selectedDateForBooking.value = dayObj.fullDate;
    selectedDateSlots.value = generateHalfHourSlots(doctor, dayObj.fullDate);
  } else {
    // 找第一个有号源的日期
    let targetDay = headerDays.value[0];
    for (const day of headerDays.value) {
      if (hasSlots(doctor, day.fullDate)) {
        targetDay = day;
        break;
      }
    }
    selectedDateDisplay.value = targetDay.displayDate;
    selectedDateForBooking.value = targetDay.fullDate;
    selectedDateSlots.value = generateHalfHourSlots(doctor, targetDay.fullDate);
  }
  
  showModal.value = true;
  document.body.style.overflow = 'hidden';
};

// 生成每半小时的时间段
const generateHalfHourSlots = (doctor, dateString) => {
  const rawSlots = getSlotsForDate(doctor, dateString);
  if (!rawSlots || rawSlots.length === 0) return [];
  
  const halfHourSlots = [];
  
  for (const slot of rawSlots) {
    let startTime = '09:00';
    let endTime = '12:00';
    
    // 后端返回的是 timeRange 格式，如 "09:00:00 - 12:00:00" 或 "09:00 - 12:00"
    if (slot.timeRange) {
      const parts = slot.timeRange.split('-').map(s => s.trim());
      if (parts.length === 2) {
        startTime = parts[0].substring(0, 5); // 取 HH:MM
        endTime = parts[1].substring(0, 5);
      }
    } else if (slot.startTime && slot.endTime) {
      // 如果有单独的 startTime/endTime 字段
      startTime = String(slot.startTime).substring(0, 5);
      endTime = String(slot.endTime).substring(0, 5);
    }
    
    const [startHour, startMin] = startTime.split(':').map(Number);
    const [endHour, endMin] = endTime.split(':').map(Number);
    
    const startMinutes = startHour * 60 + startMin;
    const endMinutes = endHour * 60 + endMin;
    
    // 每30分钟生成一个时间槽
    for (let mins = startMinutes; mins < endMinutes; mins += 30) {
      const hour = Math.floor(mins / 60);
      const minute = mins % 60;
      const displayTime = `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
      
      halfHourSlots.push({
        displayTime,
        originalSlotId: slot.id,
        period: slot.period,
        remainingSlots: slot.remainingSlots
      });
    }
  }
  
  return halfHourSlots;
};

const closeModal = () => {
  showModal.value = false;
  document.body.style.overflow = '';
};

const confirmBooking = (slot) => {
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    closeModal();
    ElMessage.warning('请先登录后再进行预约');
    // 保存当前预约信息到 localStorage，登录后可以恢复
    const bookingData = {
      doctorId: selectedDoctor.value.id,
      doctorName: selectedDoctor.value.name,
      doctorTitle: selectedDoctor.value.title,
      departmentName: selectedDoctor.value.departmentName,
      date: selectedDateForBooking.value,
      time: slot.displayTime,
      slotId: slot.originalSlotId,
      period: slot.period,
      medicalItemId: selectedMedicalItem.value?.id || preselectedMedicalItemId.value || null
    };
    localStorage.setItem('pendingBooking', JSON.stringify(bookingData));
    // 跳转到登录页，并保存当前路由以便登录后返回
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    });
    return;
  }
  
  closeModal();
  // 已登录，跳转到预约信息填写页面
  router.push({
    path: '/booking/info',
    query: {
      doctorId: selectedDoctor.value.id,
      doctorName: selectedDoctor.value.name,
      doctorTitle: selectedDoctor.value.title,
      departmentName: selectedDoctor.value.departmentName,
      date: selectedDateForBooking.value,
      time: slot.displayTime,
      slotId: slot.originalSlotId,
      period: slot.period,
      medicalItemId: selectedMedicalItem.value?.id || preselectedMedicalItemId.value || null
    }
  });
};

// --- 数据 Helper ---
const getSlotsForDate = (doctor, dateString) => {
  if (!doctor.availabilityMap) return [];
  return doctor.availabilityMap[dateString] || [];
};

// 将前端筛选选项映射到后端时间段枚举
const mapTimeslotFilterToPeriod = (filterValue) => {
  const mapping = {
    'morning': 'MORNING',
    'afternoon': 'AFTERNOON',
    'evening': 'EVENING'
  };
  return mapping[filterValue];
};

// 获取某日期的剩余号源总数（考虑时间段筛选）
const getRemainingSlotsCount = (doctor, dateString) => {
  const slots = getSlotsForDate(doctor, dateString);
  if (!slots || slots.length === 0) return 0;
  
  // 如果选择了时间段筛选，只计算选中时间段的号源
  if (selectedTimeslotFilters.value.length > 0) {
    const selectedPeriods = selectedTimeslotFilters.value.map(mapTimeslotFilterToPeriod);
    return slots
      .filter(slot => {
        // slot.period 是字符串（如 'MORNING'），从后端 TimeSlotDTO 返回
        const period = slot.period;
        return selectedPeriods.includes(period);
      })
      .reduce((total, slot) => {
        return total + (slot.remainingSlots || 0);
      }, 0);
  }
  
  // 如果没有选择时间段筛选，计算所有时间段的剩余号源总和
  return slots.reduce((total, slot) => {
    return total + (slot.remainingSlots || 0);
  }, 0);
};

const hasSlots = (doctor, dateString) => {
  return getRemainingSlotsCount(doctor, dateString) > 0;
};

const formatTime = (slot) => {
  if (!slot) return '';
  if (typeof slot === 'string') return slot;
  if (slot.timeRange) return slot.timeRange.split('-')[0].trim();
  if (slot.startTime) return slot.startTime;
  return '可约';
};

const getPinStyle = (index) => {
  const top = 20 + (index * 15) % 60; 
  const left = 20 + (index * 25) % 60;
  return { top: `${top}%`, left: `${left}%` };
};

// --- API ---
const refreshSlotDisplay = async () => {
  for (const doctor of doctors.value) { await fetchSlotsForVisibleDays(doctor); }
};

const fetchMedicalItems = async () => {
  try {
    const data = await getAllMedicalItems();
    // request.js 响应拦截器已经解析了数据，直接返回的是 data 部分
    medicalItems.value = Array.isArray(data) ? data : [];
    if (preselectedMedicalItemId.value) {
      const preselected = medicalItems.value.find(item => item.id === preselectedMedicalItemId.value);
      if (preselected) {
        selectedMedicalItem.value = preselected;
      }
    }
    // 更新搜索建议：添加医疗项目选项
    updateSearchSuggestions();
  } catch (e) {
    console.error('Failed to fetch medical items:', e);
  }
};

// 处理筛选按钮点击（用于"更多筛选"等尚未实现的筛选器）
const handleFilterClick = (filterType) => {
  console.log('Filter clicked:', filterType);
  if (filterType === 'more') {
    // 更多筛选功能待实现
    ElMessage.info('更多筛选功能开发中');
  }
};

const handleSearch = () => {
  console.log('handleSearch called'); // 调试信息
  dayOffset.value = 0;
  const params = {};
  if (searchCondition.value && searchCondition.value.trim()) {
    params.keyword = searchCondition.value.trim();
  }
  // 如果有 medicalItemId（来自路由或用户选择），必须包含在搜索参数中
  if (preselectedMedicalItemId.value || selectedMedicalItem.value?.id) {
    params.medicalItemId = preselectedMedicalItemId.value || selectedMedicalItem.value?.id;
  }
  
  // 添加科室筛选
  if (selectedDepartmentIds.value.length > 0) {
    // 如果有多个科室，使用第一个（后端当前只支持单个科室筛选）
    // 如果需要支持多科室，需要修改后端API
    params.departmentId = selectedDepartmentIds.value[0];
  }
  
  // TODO: 添加时间段筛选（需要后端支持）
  // if (selectedTimeslotFilters.value.length > 0) {
  //   params.timeslots = selectedTimeslotFilters.value;
  // }
  
  // TODO: 添加距离筛选（需要后端支持）
  // if (selectedDistanceFilter.value) {
  //   params.distance = selectedDistanceFilter.value;
  // }
  
  console.log('Search params:', params); // 调试信息
  
  // 只要有参数就调用搜索API，否则调用获取所有医生
  if (Object.keys(params).length > 0) {
    fetchDoctors(params);
  } else {
    fetchAllDoctors();
  }
};

const fetchAllDoctors = async () => {
  try {
    const data = await getAllDoctors();
    // request.js 响应拦截器已经解析了数据
    processDoctorsData(data?.content || []);
  } catch (e) { console.error(e); errorMessage.value = '加载失败'; }
};

const fetchDoctors = async (params) => {
  try {
    console.log('fetchDoctors called with params:', params); // 调试信息
    errorMessage.value = '';
    const data = await searchDoctors(params);
    console.log('Search doctors response:', data); // 调试信息
    // request.js 响应拦截器已经解析了数据
    const doctorsList = data?.content || data || [];
    processDoctorsData(doctorsList);
  } catch (e) { 
    console.error('Error fetching doctors:', e); 
    errorMessage.value = '搜索失败：' + (e.message || '未知错误');
  }
};

const processDoctorsData = async (fetchedDoctors) => {
  doctors.value = fetchedDoctors.map(doc => ({ ...doc, availabilityMap: {} }));
  for (const doctor of doctors.value) { await fetchSlotsForVisibleDays(doctor); }
};

const fetchSlotsForVisibleDays = async (doctor) => {
  try {
    const availableDates = await getAvailableDates(doctor.id); 
    // request.js 响应拦截器已经解析了数据
    const dates = Array.isArray(availableDates) ? availableDates : [];
    for (const dayObj of headerDays.value) {
      const dateStr = dayObj.fullDate;
      if (doctor.availabilityMap[dateStr]) continue;
      const isAvailable = dates.some(d => (d.date || d) === dateStr && d.available);
      if (isAvailable) {
        const slotsData = await getTimeSlots(doctor.id, dateStr);
        doctor.availabilityMap[dateStr] = Array.isArray(slotsData) ? slotsData : [];
      } else {
        doctor.availabilityMap[dateStr] = [];
      }
    }
  } catch (e) { console.error(e); }
};
</script>

<style scoped>
/* 全局样式 */
.search-results-page {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  color: #2A2A2A;
  background-color: #fff;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* === 顶部导航 === */
.top-bar { height: 72px; border-bottom: 1px solid #E0E0E0; display: flex; justify-content: space-between; align-items: center; padding: 0 24px; position: sticky; top: 0; background: #fff; z-index: 100; }
.nav-left { display: flex; align-items: center; gap: 20px; width: 60%; }
.logo-z { width: 32px; height: 32px; background: #FFD300; border-radius: 0 50% 50% 50%; display: flex; justify-content: center; align-items: center; font-weight: 800; font-size: 18px; }
.search-bar-composite { display: flex; align-items: center; border: 1px solid #CCC; height: 48px; width: 100%; max-width: 800px; border-radius: 4px; }
.input-group { flex: 1; display: flex; align-items: center; padding: 0 12px; height: 100%; background: #F8F8F8; }
.input-group:hover { background: #fff; }
.input-group input { border: none; width: 100%; outline: none; font-size: 14px; background: transparent; }
.search-input-wrapper {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}
.search-suggestions-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #DDD;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  max-height: 300px;
  overflow-y: auto;
  z-index: 1001;
  margin-top: 4px;
}
.suggestion-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #F0F0F0;
}
.suggestion-item:last-child {
  border-bottom: none;
}
.suggestion-item:hover {
  background: #F8F8F8;
}
.suggestion-icon {
  width: 16px;
  height: 16px;
  margin-right: 12px;
  color: #666;
  flex-shrink: 0;
}
.suggestion-text {
  flex: 1;
  font-size: 14px;
  color: #2A2A2A;
}
.suggestion-type {
  font-size: 12px;
  color: #999;
  background: #F0F0F0;
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 8px;
}
.icon-input { width: 18px; margin-right: 8px; color: #666; }
.divider { width: 1px; height: 60%; background: #DDD; }
.search-btn { width: 48px; height: 100%; background: #FFD300; border: none; cursor: pointer; display: flex; justify-content: center; align-items: center; transition: background 0.2s; }
.search-btn:hover { background: #FFC000; }
.search-btn:active { background: #FFB000; }
.nav-right { display: flex; gap: 16px; align-items: center; }
.nav-link { text-decoration: none; color: #2A2A2A; font-weight: 600; font-size: 14px; }
.btn-signup { padding: 8px 16px; background: #FFD300; border: none; border-radius: 4px; font-weight: 700; cursor: pointer; text-decoration: none; color: #000; font-size: 14px; }

/* === 筛选栏 === */
.filter-bar { 
  padding: 12px 24px; 
  border-bottom: 1px solid #EEE; 
  display: flex; 
  gap: 10px; 
  background: #fff; 
  position: relative;
  flex-wrap: wrap;
}
.filter-item-wrapper {
  position: relative;
}

.filter-pill { 
  padding: 8px 16px; 
  border: 1px solid #CCC; 
  border-radius: 20px; 
  background: #fff; 
  font-size: 14px; 
  cursor: pointer; 
  font-weight: 500; 
  transition: all 0.2s; 
  user-select: none; 
}
.filter-pill:hover { 
  background: #F8F8F8; 
  border-color: #999; 
}
.filter-pill.active { 
  background: #2A2A2A; 
  color: #fff; 
  border-color: #2A2A2A; 
}
.filter-pill:active { 
  transform: scale(0.98); 
}

.filter-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  background: #fff;
  border: 1px solid #DDD;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 280px;
  max-width: 320px;
  z-index: 1000;
  padding: 16px;
}

.filter-options {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.filter-option {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  user-select: none;
}

.filter-option input[type="checkbox"],
.filter-option input[type="radio"] {
  margin-right: 12px;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.option-label {
  flex: 1;
  font-size: 14px;
  color: #2A2A2A;
}

.filter-actions {
  display: flex;
  gap: 8px;
  border-top: 1px solid #EEE;
  padding-top: 16px;
}

.btn-clear {
  flex: 1;
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #CCC;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  color: #2A2A2A;
  transition: all 0.2s;
}

.btn-clear:hover {
  background: #F8F8F8;
  border-color: #999;
}

.btn-apply {
  flex: 1;
  padding: 8px 16px;
  background: #FFD300;
  border: 1px solid #FFD300;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  color: #2A2A2A;
  transition: all 0.2s;
}

.btn-apply:hover {
  background: #FFC000;
  border-color: #FFC000;
}

/* === 主体布局 === */
.main-layout { display: flex; flex: 1; overflow: hidden; }
.results-column { flex: 65%; overflow-y: auto; padding: 24px; }
.results-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.results-header h2 { font-size: 20px; font-weight: 700; margin: 0; }
.date-controls { display: flex; align-items: center; gap: 12px; }
.nav-arrow { background: #F0F0F0; border: none; width: 28px; height: 28px; border-radius: 50%; cursor: pointer; font-weight: bold; }

/* === 医生卡片 === */
.doctor-card { display: flex; border: 1px solid #E0E0E0; border-radius: 4px; margin-bottom: 24px; background: #fff; }
.card-info { flex: 0 0 35%; padding: 20px; border-right: 1px solid #F0F0F0; display: flex; flex-direction: column; }
.info-top { display: flex; justify-content: space-between; margin-bottom: 12px; }
.avatar { width: 64px; height: 64px; border-radius: 50%; object-fit: cover; border: 1px solid #F0F0F0; }
.badge-sponsored { font-size: 11px; color: #666; display: flex; align-items: center; gap: 4px; text-transform: uppercase; }
.doc-name { font-size: 16px; font-weight: 700; margin: 0 0 4px; color: #2A2A2A; text-decoration: underline; cursor: pointer; }
.doc-title { font-size: 14px; color: #2A2A2A; margin: 0 0 2px; }
.doc-dept { font-size: 13px; color: #666; margin-bottom: 8px; }
.rating-row { display: flex; align-items: center; font-size: 12px; margin-bottom: 12px; }
.stars { color: #FFD300; display: flex; margin-right: 6px; } .star-icon { width: 14px; }
.address-row { font-size: 13px; color: #2A2A2A; display: flex; gap: 6px; align-items: flex-start; margin-top: auto; } .pin-icon { width: 16px; flex-shrink: 0; }

/* === 右侧排期 (12天网格) === */
.card-calendar-container { flex: 1; background-color: #FAFAFA; display: flex; }
.calendar-grid {
  flex: 1; 
  display: grid; 
  grid-template-columns: repeat(6, 1fr); /* 6列 */
  grid-auto-rows: minmax(100px, auto); /* 自动多行 */
  border-right: 1px solid #E0E0E0;
}
.day-cell { border-right: 1px solid #E0E0E0; border-bottom: 1px solid #E0E0E0; display: flex; flex-direction: column; min-height: 90px; }
.day-cell:nth-child(6n) { border-right: none; } /* 每行第6个去边框 */

.cell-header { background-color: #F5F5F5; padding: 6px 0; text-align: center; border-bottom: 1px solid #E0E0E0; }
.cell-weekday { display: block; font-size: 11px; color: #666; }
.cell-date { display: block; font-size: 12px; font-weight: 700; color: #333; }

.cell-content { flex: 1; display: flex; align-items: center; justify-content: center; padding: 6px; background-color: #fff; }

/* 黄色概览块 */
.slot-btn-yellow {
  width: 100%; height: 100%; background-color: #FFD300; border: none; border-radius: 4px;
  cursor: pointer; display: flex; flex-direction: column; justify-content: center; align-items: center;
  padding: 4px; transition: background 0.2s;
}
.slot-btn-yellow:hover { background-color: #F4CA00; }
.slot-count { font-weight: 800; font-size: 14px; color: #000; }
.slot-label { font-size: 10px; color: #333; }
.slot-placeholder { color: #999; font-size: 11px; }

/* 更多按钮列 */
.more-btn-column { width: 50px; display: flex; align-items: center; justify-content: center; background: #fff; border-left: 1px solid #EEE; }
.btn-more { border: 1px solid #DDD; background: #fff; padding: 8px 4px; border-radius: 4px; font-size: 12px; font-weight: 600; cursor: pointer; white-space: pre-wrap; }

/* === 地图 === */
.map-column { flex: 35%; background: #E5E3DF; position: relative; border-left: 1px solid #DDD; }
.map-container { height: 100%; width: 100%; position: relative; }
.map-bg { width: 100%; height: 100%; background-image: url('https://upload.wikimedia.org/wikipedia/commons/e/ec/Map_placeholder.svg'); background-size: cover; opacity: 0.6; }
.map-pin { position: absolute; width: 28px; height: 28px; background: #2A2A2A; color: #fff; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-size: 12px; font-weight: bold; cursor: pointer; }
.map-controls { position: absolute; top: 16px; right: 16px; background: #fff; border-radius: 4px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
.map-controls button { display: block; width: 32px; height: 32px; border: none; background: #fff; font-size: 18px; cursor: pointer; border-bottom: 1px solid #EEE; }

/* === 弹窗样式 (Zocdoc 风格复刻) === */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background-color: rgba(0, 0, 0, 0.6); display: flex; justify-content: center; align-items: center; z-index: 200; padding: 20px;
}
.modal-content {
  background: #fff; width: 550px; max-width: 100%; border-radius: 8px; padding: 0; position: relative;
  max-height: 90vh; overflow-y: auto; box-shadow: 0 8px 30px rgba(0,0,0,0.15); display: flex; flex-direction: column;
}
.close-btn { position: absolute; top: 16px; right: 16px; background: none; border: none; cursor: pointer; color: #666; z-index: 10; padding: 4px; }
.modal-title { font-size: 22px; font-weight: 700; color: #2A2A2A; padding: 32px 32px 16px 32px; margin: 0; }

.modal-doc-card { display: flex; gap: 20px; padding: 0 32px 24px 32px; }
.modal-avatar { width: 80px; height: 80px; border-radius: 50%; object-fit: cover; border: 1px solid #EEE; flex-shrink: 0; }
.modal-doc-info { display: flex; flex-direction: column; }
.modal-doc-info .doc-name { font-size: 18px; font-weight: 700; margin: 0 0 4px 0; color: #2A2A2A; text-decoration: none; }
.modal-doc-info .doc-specialty { font-size: 14px; color: #2A2A2A; margin: 0 0 8px 0; }
.modal-doc-info .rating-row { display: flex; align-items: center; gap: 6px; margin-bottom: 8px; }
.modal-doc-info .star-icon { width: 14px; height: 14px; }
.modal-doc-info .rating-text { font-size: 12px; font-weight: 600; color: #2A2A2A; }
.modal-doc-info .address-row { display: flex; align-items: flex-start; gap: 6px; margin-bottom: 8px; }
.modal-doc-info .pin-icon { width: 16px; height: 16px; color: #666; margin-top: 1px; flex-shrink: 0; }
.modal-doc-info .address-text { font-size: 13px; color: #666; line-height: 1.4; }
.modal-doc-info .network-row { display: flex; align-items: center; gap: 6px; }
.modal-doc-info .shield-icon { width: 14px; height: 14px; color: #2A2A2A; }
.modal-doc-info a { font-size: 13px; color: #2A2A2A; font-weight: 600; text-decoration: underline; }

.scheduling-details { padding: 0 32px 24px 32px; }
.scheduling-details h4 { font-size: 16px; font-weight: 600; margin: 0 0 12px 0; color: #2A2A2A; }
.fake-select { display: flex; align-items: center; border: 1px solid #CCC; border-radius: 4px; padding: 10px 12px; margin-bottom: 12px; cursor: pointer; background: #FFF; }
.fake-select:hover { border-color: #999; }
.fake-select .input-icon { width: 18px; height: 18px; color: #666; margin-right: 12px; }
.fake-select span { font-size: 14px; color: #2A2A2A; flex: 1; }
.fake-select .chevron-down { width: 16px; height: 16px; color: #666; }

/* Custom Select Dropdown Styles */
.custom-select-wrapper {
  position: relative; /* Essential for positioning the options list */
  width: 100%;
  margin-bottom: 12px; /* Maintain spacing */
}

.custom-select-options {
  position: absolute;
  top: 100%; /* Position below the fake-select */
  left: 0;
  right: 0;
  background-color: #fff;
  border: 1px solid #CCC;
  border-radius: 4px;
  z-index: 10; /* Ensure it appears above other content */
  max-height: 200px; /* Scroll for many options */
  overflow-y: auto;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1); /* Subtle shadow */
  margin-top: 4px; /* Small gap between select and options */
}

.option-item {
  padding: 10px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #2A2A2A;
}

.option-item:hover {
  background-color: #f0f0f0; /* Highlight on hover */
}

.modal-availability { padding: 0 32px 24px 32px; }
.modal-availability h4 { font-size: 16px; font-weight: 600; margin: 0 0 8px 0; color: #2A2A2A; }
.date-label { font-size: 14px; font-weight: 600; color: #2A2A2A; margin-bottom: 12px; }
.time-slots-grid { display: flex; flex-wrap: wrap; gap: 10px; }
.modal-time-btn { background-color: #FFD300; border: none; border-radius: 4px; padding: 10px 16px; font-size: 14px; font-weight: 600; color: #2A2A2A; cursor: pointer; transition: background 0.2s; min-width: 80px; text-align: center; }
.modal-time-btn:hover { background-color: #F4CA00; }
.no-slots-msg { font-size: 14px; color: #666; padding: 10px 0; }

.modal-footer { padding: 16px 32px 32px 32px; border-top: 1px solid #F0F0F0; }
.btn-more-avail { background: none; border: none; font-size: 14px; font-weight: 600; color: #2A2A2A; text-decoration: underline; cursor: pointer; padding: 0; }

@media (max-width: 1000px) {
  .map-column { display: none; }
  .card-calendar-container { display: none; } 
  .card-info { flex: 1; border-right: none; }
}
</style>