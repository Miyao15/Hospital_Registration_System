<template>
  <div class="search-results-page page-animate">
    
    <header class="top-bar slide-down">
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
              class="input-focus"
            />
            <!-- 搜索建议下拉列表 -->
            <div v-if="showSearchSuggestions && (filteredSearchSuggestions.length > 0 || searchSuggestions.length > 0)" class="search-suggestions-dropdown scale-in">
              <div 
                class="suggestion-item stagger-item" 
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
            <select v-model="selectedRegion" @change="handleRegionChange" class="input-focus" style="border: none; outline: none; background: transparent; width: 100%;">
              <option value="">选择地区</option>
              <option value="华北">华北</option>
              <option value="华东">华东</option>
              <option value="华南">华南</option>
              <option value="华中">华中</option>
              <option value="西南">西南</option>
              <option value="西北">西北</option>
              <option value="东北">东北</option>
            </select>
          </div>
          <div class="divider"></div>
          <div class="input-group">
            <select v-model="selectedDistrict" @change="handleDistrictChange" class="input-focus" style="border: none; outline: none; background: transparent; width: 100%;">
              <option value="">选择区域</option>
              <option value="和平区">和平区</option>
              <option value="河东区">河东区</option>
              <option value="河西区">河西区</option>
              <option value="南开区">南开区</option>
              <option value="河北区">河北区</option>
              <option value="红桥区">红桥区</option>
              <option value="东丽区">东丽区</option>
              <option value="西青区">西青区</option>
              <option value="津南区">津南区</option>
              <option value="北辰区">北辰区</option>
              <option value="武清区">武清区</option>
              <option value="宝坻区">宝坻区</option>
              <option value="滨海新区">滨海新区</option>
              <option value="宁河区">宁河区</option>
              <option value="静海区">静海区</option>
              <option value="蓟州区">蓟州区</option>
            </select>
          </div>
          <button class="search-btn btn-hover ripple" @click="handleSearch">
            <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="3" fill="none"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
          </button>
        </div>
      </div>
      <div class="nav-right">
        <button class="back-btn" @click="handleBack" title="返回">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <line x1="19" y1="12" x2="5" y2="12"></line>
            <polyline points="12 19 5 12 12 5"></polyline>
          </svg>
          返回
        </button>
        <template v-if="userStore.isLoggedIn">
          <span class="user-greeting">Hi, {{ userStore.userInfo?.username }}</span>
          <a href="#" class="nav-link link-underline" @click.prevent="userStore.logout()">退出</a>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link link-underline">登录</router-link>
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
        <div class="map-container" id="amap-container">
          <!-- 高德地图将渲染在这里 -->
          <div v-if="!mapLoaded" class="map-loading">
            <div class="loading-spinner"></div>
            <span>地图加载中...</span>
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
        </div>

        <div class="modal-availability">
          <h4>可预约时间</h4>
          <p class="date-label">{{ selectedDateDisplay }}</p>
          
          <div class="time-slots-grid" v-if="selectedDateSlots.length > 0">
            <button 
              class="modal-time-btn" 
              :class="{ 
                'disabled': !slot.isAvailable,
                'low-slots': slot.isAvailable && slot.remainingSlots === 1
              }"
              v-for="(slot, index) in selectedDateSlots" 
              :key="index"
              @click="slot.isAvailable ? confirmBooking(slot) : null"
              :disabled="!slot.isAvailable"
            >
              <span class="time-text">{{ slot.displayTime }}</span>
              <span v-if="slot.isAvailable" class="slots-count">{{ slot.remainingSlots }}/{{ slot.totalSlots }}</span>
              <span v-else class="slots-status">已满</span>
            </button>
          </div>
          <div v-else class="no-slots-msg">
            该日期暂无号源，请尝试其他日期。
          </div>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
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

const handleBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/landing');
  }
};

// --- Constants ---
const DAYS_TO_SHOW = 12;
const AMAP_KEY = '7621055765e5ea433a56367bccc10c7e'; // 高德Web端JS API Key

// --- 基础状态 ---
const searchCondition = ref('');
const searchLocation = ref('');
const selectedRegion = ref(''); // 选择的地区
const selectedCity = ref('天津市'); // 默认城市为天津市
const selectedDistrict = ref(''); // 选择的区
const doctors = ref([]);
const errorMessage = ref('');
const dayOffset = ref(0); // For date pagination
const medicalItems = ref([]); // State to store medical items
const selectedMedicalItem = ref(null); // To store the selected medical item
const preselectedMedicalItemId = ref(null); // To store ID from route
const showMedicalItemSelect = ref(false); // Controls visibility of custom select options
const medicalItemSelectRef = ref(null); // Ref for the custom select wrapper

// --- 地图状态 ---
const mapLoaded = ref(false);
let mapInstance = null;
let markers = [];

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

// --- 高德地图初始化 ---
const initAMap = () => {
  // 设置安全密钥（高德地图 JS API 2.0 必需）
  // 注意：需要在高德控制台获取安全密钥
  window._AMapSecurityConfig = {
    securityJsCode: '3b9c3c865a78dc8831faac2225035022'
  };
  
  // 动态加载高德地图JS API
  if (window.AMap) {
    createMap();
    return;
  }
  
  const script = document.createElement('script');
  script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}`;
  script.onload = () => {
    createMap();
  };
  script.onerror = () => {
    console.error('高德地图加载失败');
    mapLoaded.value = true; // 即使失败也标记为已加载，避免一直显示loading
  };
  document.head.appendChild(script);
};

const createMap = () => {
  nextTick(() => {
    const container = document.getElementById('amap-container');
    if (!container || !window.AMap) return;
    
    // 默认中心点（天津医科大学总医院）
    const DEFAULT_CENTER = [117.195907, 39.104027];
    
    // 创建地图实例
    mapInstance = new window.AMap.Map('amap-container', {
      zoom: 15,
      center: DEFAULT_CENTER,
      mapStyle: 'amap://styles/light' // 浅色主题
    });
    
    mapLoaded.value = true;
    
    // 如果有医生数据，添加标记
    if (doctors.value.length > 0) {
      addDoctorMarkers();
    }
  });
};

// 添加医生位置标记（使用真实的医院坐标）
const addDoctorMarkers = () => {
  if (!mapInstance || !window.AMap) return;
  
  // 清除旧标记
  markers.forEach(m => mapInstance.remove(m));
  markers = [];
  
  // 默认中心点（天津医科大学总医院）
  const DEFAULT_CENTER = [117.195907, 39.104027];
  
  // 按医院分组医生
  const hospitalGroups = {};
  doctors.value.forEach((doc, index) => {
    // 使用后端返回的医院位置，如果没有则使用默认位置
    const hospitalKey = doc.hospitalId || 'default';
    if (!hospitalGroups[hospitalKey]) {
      // 优先使用医生DTO中的医院坐标
      const hospitalLng = doc.hospitalLongitude;
      const hospitalLat = doc.hospitalLatitude;
      
      hospitalGroups[hospitalKey] = {
        center: hospitalLng && hospitalLat 
          ? [hospitalLng, hospitalLat] 
          : DEFAULT_CENTER,
        name: doc.hospitalName || '未知医院',
        address: doc.hospitalAddress || '',
        city: doc.hospitalCity || '',
        region: doc.hospitalRegion || '',
        doctors: []
      };
    }
    hospitalGroups[hospitalKey].doctors.push({ ...doc, originalIndex: index });
  });
  
  // 为每个医院添加一个标记（显示医院位置）
  Object.values(hospitalGroups).forEach((group, groupIndex) => {
    // 只在医院位置添加一个标记，而不是为每个医生添加
    const marker = new window.AMap.Marker({
      position: group.center,
      content: `<div class="custom-marker" style="background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);">🏥</div>`,
      offset: new window.AMap.Pixel(-15, -15),
      title: group.name
    });
    
    // 点击标记显示医院信息和该医院的医生列表
    marker.on('click', () => {
      const doctorsList = group.doctors.map(doc => 
        `<div style="padding: 5px 0; border-bottom: 1px solid #eee;">
          <strong>${doc.name}</strong> - ${doc.title || ''} - ${doc.departmentName || ''}
        </div>`
      ).join('');
      
      const infoWindow = new window.AMap.InfoWindow({
        content: `
          <div style="padding: 10px; min-width: 200px; max-height: 300px; overflow-y: auto;">
            <h4 style="margin: 0 0 8px; font-size: 16px; color: #2A2A2A;">${group.name}</h4>
            <p style="margin: 0 0 8px; font-size: 12px; color: #666;">${group.address || ''}</p>
            ${group.city ? `<p style="margin: 0 0 8px; font-size: 12px; color: #999;">📍 ${group.city}${group.region ? ' · ' + group.region : ''}</p>` : ''}
            <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
              <p style="margin: 0 0 5px; font-size: 12px; font-weight: bold; color: #333;">该医院医生 (${group.doctors.length}位):</p>
              ${doctorsList}
            </div>
          </div>
        `,
        offset: new window.AMap.Pixel(0, -20)
      });
      infoWindow.open(mapInstance, marker.getPosition());
    });
    
    markers.push(marker);
    mapInstance.add(marker);
  });
  
  // 自动调整视野以包含所有标记
  if (markers.length > 0) {
    mapInstance.setFitView(markers, false, [50, 50, 50, 50]);
  } else {
    // 如果没有标记，使用默认中心点
    mapInstance.setCenter(DEFAULT_CENTER);
    mapInstance.setZoom(15);
  }
};

// 地区选择变化处理
const handleRegionChange = () => {
  // 地区变化时自动搜索
  handleSearch();
};

// 区选择变化处理
const handleDistrictChange = () => {
  // 区变化时自动搜索
  handleSearch();
};

// 监听医生数据变化，更新地图标记
watch(doctors, () => {
  if (mapLoaded.value && mapInstance) {
    addDoctorMarkers();
  }
}, { deep: true });

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
  const { specialty, keyword, location, district, departmentId, medicalItemId, minRating, priorityDoctorId } = route.query;
  preselectedMedicalItemId.value = medicalItemId;

  const params = {};
  // 支持 specialty 和 keyword 两种参数名
  const searchKeyword = keyword || specialty;
  if (searchKeyword) {
    searchCondition.value = searchKeyword;
    params.keyword = searchKeyword;
  }
  // 支持地区搜索
  if (location) {
    searchLocation.value = location;
  }
  // 支持区搜索
  if (district) {
    selectedDistrict.value = district;
    params.district = district;
  }
  // 只有在有明确搜索条件时才设置默认城市，如果只有priorityDoctorId或只有minRating，不设置城市限制
  const hasOtherSearchConditions = searchKeyword || district || location || departmentId || medicalItemId;
  // 如果只有minRating或只有priorityDoctorId，不设置城市限制，以获取所有医生
  const onlyMinRating = minRating && !hasOtherSearchConditions && !priorityDoctorId;
  const onlyPriorityDoctor = priorityDoctorId && !hasOtherSearchConditions && !minRating;
  if (hasOtherSearchConditions && !onlyMinRating && !onlyPriorityDoctor) {
    params.city = '天津市';
  }
  if (departmentId) {
    params.departmentId = departmentId;
    selectedDepartmentIds.value = [departmentId]; // 设置初始选中的科室
  }
  // 重要：如果有 medicalItemId，必须传递到搜索参数中
  if (medicalItemId) {
    params.medicalItemId = medicalItemId;
  }
  // 保存评分筛选和优先医生ID（用于前端筛选和排序）
  if (minRating) {
    params.minRating = minRating;
  }
  if (priorityDoctorId) {
    params.priorityDoctorId = priorityDoctorId;
  }

  // 如果只有priorityDoctorId或只有minRating，使用getAllDoctors获取所有医生（不限制城市）
  // onlyPriorityDoctor 和 onlyMinRating 已在上面定义
  if (onlyPriorityDoctor) {
    // 将priorityDoctorId和minRating保存到params中，供processDoctorsData使用
    const allParams = { priorityDoctorId };
    if (minRating) {
      allParams.minRating = minRating;
    }
    fetchAllDoctorsWithPriority(allParams);
  } else if (onlyMinRating) {
    // 只有minRating时，获取所有医生，然后在前端筛选
    const allParams = { minRating };
    fetchAllDoctorsWithPriority(allParams);
  } else if (Object.keys(params).length > 0) {
    fetchDoctors(params);
  } else {
    fetchAllDoctors();
  }
  
  fetchMedicalItems(); // Call to fetch medical items
  fetchDepartments(); // Fetch departments for filter
  initAMap(); // 初始化高德地图
  
  document.addEventListener('click', handleClickOutside);
  document.addEventListener('click', handleFilterClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
  document.removeEventListener('click', handleFilterClickOutside);
  // 销毁地图实例
  if (mapInstance) {
    mapInstance.destroy();
    mapInstance = null;
  }
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
  // 允许未登录用户打开预约模态框，选择时间后再提示登录
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
// 每个时间点（30分钟）固定2个号源
const SLOTS_PER_HALF_HOUR = 2;

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
    
    // 计算该时间段有多少个30分钟时间段
    const halfHourCount = Math.floor((endMinutes - startMinutes) / 30);
    
    // 计算每个时间点应该有多少剩余号源
    // 如果后端剩余号源为0，所有时间点都不可用
    // 否则，平均分配剩余号源到各个时间点
    let remainingForPeriod = slot.remainingSlots || 0;
    const slotsPerTimeSlot = Math.max(0, Math.floor(remainingForPeriod / halfHourCount));
    const extraSlots = remainingForPeriod % halfHourCount; // 余数分配给前面的时间点
    
    // 每30分钟生成一个时间槽
    for (let i = 0; i < halfHourCount; i++) {
      const mins = startMinutes + i * 30;
      const hour = Math.floor(mins / 60);
      const minute = mins % 60;
      const displayTime = `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
      
      // 每个时间点的剩余号源 = 基础号源数 + 是否有余数
      // 前面几个时间点可能会多分配1个号源（如果有余数）
      const timeSlotRemaining = i < extraSlots 
        ? Math.min(SLOTS_PER_HALF_HOUR, slotsPerTimeSlot + 1)
        : Math.min(SLOTS_PER_HALF_HOUR, slotsPerTimeSlot);
      
      halfHourSlots.push({
        displayTime,
        originalSlotId: slot.id,
        period: slot.period,
        remainingSlots: timeSlotRemaining,
        totalSlots: SLOTS_PER_HALF_HOUR,
        isAvailable: timeSlotRemaining > 0
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
  // 检查是否登录 - 在选择时间后才检查登录状态
  if (!userStore.isLoggedIn) {
    closeModal();
    // 保存完整的预约信息到 localStorage，包括选择的医生、日期和时间
    const bookingData = {
      doctorId: selectedDoctor.value.id,
      doctorName: selectedDoctor.value.name,
      doctorTitle: selectedDoctor.value.title,
      departmentName: selectedDoctor.value.departmentName,
      date: selectedDateForBooking.value,
      time: slot.displayTime,
      slotId: slot.originalSlotId,
      period: slot.period,
      medicalItemId: selectedMedicalItem.value?.id || preselectedMedicalItemId.value || null,
      returnPath: '/search-results' // 登录后返回的路径
    };
    localStorage.setItem('pendingBooking', JSON.stringify(bookingData));
    // 跳转到登录页，登录后会自动恢复预约信息
    router.push('/login');
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
  
  // 添加地区筛选
  if (selectedRegion.value) {
    params.region = selectedRegion.value;
  }
  
  // 添加城市筛选（默认天津市）
  if (selectedCity.value) {
    params.city = selectedCity.value;
  }
  
  // 添加区筛选
  if (selectedDistrict.value) {
    params.district = selectedDistrict.value;
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

// 获取所有医生并处理优先医生
const fetchAllDoctorsWithPriority = async (params) => {
  try {
    // 增大size以获取更多医生，确保优先医生在结果中
    const data = await getAllDoctors({ size: 100 });
    // request.js 响应拦截器已经解析了数据
    processDoctorsData(data?.content || [], params);
  } catch (e) { 
    console.error(e); 
    errorMessage.value = '加载失败'; 
  }
};

const fetchDoctors = async (params) => {
  try {
    console.log('fetchDoctors called with params:', params); // 调试信息
    errorMessage.value = '';
    
    // priorityDoctorId只用于前端排序，不应该传递给后端API
    const { priorityDoctorId, minRating, ...searchParams } = params;
    
    // 如果有priorityDoctorId，增大size参数以获取更多医生
    if (priorityDoctorId && !searchParams.size) {
      searchParams.size = 100; // 增大size以获取更多医生
    }
    
    const data = await searchDoctors(searchParams);
    console.log('Search doctors response:', data); // 调试信息
    // request.js 响应拦截器已经解析了数据
    const doctorsList = data?.content || data || [];
    processDoctorsData(doctorsList);
  } catch (e) { 
    console.error('Error fetching doctors:', e); 
    errorMessage.value = '搜索失败：' + (e.message || '未知错误');
  }
};

const processDoctorsData = async (fetchedDoctors, extraParams = {}) => {
  // 从路由参数或额外参数获取筛选条件
  const minRating = route.query.minRating ? parseFloat(route.query.minRating) : (extraParams.minRating ? parseFloat(extraParams.minRating) : null);
  const priorityDoctorId = route.query.priorityDoctorId || extraParams.priorityDoctorId;
  
  let processedDoctors = fetchedDoctors.map(doc => ({ ...doc, availabilityMap: {} }));
  
  // 按评分筛选
  if (minRating !== null) {
    processedDoctors = processedDoctors.filter(doc => {
      const rating = doc.rating || 0;
      return rating >= minRating;
    });
  }
  
  // 优先显示指定医生（只排序，不添加新医生，保持数量一致）
  if (priorityDoctorId) {
    const priorityDoctor = processedDoctors.find(doc => doc.id === priorityDoctorId);
    if (priorityDoctor) {
      // 将优先医生移到第一位
      processedDoctors = processedDoctors.filter(doc => doc.id !== priorityDoctorId);
      processedDoctors.unshift(priorityDoctor);
    }
    // 如果优先医生不在搜索结果中，不添加（保持搜索结果的一致性）
    // 这样可以确保无论点击哪个医生，显示的医生总数都一致
  }
  
  doctors.value = processedDoctors;
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
.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #DDD;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  color: #2A2A2A;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  
  &:hover {
    background: #F5F5F5;
    border-color: #999;
  }
  
  &:active {
    transform: scale(0.98);
  }
  
  svg {
    display: block;
  }
}
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
.map-column { flex: 35%; background: #f5f5f5; position: relative; border-left: 1px solid #DDD; min-height: 600px; }
.map-container { height: 100%; width: 100%; position: relative; min-height: 600px; }
.map-loading { 
  position: absolute; 
  top: 50%; 
  left: 50%; 
  transform: translate(-50%, -50%); 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  gap: 10px;
  color: #666;
  font-size: 14px;
}
.map-loading .loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #FFD300;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
/* 自定义地图标记样式 */
:deep(.custom-marker) {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
  color: #2A2A2A;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  cursor: pointer;
  transition: transform 0.2s ease;
}
:deep(.custom-marker:hover) {
  transform: scale(1.2);
}
:deep(.amap-info-content) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

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
.modal-time-btn { 
  background-color: #FFD300; 
  border: none; 
  border-radius: 4px; 
  padding: 10px 16px; 
  font-size: 14px; 
  font-weight: 600; 
  color: #2A2A2A; 
  cursor: pointer; 
  transition: all 0.2s; 
  min-width: 90px; 
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.modal-time-btn:hover:not(:disabled) { background-color: #F4CA00; }
.modal-time-btn.disabled { 
  background-color: #F5F5F5; 
  color: #999; 
  cursor: not-allowed; 
}
.modal-time-btn.low-slots { background-color: #FFF9C4; }
.modal-time-btn .time-text { font-weight: 600; }
.modal-time-btn .slots-count { font-size: 12px; font-weight: 400; color: #666; }
.modal-time-btn .slots-status { font-size: 12px; font-weight: 400; color: #999; }
.no-slots-msg { font-size: 14px; color: #666; padding: 10px 0; }

.modal-footer { padding: 16px 32px 32px 32px; border-top: 1px solid #F0F0F0; }
.btn-more-avail { background: none; border: none; font-size: 14px; font-weight: 600; color: #2A2A2A; text-decoration: underline; cursor: pointer; padding: 0; }

@media (max-width: 1000px) {
  .map-column { display: none; }
  .card-calendar-container { display: none; } 
  .card-info { flex: 1; border-right: none; }
}
</style>