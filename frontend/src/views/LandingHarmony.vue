<template>
  <div class="harmony-landing" @mousemove="handleMouseMove">
    <!-- Header -->
    <header class="harmony-header" :class="{ scrolled: isHeaderScrolled }">
      <div class="container nav-container">
        <div class="logo-area">
          <div class="logo-box">
            <span class="logo-char">优</span>
          </div>
          <span class="logo-text">优医预约</span>
          <!-- 定位显示 -->
          <div class="location-display" v-if="currentLocation" @click="autoLocate">
            <svg class="location-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <span class="location-text">{{ currentLocation }}</span>
          </div>
          <div class="location-display locating" v-else-if="isLocating">
            <svg class="location-icon spinning" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M12 6v2"></path>
              <path d="M12 16v2"></path>
              <path d="M6 12h2"></path>
              <path d="M16 12h2"></path>
            </svg>
            <span class="location-text">定位中...</span>
          </div>
          <div class="location-display failed" v-else @click="autoLocate">
            <svg class="location-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <span class="location-text">点击定位</span>
          </div>
        </div>
        <nav class="main-nav">
          <template v-if="!isLoggedIn">
            <a href="#" class="nav-link" @click.prevent="goLogin">登录</a>
            <button class="btn-signup" @click="goRegister">
              <span>注册</span>
            </button>
          </template>
          <template v-else>
            <div class="user-menu-wrapper">
              <div class="user-info-display" @click="toggleUserMenu">
                <div class="user-avatar">
                  <img v-if="userInfo?.avatarUrl" :src="userInfo.avatarUrl" alt="头像" />
                  <span v-else class="avatar-placeholder">{{ avatarPlaceholderText }}</span>
                </div>
                <span class="user-role-badge" v-if="userStore.userRole === 'DOCTOR'">医生</span>
                <span class="user-role-badge patient" v-else-if="userStore.userRole === 'PATIENT'">患者</span>
                <span class="user-role-badge admin" v-else-if="userStore.userRole === 'ADMIN'">管理员</span>
              </div>
              <div class="user-dropdown" v-if="showUserMenu" @click.stop>
                <template v-if="userStore.userRole === 'PATIENT'">
                  <a href="#" @click.prevent="goToHome">首页</a>
                  <a href="#" @click.prevent="goToMyHome">我的主页</a>
                  <a href="#" @click.prevent="goToProfile">个人信息</a>
                  <a href="#" @click.prevent="goToAppointments">我的预约</a>
                  <a href="#" @click.prevent="goToSearch">查找医生</a>
                  <a href="#" @click.prevent="goToRecords">就诊记录</a>
                  <a href="#" @click.prevent="goToSettings">设置</a>
                </template>
                <template v-else-if="userStore.userRole === 'DOCTOR'">
                  <a href="#" @click.prevent="goToHome">首页</a>
                  <a href="#" @click.prevent="goToWorkbench">工作台</a>
                  <a href="#" @click.prevent="goToProfile">个人资料</a>
                  <a href="#" @click.prevent="goToSettings">设置</a>
                </template>
                <template v-else-if="userStore.userRole === 'ADMIN'">
                  <a href="#" @click.prevent="goToHome">首页</a>
                  <a href="#" @click.prevent="goToAdminHome">管理后台</a>
                  <a href="#" @click.prevent="goToSettings">设置</a>
                </template>
                <div class="dropdown-divider"></div>
                <a href="#" @click.prevent="handleLogout">退出登录</a>
              </div>
            </div>
          </template>
        </nav>
      </div>
    </header>

    <!-- 3D Transition Container -->
    <div class="scene-3d-container" ref="scene3dContainer">
      <!-- Hero Section -->
      <section class="hero-section scene-3d" ref="heroSection" :style="heroTransformStyle">
        <div class="container hero-container">
        <div class="hero-content fade-in-up">
          <div class="hero-badge">
            <span class="badge-dot"></span>
            <span>智慧医疗平台</span>
          </div>
          <h1 class="hero-title">
            <span class="text-line line-1">在线预约医生</span><br>
            <span class="text-line line-2">告别排队烦恼</span>
          </h1>
          <p class="hero-subtitle">精准匹配 · AI智能推荐 · 便捷预约</p>
          
          <!-- 搜索栏 -->
          <div class="search-bar-wrapper">
            <div class="search-bar">
              <div class="input-item">
                <div class="icon-wrap">
                  <svg class="icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                  </svg>
                </div>
                <div class="text-area">
                  <label>搜索</label>
                  <input type="text" v-model="searchKeyword" placeholder="科室、医生或疾病..." />
                </div>
              </div>
              <div class="line"></div>
              <div class="input-item">
                <div class="icon-wrap">
                  <svg class="icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                </div>
                <div class="text-area">
                  <label>地区</label>
                  <select v-model="selectedDistrict" class="district-select" @change="onDistrictChange">
                    <option value="">选择区域</option>
                    <option v-for="district in tianjinDistricts" :key="district" :value="district">{{ district }}</option>
                  </select>
                </div>
              </div>
              <button class="btn-search" @click="findCare">
                <svg class="icon-search-btn" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
              </button>
            </div>
          </div>
          
          <!-- Hero图片 -->
          <div class="hero-image-box">
            <div class="hero-image-overlay"></div>
            <img src="https://images.unsplash.com/photo-1586773860418-d37222d8fce3?w=600&h=400&fit=crop&q=80" alt="医疗健康" @error="handleImageError" />
          </div>
          
          <!-- 滚动提示 -->
          <div class="scroll-hint">
            <div class="mouse">
              <div class="wheel"></div>
            </div>
            <span>向下滚动探索更多</span>
          </div>
        </div>
        
        <div class="hero-buttons">
          <button class="btn-primary" @click="goToSearch">
            <span>开始预约</span>
            <svg class="btn-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </button>
          <button class="btn-secondary" @click="goToDepartments">
            <span>浏览科室</span>
          </button>
        </div>
        
        <!-- Parallax decorative shapes -->
        <div class="parallax-shapes">
          <div class="shape shape-1" :style="{ transform: `translate(${mouseX * 0.5}px, ${mouseY * 0.5}px)` }"></div>
          <div class="shape shape-2" :style="{ transform: `translate(${mouseX * -0.3}px, ${mouseY * -0.3}px)` }"></div>
          <div class="shape shape-3" :style="{ transform: `translate(${mouseX * 0.4}px, ${mouseY * 0.4}px)` }"></div>
        </div>
      </div>
    </section>

      <!-- 3D Transition Overlay -->
      <div class="transition-overlay" :style="transitionOverlayStyle"></div>

      <!-- Doctors Section -->
      <section class="doctors-section scroll-animate scene-3d" ref="doctorsSection" :style="doctorsTransformStyle">
        <div class="container">
        <div class="section-header fade-in-up">
          <h2 class="section-title">评价最高的全科医生</h2>
          <p class="section-subtitle">90% 的患者给这些医生打出了 5 星好评</p>
        </div>
        <div class="doctors-grid">
          <div class="doctor-card fade-in-up" v-for="(doc, index) in topDoctors" :key="index" :style="{ animationDelay: `${index * 0.1}s` }" @click="() => bookDoctor(doc)">
            <div class="card-gradient"></div>
            <div class="doctor-avatar">
              <img :src="doc.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="Avatar" />
            </div>
            <h3 class="doctor-name">{{ doc.name }}</h3>
            <p class="doctor-title">{{ doc.title }}</p>
            <div class="doctor-rating">
              <svg class="star-icon" viewBox="0 0 24 24" fill="currentColor">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
              <span class="rating-score">{{ (doc.rating || 5.0).toFixed(1) }}</span>
              <span class="rating-count">({{ doc.reviewCount || 0 }})</span>
            </div>
            <p class="doctor-department">{{ doc.departmentName || '暂无地址' }}</p>
            <button class="btn-book">在线预约</button>
          </div>
        </div>
      </div>
    </section>
    </div>
    <!-- End 3D Transition Container -->

    <!-- Stats Section -->
    <section class="stats-section scroll-animate">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item fade-in-up" style="animation-delay: 0.1s">
            <div class="stat-number" data-target="10000">0</div>
            <div class="stat-label">注册医生</div>
          </div>
          <div class="stat-item fade-in-up" style="animation-delay: 0.2s">
            <div class="stat-number" data-target="50000">0</div>
            <div class="stat-label">服务患者</div>
          </div>
          <div class="stat-item fade-in-up" style="animation-delay: 0.3s">
            <div class="stat-number" data-target="98">0</div>
            <div class="stat-suffix">%</div>
            <div class="stat-label">满意度</div>
          </div>
          <div class="stat-item fade-in-up" style="animation-delay: 0.4s">
            <div class="stat-number" data-target="24">0</div>
            <div class="stat-suffix">小时</div>
            <div class="stat-label">在线服务</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Departments Section -->
    <section class="departments-section scroll-animate">
      <div class="container">
        <div class="section-header fade-in-up">
          <h2 class="section-title">热门科室</h2>
          <p class="section-subtitle">覆盖全科室，满足您的就医需求</p>
        </div>
        <div class="departments-grid">
          <div class="dept-card fade-in-up" v-for="(dept, index) in departments" :key="index" :style="{ animationDelay: `${index * 0.1}s` }" @click="goToDepartment(dept.id)">
            <div class="dept-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
              </svg>
            </div>
            <h3 class="dept-name">{{ dept.name }}</h3>
            <p class="dept-count">{{ dept.doctorCount || 0 }} 位医生</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Process Section -->
    <section class="process-section scroll-animate">
      <div class="container">
        <h2 class="section-title center-text fade-in-up">简单三步，轻松预约</h2>
        <div class="process-grid">
          <div class="process-item fade-in-up" style="animation-delay: 0.1s">
            <div class="process-number">01</div>
            <div class="process-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </div>
            <h3 class="process-title">搜索医生</h3>
            <p class="process-desc">根据科室、症状或医生姓名快速查找</p>
          </div>
          <div class="process-arrow fade-in-up" style="animation-delay: 0.15s">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </div>
          <div class="process-item fade-in-up" style="animation-delay: 0.2s">
            <div class="process-number">02</div>
            <div class="process-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
            </div>
            <h3 class="process-title">选择时间</h3>
            <p class="process-desc">查看医生排班，选择合适的就诊时间</p>
          </div>
          <div class="process-arrow fade-in-up" style="animation-delay: 0.25s">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </div>
          <div class="process-item fade-in-up" style="animation-delay: 0.3s">
            <div class="process-number">03</div>
            <div class="process-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
            <h3 class="process-title">确认预约</h3>
            <p class="process-desc">填写就诊信息，完成预约并等待确认</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials Section -->
    <section class="testimonials-section scroll-animate">
      <div class="container">
        <h2 class="section-title center-text fade-in-up">用户真实评价</h2>
        <div class="testimonials-grid">
          <div class="testimonial-card fade-in-up" v-for="(review, index) in testimonials" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="testimonial-stars">
              <svg v-for="n in 5" :key="n" class="star-icon" viewBox="0 0 24 24" fill="currentColor">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
            <p class="testimonial-text">{{ review.text }}</p>
            <div class="testimonial-author">
              <div class="author-avatar">{{ review.author.charAt(0) }}</div>
              <div class="author-info">
                <div class="author-name">{{ review.author }}</div>
                <div class="author-date">{{ review.date }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section scroll-animate">
      <div class="container">
        <div class="cta-content fade-in-up">
          <h2 class="cta-title">立即开始您的健康之旅</h2>
          <p class="cta-subtitle">加入数万用户，体验便捷的在线医疗服务</p>
          <div class="cta-buttons">
            <button class="btn-cta-primary" @click="goRegister">
              <span>立即注册</span>
              <svg class="btn-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="5" y1="12" x2="19" y2="12"></line>
                <polyline points="12 5 19 12 12 19"></polyline>
              </svg>
            </button>
            <button class="btn-cta-secondary" @click="goToSearch">
              <span>浏览医生</span>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section scroll-animate">
      <div class="container">
        <h2 class="section-title center-text fade-in-up">为您匹配最合适的医生</h2>
        <div class="features-grid">
          <div class="feature-card fade-in-up" style="animation-delay: 0.1s">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
              </svg>
            </div>
            <h3 class="feature-title">浏览各类专业医生</h3>
            <p class="feature-desc">满足您的就医需求</p>
            <button class="feature-btn" @click="goToDepartments">查看科室</button>
          </div>
          <div class="feature-card fade-in-up" style="animation-delay: 0.2s">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
            </div>
            <h3 class="feature-title">查看真实患者评价</h3>
            <p class="feature-desc">选择更放心</p>
            <button class="feature-btn" @click="goToDoctors">查看医生</button>
          </div>
          <div class="feature-card fade-in-up" style="animation-delay: 0.3s">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
            </div>
            <h3 class="feature-title">今天即可在线预约</h3>
            <p class="feature-desc">方便快捷</p>
            <button class="feature-btn" @click="goToSchedule">查看排期</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="harmony-footer">
      <div class="container footer-content">
        <div class="footer-col">
          <h4>关于优医</h4>
          <a href="#">关于我们</a>
          <a href="#">联系我们</a>
        </div>
        <div class="footer-col">
          <h4>发现</h4>
          <a href="#">按科室</a>
          <a href="#">按地区</a>
        </div>
        <div class="footer-col">
          <p>© 2025 优医预约系统</p>
        </div>
      </div>
    </footer>
  </div>
</template>


<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';
import { getCurrentLocation, getLocationByIP } from '@/utils/location';
import { getAllHospitals } from '@/api/hospital';

const router = useRouter();
const userStore = useUserStore();
const showUserMenu = ref(false);
const userInfo = ref(null);
const topDoctors = ref([]);

// 搜索和定位相关
const searchKeyword = ref('');
const selectedDistrict = ref('');
const tianjinDistricts = ref([]);
const isLocating = ref(false);
const locationError = ref('');
const currentLocation = ref('');

// New data for enriched sections - 真实数据
const departments = ref([
  { id: 1, name: '儿科', doctorCount: 4 },
  { id: 2, name: '皮肤科', doctorCount: 3 },
  { id: 3, name: '口腔科', doctorCount: 1 },
  { id: 4, name: '妇科', doctorCount: 1 },
  { id: 5, name: '心血管内科', doctorCount: 1 },
  { id: 6, name: '眼科', doctorCount: 1 },
  { id: 7, name: '骨科', doctorCount: 1 }
]);

const testimonials = ref([
  {
    text: '预约非常方便，医生很专业，服务态度也很好。省去了排队的烦恼，强烈推荐！',
    author: '张女士',
    date: '2025-01-03'
  },
  {
    text: '平台操作简单，医生资质都很好。第一次使用就预约成功了，体验非常棒。',
    author: '李先生',
    date: '2025-01-02'
  },
  {
    text: '作为一名医生，这个平台让我能更好地管理预约，提高了工作效率。',
    author: '王医生',
    date: '2025-01-01'
  }
]);

// Scroll and mouse tracking
const scrollY = ref(0);
const isHeaderScrolled = ref(false);
const mouseX = ref(0);
const mouseY = ref(0);

// 3D Transition refs and state
const scene3dContainer = ref(null);
const heroSection = ref(null);
const doctorsSection = ref(null);
const transitionProgress = ref(0);
const heroTransformStyle = ref({});
const doctorsTransformStyle = ref({});
const transitionOverlayStyle = ref({});

// Login state
const isLoggedIn = computed(() => userStore.isLoggedIn);

// Avatar placeholder
const avatarPlaceholderText = computed(() => {
  if (userStore.userRole === 'PATIENT') return '患';
  if (userStore.userRole === 'DOCTOR') return '医';
  if (userStore.userRole === 'ADMIN') return '管';
  return userInfo.value?.name?.charAt(0) || userStore.userInfo?.realName?.charAt(0) || '用';
});

// 自动获取位置
const autoLocate = async () => {
  isLocating.value = true;
  locationError.value = '';
  
  try {
    const location = await getCurrentLocation();
    const locationStr = location.district 
      ? `${location.city}${location.district}` 
      : location.city;
    if (location.district) {
      selectedDistrict.value = location.district;
    }
    currentLocation.value = locationStr;
  } catch (error) {
    console.warn('精确定位失败，尝试IP定位:', error.message);
    try {
      const ipLocation = await getLocationByIP();
      if (ipLocation && ipLocation.city) {
        currentLocation.value = ipLocation.city;
      } else {
        locationError.value = '定位失败';
        currentLocation.value = '';
      }
    } catch (ipError) {
      locationError.value = '定位失败';
      currentLocation.value = '';
    }
  } finally {
    isLocating.value = false;
  }
};

// 获取天津市各区列表
const fetchTianjinDistricts = async () => {
  try {
    const response = await getAllHospitals();
    if (response && Array.isArray(response)) {
      const districts = [...new Set(response
        .filter(h => h.district && h.district.includes('区'))
        .map(h => h.district)
        .sort())];
      tianjinDistricts.value = districts;
    }
  } catch (error) {
    console.error('获取区列表失败:', error);
    tianjinDistricts.value = ['和平区', '河东区', '河西区', '南开区', '河北区', '红桥区', '东丽区', '西青区', '津南区', '北辰区', '武清区', '宝坻区', '滨海新区'];
  }
};

// 区选择变化
const onDistrictChange = () => {
  if (selectedDistrict.value) {
    currentLocation.value = selectedDistrict.value;
  }
};

// 搜索功能
const findCare = () => {
  const query = {};
  if (searchKeyword.value && searchKeyword.value.trim()) {
    query.keyword = searchKeyword.value.trim();
  }
  if (selectedDistrict.value) {
    query.district = selectedDistrict.value;
  }
  router.push({ path: '/search-results', query });
};

// 图片加载错误处理
const handleImageError = (e) => {
  console.error('图片加载失败:', e);
  const imgBox = e.target?.closest('.hero-image-box');
  if (imgBox) {
    imgBox.style.display = 'none';
  }
};

// Mouse move handler for parallax
const handleMouseMove = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 30;
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 30;
};

// Scroll handler
const handleScroll = () => {
  scrollY.value = window.scrollY;
  isHeaderScrolled.value = window.scrollY > 50;
  
  // Calculate 3D transition progress
  calculate3DTransition();
  
  // Trigger scroll animations
  const elements = document.querySelectorAll('.scroll-animate');
  elements.forEach(el => {
    const rect = el.getBoundingClientRect();
    const isVisible = rect.top < window.innerHeight * 0.85;
    if (isVisible) {
      el.classList.add('animate-in');
    }
  });
};

// 3D Transition calculation
const calculate3DTransition = () => {
  if (!heroSection.value || !doctorsSection.value) {
    console.log('Refs not ready:', { hero: !!heroSection.value, doctors: !!doctorsSection.value });
    return;
  }
  
  const windowHeight = window.innerHeight;
  const scrollPosition = window.scrollY;
  
  // 调整触发范围：滚动更多才开始，让用户先看完第一屏
  // 滚动 200px 后开始，滚动到 100% 视口高度时完成
  const transitionStart = 200;
  const transitionEnd = windowHeight;
  
  let progress = 0;
  
  if (scrollPosition >= transitionStart && scrollPosition <= transitionEnd) {
    progress = (scrollPosition - transitionStart) / (transitionEnd - transitionStart);
    progress = Math.max(0, Math.min(1, progress));
  } else if (scrollPosition > transitionEnd) {
    progress = 1;
  }
  
  // Debug log
  console.log('3D Transition:', { 
    scrollY: scrollPosition.toFixed(0), 
    progress: (progress * 100).toFixed(1) + '%',
    transitionStart: transitionStart.toFixed(0),
    transitionEnd: transitionEnd.toFixed(0)
  });
  
  transitionProgress.value = progress;
  
  // Apply 3D transforms based on progress
  apply3DTransforms(progress);
};

// Apply 3D transforms
const apply3DTransforms = (progress) => {
  // Easing functions
  const easeInOutCubic = (t) => {
    return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
  };
  
  const easeOutBack = (t) => {
    const c1 = 1.70158;
    const c3 = c1 + 1;
    return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
  };
  
  const easedProgress = easeInOutCubic(progress);
  
  // ========== 书页翻转 + 场景切换效果 ==========
  
  // Hero section - 向左翻页效果（进一步减小幅度，更优雅）
  const heroRotationY = easedProgress * -25;  // 从 -45 改为 -25，更微妙的翻转
  const heroTranslateX = easedProgress * -15;  // 从 -25 改为 -15，减少左移
  const heroScale = 1 - easedProgress * 0.05;  // 从 0.1 改为 0.05，几乎不缩放
  const heroOpacity = Math.max(0, 1 - easedProgress * 1.5); // 稍微加快淡出
  
  // 直接设置 DOM 样式，绕过可能的 Vue 响应式问题
  if (heroSection.value) {
    const heroEl = heroSection.value;
    heroEl.style.cssText = `
      transform: perspective(1500px) rotateY(${heroRotationY}deg) translateX(${heroTranslateX}%) scale(${heroScale}) !important;
      opacity: ${heroOpacity} !important;
      filter: blur(${easedProgress * 2}px);
      transform-origin: left center !important;
      box-shadow: ${easedProgress > 0.1 ? `${-15 * easedProgress}px 0 40px rgba(0,0,0,${0.2 * easedProgress})` : 'none'};
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      padding: 120px 0 80px;
      background: linear-gradient(180deg, #FFF9E5 0%, #FFFDF5 50%, #FFF9E5 100%);
      transform-style: preserve-3d;
      backface-visibility: hidden;
    `;
  }
  
  // Doctors section - 从上方滑入
  const doctorsDelay = 0.2;
  const doctorsProgress = Math.max(0, (easedProgress - doctorsDelay) / (1 - doctorsDelay));
  const doctorsEased = easeOutBack(Math.min(1, doctorsProgress));
  
  const doctorsTranslateY = (1 - doctorsEased) * -100;
  const doctorsScale = 0.8 + doctorsEased * 0.2;
  const doctorsOpacity = doctorsProgress > 0 ? Math.min(1, doctorsProgress * 2) : 0;
  const doctorsRotateX = (1 - doctorsEased) * 20;
  
  if (doctorsSection.value) {
    doctorsSection.value.style.transform = `perspective(1200px) translateY(${doctorsTranslateY}vh) scale(${doctorsScale}) rotateX(${doctorsRotateX}deg)`;
    doctorsSection.value.style.opacity = doctorsOpacity;
    doctorsSection.value.style.filter = `blur(${(1 - doctorsEased) * 6}px)`;
    doctorsSection.value.style.transformOrigin = 'center top';
  }
  
  // Transition overlay - 场景转换光效
  const overlayPeak = 0.5;
  const overlayIntensity = Math.exp(-Math.pow((easedProgress - overlayPeak) / 0.3, 2));
  const overlayOpacity = overlayIntensity * 0.8;
  const lightPositionX = 100 - easedProgress * 200;
  
  transitionOverlayStyle.value = {
    opacity: overlayOpacity,
    transform: `translateX(${lightPositionX}%)`,
    background: `linear-gradient(90deg, transparent 0%, rgba(255, 211, 0, ${overlayOpacity * 0.6}) 20%, rgba(255, 255, 255, ${overlayOpacity * 0.9}) 50%, rgba(255, 211, 0, ${overlayOpacity * 0.6}) 80%, transparent 100%)`,
    transition: 'none',
    mixBlendMode: 'screen'
  };
  
  // Debug log
  console.log('Transform applied:', {
    progress: (progress * 100).toFixed(1) + '%',
    heroRotationY: heroRotationY.toFixed(1),
    heroOpacity: heroOpacity.toFixed(2),
    doctorsTranslateY: doctorsTranslateY.toFixed(1),
    doctorsOpacity: doctorsOpacity.toFixed(2)
  });
};

// Initialize transforms with progress = 0 (hero clear, doctors blurred)
const initializeTransforms = () => {
  console.log('Initializing transforms...');
  if (!heroSection.value || !doctorsSection.value) {
    console.warn('Cannot initialize: refs not ready');
    return;
  }
  console.log('Applying initial transforms with progress = 0');
  apply3DTransforms(0);
};

// User menu
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

const handleClickOutside = (e) => {
  if (!e.target.closest('.user-menu-wrapper') && !e.target.closest('.user-info-display')) {
    showUserMenu.value = false;
  }
};

// Navigation functions
const goLogin = () => router.push('/login');
const goRegister = () => router.push('/register');
const goToHome = () => { showUserMenu.value = false; router.push('/landing'); };
const goToMyHome = () => { showUserMenu.value = false; router.push('/patient/home'); };
const goToWorkbench = () => { showUserMenu.value = false; router.push('/doctor/home'); };
const goToAdminHome = () => { showUserMenu.value = false; router.push('/admin/home'); };
const goToProfile = () => {
  showUserMenu.value = false;
  if (userStore.userRole === 'PATIENT') router.push('/patient/profile');
  else if (userStore.userRole === 'DOCTOR') router.push('/doctor/profile');
};
const goToAppointments = () => {
  showUserMenu.value = false;
  if (userStore.userRole === 'PATIENT') router.push('/patient/appointments');
  else if (userStore.userRole === 'DOCTOR') router.push('/doctor/appointments');
};
const goToSearch = () => { showUserMenu.value = false; router.push('/search-results'); };
const goToRecords = () => { showUserMenu.value = false; if (userStore.userRole === 'PATIENT') router.push('/patient/medical-records'); };
const goToSettings = () => {
  showUserMenu.value = false;
  if (userStore.userRole === 'PATIENT') router.push('/patient/settings');
  else if (userStore.userRole === 'DOCTOR') router.push('/doctor/settings');
};
const handleLogout = () => { showUserMenu.value = false; userStore.logout(); router.push('/landing'); };

const goToDepartments = () => router.push('/search-triage');
const goToDoctors = () => router.push('/search-triage');
const goToSchedule = () => router.push('/search-triage');
const bookDoctor = (doctor) => {
  router.push({ path: '/search-results', query: { priorityDoctorId: doctor.id, minRating: '5.0' } });
};
const goToDepartment = (deptId) => {
  router.push({ path: '/search-results', query: { departmentId: deptId } });
};

// Number counter animation
const animateNumber = (element, target, duration = 2000) => {
  const start = 0;
  const increment = target / (duration / 16);
  let current = start;
  
  const timer = setInterval(() => {
    current += increment;
    if (current >= target) {
      element.textContent = Math.floor(target);
      clearInterval(timer);
    } else {
      element.textContent = Math.floor(current);
    }
  }, 16);
};

const initNumberCounters = () => {
  const statNumbers = document.querySelectorAll('.stat-number');
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting && !entry.target.classList.contains('counted')) {
        const target = parseInt(entry.target.getAttribute('data-target'));
        animateNumber(entry.target, target);
        entry.target.classList.add('counted');
      }
    });
  }, { threshold: 0.5 });
  
  statNumbers.forEach(num => observer.observe(num));
};

// Fetch data
const fetchLandingData = async () => {
  try {
    const results = await Promise.allSettled([
      request.get('/api/doctors/top', { params: { limit: 10 } })
    ]);
    const allDoctors = (results[0].status === 'fulfilled' && results[0].value) ? results[0].value : [];
    topDoctors.value = allDoctors.filter(doc => (doc.rating || 0) === 5.0).slice(0, 6);
  } catch (error) {
    console.error('Failed to fetch landing page data:', error);
  }
};

// 获取当前用户信息（包括头像）
const fetchUserInfo = async () => {
  if (!isLoggedIn.value) return;
  
  try {
    if (userStore.userRole === 'PATIENT') {
      const data = await request.get('/api/patients/profile');
      if (data) {
        userInfo.value = {
          name: data.name,
          avatarUrl: data.avatarUrl
        };
      }
    } else if (userStore.userRole === 'DOCTOR') {
      const data = await request.get('/api/doctor/profile');
      if (data) {
        userInfo.value = {
          name: data.name,
          avatarUrl: data.avatarUrl
        };
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

onMounted(async () => {
  // Wait for DOM to be fully rendered
  await nextTick();
  
  // Add small delay to ensure refs are populated
  setTimeout(() => {
    console.log('onMounted: Initializing...');
    initializeTransforms();
    handleScroll(); // Also call handleScroll to update based on initial scroll position
  }, 100);
  
  window.addEventListener('scroll', handleScroll, { passive: true });
  window.addEventListener('click', handleClickOutside);
  fetchLandingData();
  fetchTianjinDistricts();
  autoLocate();
  initNumberCounters();
  fetchUserInfo();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('click', handleClickOutside);
});
</script>


<style scoped>
/* ========== HarmonyOS Style Variables ========== */
:root {
  --harmony-gradient-start: #FFD300;
  --harmony-gradient-end: #FF9800;
  --harmony-bg-dark: #0A0E27;
  --harmony-bg-section: #111827;
  --harmony-text-light: #fff;
  --harmony-text-gray: #9CA3AF;
  --harmony-accent: #FFD300;
}

/* ========== Base Styles ========== */
.harmony-landing {
  background: linear-gradient(180deg, #FFF9E5 0%, #FFFDF5 50%, #FFF9E5 100%);
  color: #2A2A2A;
  min-height: 100vh;
  overflow-x: hidden;
  perspective: 1000px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* ========== 3D Transition Styles ========== */
.scene-3d-container {
  position: relative;
  transform-style: preserve-3d;
  perspective: 1500px;
  perspective-origin: 60% 50%;
}

.scene-3d {
  transform-style: preserve-3d;
  will-change: transform, opacity, filter, box-shadow;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
}

/* Hero section 特殊样式 - 翻页效果 */
.hero-section.scene-3d {
  position: relative;
  z-index: 2;
}

/* Doctors section 特殊样式 - 下滑效果 */
.doctors-section.scene-3d {
  position: relative;
  z-index: 1;
}

.transition-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 100;
  will-change: transform, opacity, background;
}

/* ========== Animations ========== */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-40px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes fadeInRight {
  from { opacity: 0; transform: translateX(40px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes heroFadeIn {
  from { opacity: 0; transform: translateY(60px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes scroll {
  0%, 100% { opacity: 0; transform: translateX(-50%) translateY(0); }
  50% { opacity: 1; transform: translateX(-50%) translateY(12px); }
}

.fade-in-up {
  animation: fadeInUp 0.8s ease-out both;
}

.fade-in-left {
  animation: fadeInLeft 0.8s ease-out both;
}

.fade-in-right {
  animation: fadeInRight 0.8s ease-out both;
}

.hero-animate {
  animation: heroFadeIn 1s ease-out both;
}

.scroll-animate {
  opacity: 0;
  transform: translateY(60px);
  transition: opacity 0.8s cubic-bezier(0.4, 0, 0.2, 1), transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.scroll-animate.animate-in {
  opacity: 1;
  transform: translateY(0);
}


/* ========== Header ========== */
.harmony-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 20px 0;
  transition: all 0.3s ease;
  background: linear-gradient(180deg, #FFF9E5 0%, rgba(255, 249, 229, 0.95) 100%);
}

.harmony-header.scrolled {
  background: rgba(255, 249, 229, 0.98);
  backdrop-filter: blur(20px);
  padding: 15px 0;
  box-shadow: 0 4px 20px rgba(255, 211, 0, 0.1);
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-box {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FFD300, #FF9800);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  color: #2A2A2A;
  transition: transform 0.3s ease;
}

.logo-area:hover .logo-box {
  transform: scale(1.1) rotate(5deg);
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  color: #2A2A2A;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.3s ease;
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  transition: width 0.3s ease;
}

.nav-link:hover {
  color: #FFD300;
}

.nav-link:hover::after {
  width: 100%;
}

.btn-signup {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  padding: 12px 28px;
  border-radius: 25px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 211, 0, 0.3);
}

.btn-signup:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 211, 0, 0.5);
}

/* ========== Hero Section ========== */
.hero-section {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 120px 0 80px;
  background: linear-gradient(180deg, #FFF9E5 0%, #FFFDF5 50%, #FFF9E5 100%);
  /* 确保3D变换生效 */
  transform-style: preserve-3d;
}

.hero-container {
  position: relative;
  z-index: 2;
}

.hero-content {
  max-width: 1000px;
  text-align: center;
  margin: 0 auto;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(255, 211, 0, 0.1);
  border: 1px solid rgba(255, 211, 0, 0.3);
  border-radius: 25px;
  font-size: 14px;
  color: #FF9800;
  font-weight: 600;
  margin-bottom: 32px;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.hero-title {
  font-size: 72px;
  font-weight: 700;
  line-height: 1.1;
  margin: 0 0 24px 0;
  letter-spacing: -2px;
}

.text-line {
  display: block;
  color: #2A2A2A;
}

.line-1 {
  animation-delay: 0.2s;
}

.line-2 {
  animation-delay: 0.4s;
}

.hero-subtitle {
  font-size: 20px;
  color: #666;
  margin: 0 0 48px 0;
  letter-spacing: 3px;
  animation-delay: 0.6s;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  animation-delay: 0.8s;
  margin-top: 48px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  padding: 18px 40px;
  border-radius: 30px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 30px rgba(255, 211, 0, 0.4);
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(255, 211, 0, 0.6);
}

.btn-arrow {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.btn-primary:hover .btn-arrow {
  transform: translateX(4px);
}

.btn-secondary {
  background: transparent;
  border: 2px solid rgba(42, 42, 42, 0.3);
  padding: 16px 40px;
  border-radius: 30px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background: rgba(255, 211, 0, 0.1);
  border-color: #FFD300;
  transform: translateY(-2px);
}

/* Parallax shapes */
.parallax-shapes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.3), transparent);
  top: 10%;
  left: 10%;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 152, 0, 0.2), transparent);
  bottom: 10%;
  right: 10%;
  animation-delay: -2s;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.25), transparent);
  top: 50%;
  left: 50%;
  animation-delay: -4s;
}
/* ========== Search Bar ========== */
.search-bar-wrapper {
  margin: 48px 0;
  animation-delay: 0.7s;
  width: 100%;
  max-width: 1000px;
}

.search-bar {
  background: rgba(255, 249, 229, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 60px;
  padding: 8px;
  display: flex;
  align-items: center;
  gap: 0;
  box-shadow: 0 10px 40px rgba(255, 211, 0, 0.2);
  transition: all 0.3s ease;
  width: 100%;
}

.search-bar:hover {
  border-color: rgba(255, 211, 0, 0.4);
  box-shadow: 0 15px 50px rgba(255, 211, 0, 0.3);
}

.input-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
}

.icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-sm {
  width: 20px;
  height: 20px;
  color: var(--harmony-accent);
}

.text-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.text-area label {
  font-size: 12px;
  color: var(--harmony-text-gray);
  font-weight: 500;
}

.text-area input,
.text-area select {
  background: transparent;
  border: none;
  outline: none;
  color: #2A2A2A;
  font-size: 15px;
  font-weight: 500;
  width: 100%;
}

.text-area input::placeholder {
  color: rgba(156, 163, 175, 0.5);
}

.district-select {
  cursor: pointer;
}

.district-select option {
  background: #FFF9E5;
  color: #2A2A2A;
}

.line {
  width: 1px;
  height: 40px;
  background: rgba(255, 211, 0, 0.2);
}

.btn-search {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 211, 0, 0.4);
}

.btn-search:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 25px rgba(255, 211, 0, 0.6);
}

.icon-search-btn {
  width: 24px;
  height: 24px;
  color: #2A2A2A;
}

/* ========== Location Display ========== */
.location-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255, 211, 0, 0.1);
  border: 1px solid rgba(255, 211, 0, 0.3);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.location-display:hover {
  background: rgba(255, 211, 0, 0.2);
  border-color: rgba(255, 211, 0, 0.5);
}

.location-display.locating {
  cursor: wait;
}

.location-display.failed {
  border-color: rgba(239, 68, 68, 0.5);
  background: rgba(239, 68, 68, 0.1);
}

.location-icon {
  width: 16px;
  height: 16px;
  color: var(--harmony-accent);
}

.location-icon.spinning {
  animation: spin 1s linear infinite;
}

.location-text {
  font-size: 13px;
  color: #2A2A2A;
  font-weight: 500;
}

/* ========== Hero Image ========== */
.hero-image-box {
  margin: 48px auto 0;
  max-width: 600px;
  border-radius: 24px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 20px 60px rgba(255, 211, 0, 0.3);
  animation-delay: 0.9s;
}

.hero-image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.3), rgba(255, 152, 0, 0.3));
  z-index: 1;
}

.hero-image-box img {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.6s ease;
}

.hero-image-box:hover img {
  transform: scale(1.05);
}

/* ========== Scroll Hint ========== */
.scroll-hint {
  margin-top: 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--harmony-text-gray);
  font-size: 14px;
  animation-delay: 1s;
}

.mouse {
  width: 24px;
  height: 40px;
  border: 2px solid rgba(255, 211, 0, 0.5);
  border-radius: 12px;
  position: relative;
}

.wheel {
  width: 4px;
  height: 8px;
  background: var(--harmony-accent);
  border-radius: 2px;
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  animation: scroll 2s ease-in-out infinite;
}

/* ========== Doctors Section ========== */
.doctors-section {
  padding: 100px 0;
  position: relative;
  background: linear-gradient(180deg, #FFFDF5 0%, #FFF9E5 100%);
  transform-style: preserve-3d;
  min-height: 80vh;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #2A2A2A;
}

.section-subtitle {
  font-size: 18px;
  color: var(--harmony-text-gray);
  margin: 0;
}

.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
}

.doctor-card {
  background: rgba(255, 249, 229, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 24px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.card-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.doctor-card:hover .card-gradient {
  opacity: 1;
}

.doctor-card:hover {
  transform: translateY(-8px);
  border-color: rgba(255, 211, 0, 0.5);
  box-shadow: 0 20px 60px rgba(255, 211, 0, 0.3);
}

.doctor-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin: 0 auto 20px;
  overflow: hidden;
  border: 3px solid rgba(255, 211, 0, 0.3);
  transition: all 0.3s ease;
}

.doctor-card:hover .doctor-avatar {
  border-color: var(--harmony-gradient-start);
  transform: scale(1.1);
}

.doctor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.doctor-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #2A2A2A;
}

.doctor-title {
  font-size: 14px;
  color: var(--harmony-text-gray);
  margin: 0 0 16px 0;
}

.doctor-rating {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.star-icon {
  width: 18px;
  height: 18px;
  color: #FCD34D;
}

.rating-score {
  font-size: 16px;
  font-weight: 700;
  color: #2A2A2A;
}

.rating-count {
  font-size: 14px;
  color: var(--harmony-text-gray);
}

.doctor-department {
  font-size: 14px;
  color: var(--harmony-text-gray);
  margin: 0 0 20px 0;
}

.btn-book {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  padding: 12px 32px;
  border-radius: 25px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.btn-book:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(255, 211, 0, 0.5);
}

/* ========== Stats Section ========== */
.stats-section {
  padding: 80px 0;
  background: rgba(255, 249, 229, 0.4);
  backdrop-filter: blur(20px);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 48px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 56px;
  font-weight: 700;
  color: var(--harmony-gradient-start);
  display: inline-block;
  margin-bottom: 8px;
}

.stat-suffix {
  font-size: 32px;
  font-weight: 700;
  color: var(--harmony-gradient-start);
  display: inline-block;
  margin-left: 4px;
}

.stat-label {
  font-size: 16px;
  color: var(--harmony-text-gray);
  font-weight: 500;
}

/* ========== Departments Section ========== */
.departments-section {
  padding: 100px 0;
}

.departments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.dept-card {
  background: rgba(255, 249, 229, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.dept-card:hover {
  transform: translateY(-8px);
  border-color: rgba(255, 211, 0, 0.5);
  box-shadow: 0 15px 50px rgba(255, 211, 0, 0.3);
  background: rgba(255, 249, 229, 0.8);
}

.dept-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.2), rgba(255, 152, 0, 0.2));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.dept-card:hover .dept-icon {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  transform: scale(1.1) rotate(5deg);
}

.dept-icon svg {
  width: 32px;
  height: 32px;
  color: var(--harmony-accent);
  transition: color 0.3s ease;
}

.dept-card:hover .dept-icon svg {
  color: #2A2A2A;
}

.dept-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #2A2A2A;
}

.dept-count {
  font-size: 14px;
  color: var(--harmony-text-gray);
  margin: 0;
}

/* ========== Process Section ========== */
.process-section {
  padding: 100px 0;
  background: rgba(255, 249, 229, 0.4);
  backdrop-filter: blur(20px);
}

.center-text {
  text-align: center;
}

.process-grid {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  margin-top: 60px;
  flex-wrap: wrap;
}

.process-item {
  flex: 1;
  min-width: 200px;
  max-width: 280px;
  text-align: center;
  position: relative;
}

.process-number {
  font-size: 72px;
  font-weight: 700;
  color: rgba(255, 211, 0, 0.5);
  margin-bottom: 20px;
  line-height: 1;
}

.process-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.2), rgba(255, 152, 0, 0.2));
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.process-item:hover .process-icon {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  transform: scale(1.1);
}

.process-icon svg {
  width: 40px;
  height: 40px;
  color: var(--harmony-accent);
  transition: color 0.3s ease;
}

.process-item:hover .process-icon svg {
  color: #2A2A2A;
}

.process-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #2A2A2A;
}

.process-desc {
  font-size: 14px;
  color: var(--harmony-text-gray);
  margin: 0;
  line-height: 1.6;
}

.process-arrow {
  flex: 0 0 auto;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.process-arrow svg {
  width: 32px;
  height: 32px;
  color: rgba(255, 211, 0, 0.5);
}

/* ========== Testimonials Section ========== */
.testimonials-section {
  padding: 100px 0;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 32px;
  margin-top: 60px;
}

.testimonial-card {
  background: rgba(255, 249, 229, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 24px;
  padding: 32px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.testimonial-card:hover {
  transform: translateY(-8px);
  border-color: rgba(255, 211, 0, 0.5);
  box-shadow: 0 20px 60px rgba(255, 211, 0, 0.3);
}

.testimonial-stars {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
}

.testimonial-stars .star-icon {
  width: 20px;
  height: 20px;
  color: #FCD34D;
}

.testimonial-text {
  font-size: 16px;
  color: #2A2A2A;
  line-height: 1.8;
  margin: 0 0 24px 0;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 16px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 16px;
  font-weight: 700;
  color: #2A2A2A;
  margin-bottom: 4px;
}

.author-date {
  font-size: 14px;
  color: var(--harmony-text-gray);
}

/* ========== CTA Section ========== */
.cta-section {
  padding: 100px 0;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.1), rgba(255, 152, 0, 0.1));
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.15), transparent 50%);
  animation: float 10s ease-in-out infinite;
}

.cta-content {
  text-align: center;
  position: relative;
  z-index: 2;
}

.cta-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 20px 0;
  color: #2A2A2A;
}

.cta-subtitle {
  font-size: 20px;
  color: var(--harmony-text-gray);
  margin: 0 0 48px 0;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-cta-primary {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border: none;
  padding: 18px 48px;
  border-radius: 30px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 30px rgba(255, 211, 0, 0.4);
}

.btn-cta-primary:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 40px rgba(255, 211, 0, 0.6);
}

.btn-cta-primary .btn-arrow {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.btn-cta-primary:hover .btn-arrow {
  transform: translateX(4px);
}

.btn-cta-secondary {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.3);
  padding: 16px 48px;
  border-radius: 30px;
  color: #2A2A2A;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cta-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

/* ========== Features Section ========== */
.features-section {
  padding: 100px 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 32px;
  margin-top: 60px;
}

.feature-card {
  background: rgba(255, 249, 229, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.2);
  border-radius: 24px;
  padding: 40px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.feature-card:hover {
  transform: translateY(-8px);
  border-color: rgba(255, 211, 0, 0.5);
  box-shadow: 0 20px 60px rgba(255, 211, 0, 0.3);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.2), rgba(255, 152, 0, 0.2));
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.feature-card:hover .feature-icon {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  transform: scale(1.1) rotate(5deg);
}

.feature-icon svg {
  width: 40px;
  height: 40px;
  color: var(--harmony-accent);
  transition: color 0.3s ease;
}

.feature-card:hover .feature-icon svg {
  color: #2A2A2A;
}

.feature-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #2A2A2A;
}

.feature-desc {
  font-size: 14px;
  color: var(--harmony-text-gray);
  margin: 0 0 24px 0;
}

.feature-btn {
  background: transparent;
  border: 2px solid rgba(255, 211, 0, 0.3);
  padding: 12px 32px;
  border-radius: 25px;
  color: var(--harmony-accent);
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.feature-btn:hover {
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  border-color: transparent;
  color: #2A2A2A;
  transform: scale(1.05);
}

/* ========== Footer ========== */
.harmony-footer {
  padding: 60px 0 40px;
  background: rgba(255, 249, 229, 0.8);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 211, 0, 0.2);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 48px;
  flex-wrap: wrap;
}

.footer-col {
  flex: 1;
  min-width: 200px;
}

.footer-col h4 {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 20px 0;
  color: #2A2A2A;
}

.footer-col a {
  display: block;
  color: var(--harmony-text-gray);
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 12px;
  transition: color 0.3s ease;
}

.footer-col a:hover {
  color: var(--harmony-accent);
}

.footer-col p {
  color: var(--harmony-text-gray);
  font-size: 14px;
  margin: 0;
}

/* ========== User Menu ========== */
.user-menu-wrapper {
  position: relative;
}

.user-info-display {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
  background: rgba(255, 211, 0, 0.1);
  border: 1px solid rgba(255, 211, 0, 0.3);
}

.user-info-display:hover {
  background: rgba(255, 211, 0, 0.2);
  border-color: rgba(255, 211, 0, 0.5);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 16px;
  font-weight: 700;
  color: #2A2A2A;
}

.user-role-badge {
  font-size: 13px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--harmony-gradient-start), var(--harmony-gradient-end));
  color: #2A2A2A;
}

.user-role-badge.patient {
  background: linear-gradient(135deg, #10B981, #059669);
}

.user-role-badge.admin {
  background: linear-gradient(135deg, #F59E0B, #D97706);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  min-width: 200px;
  background: rgba(255, 249, 229, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 211, 0, 0.3);
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 10px 40px rgba(255, 211, 0, 0.3);
  z-index: 1000;
  animation: fadeInUp 0.3s ease-out;
}

.user-dropdown a {
  display: block;
  padding: 12px 16px;
  color: #2A2A2A;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-dropdown a:hover {
  background: rgba(255, 211, 0, 0.2);
  color: var(--harmony-accent);
}

.dropdown-divider {
  height: 1px;
  background: rgba(255, 211, 0, 0.2);
  margin: 8px 0;
}

/* ========== Responsive Design ========== */
@media (max-width: 1024px) {
  .hero-title {
    font-size: 56px;
  }
  
  .section-title {
    font-size: 40px;
  }
  
  .doctors-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 42px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: stretch;
  }
  
  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }
  
  .search-bar {
    flex-direction: column;
    border-radius: 24px;
    gap: 8px;
  }
  
  .line {
    display: none;
  }
  
  .btn-search {
    width: 100%;
    border-radius: 16px;
    height: 48px;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .doctors-grid,
  .departments-grid,
  .features-grid,
  .testimonials-grid {
    grid-template-columns: 1fr;
  }
  
  .process-grid {
    flex-direction: column;
  }
  
  .process-arrow {
    transform: rotate(90deg);
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
  }
  
  .cta-title {
    font-size: 36px;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: stretch;
  }
  
  .btn-cta-primary,
  .btn-cta-secondary {
    width: 100%;
    justify-content: center;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 32px;
  }
  
  .nav-container {
    flex-wrap: wrap;
  }
  
  .main-nav {
    width: 100%;
    justify-content: center;
    margin-top: 16px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 32px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .stat-number {
    font-size: 42px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .logo-text {
    display: none;
  }
  
  .location-display {
    display: none;
  }
}
</style>
