<template>
  <div class="landing-page" @mousemove="handleMouseMove">
    <!-- 星河背景 -->
    <div class="starfield">
      <div class="stars stars-1"></div>
      <div class="stars stars-2"></div>
      <div class="stars stars-3"></div>
      <div class="shooting-stars">
        <div class="shooting-star" v-for="n in 8" :key="'shoot-'+n" :style="getShootingStarStyle(n)"></div>
      </div>
      <div class="nebula"></div>
      <div class="nebula nebula-2"></div>
      <div class="nebula nebula-3"></div>
    </div>
    
    <!-- 背景粒子效果 -->
    <div class="particles-bg">
      <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
    </div>
    
    <div class="cream-wrapper">
      <!-- 动态光晕背景 -->
      <div class="hero-glow" :style="{ transform: `translate(${mouseX * 0.5}px, ${mouseY * 0.5}px)` }"></div>
      <div class="hero-glow-2" :style="{ transform: `translate(${mouseX * -0.3}px, ${mouseY * -0.3}px)` }"></div>
      
      <header class="main-header" :class="{ scrolled: isHeaderScrolled }">
        <div class="container nav-container">
          <div class="logo-area">
            <div class="logo-box">
              <span class="logo-char">优</span>
            </div>
            <span class="logo-text">优医预约</span>
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
            <a href="#" class="nav-link">
              浏览
              <svg class="icon-xs" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"></polyline></svg>
            </a>
            <a href="#" class="nav-link">帮助中心</a>
            <a href="#" class="nav-link">医疗机构入驻</a>
            <div class="nav-divider"></div>
            <a href="#" class="nav-link login-link" @click.prevent="goLogin">登录</a>
            <button class="btn-signup" @click="goRegister">
              <span>注册</span>
              <div class="btn-ripple"></div>
            </button>
          </nav>
        </div>
      </header>

      <section class="hero-section">
        <div class="container hero-container">
          <!-- 装饰浮动元素 -->
          <div class="floating-shapes">
            <div class="shape shape-1" :style="{ transform: `translate(${mouseX * 0.8}px, ${mouseY * 0.8}px)` }"></div>
            <div class="shape shape-2" :style="{ transform: `translate(${mouseX * -0.5}px, ${mouseY * -0.5}px)` }"></div>
            <div class="shape shape-3" :style="{ transform: `translate(${mouseX * 0.6}px, ${mouseY * 0.6}px)` }"></div>
          </div>
          
          <div class="hero-text">
            <div class="hero-badge">
              <span class="badge-dot"></span>
              <span>智慧医疗平台</span>
            </div>
            <h1>
              <span class="text-line line-1">在线预约医生</span><br>
              <span class="text-line line-2">告别排队烦恼</span>
            </h1>
            <p class="hero-subtitle">精准匹配 · AI智能推荐 · 便捷预约</p>
          </div>
          
          <div class="hero-image-box">
            <div class="image-glow"></div>
            <img src="@/assets/header-illustration.png" alt="医生插图" class="blended-image" />
          </div>
          
          <div class="search-bar-wrapper">
            <div class="search-bar">
              <div class="input-item">
                <div class="icon-wrap">
                  <svg class="icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                </div>
                <div class="text-area">
                  <label>搜索</label>
                  <input type="text" v-model="searchKeyword" placeholder="科室、医生或疾病..." />
                </div>
              </div>
              <div class="line"></div>
              <div class="input-item">
                <div class="icon-wrap">
                  <svg class="icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                </div>
                <div class="text-area">
                  <label>地区</label>
                  <input type="text" v-model="searchLocation" placeholder="例如：北京市" />
                </div>
              </div>
              <button class="btn-search" @click="findCare">
                <svg class="icon-search-btn" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
              </button>
            </div>
          </div>
          
          <!-- 滚动提示 -->
          <div class="scroll-hint">
            <div class="mouse">
              <div class="wheel"></div>
            </div>
            <span>向下滚动探索更多</span>
          </div>
        </div>
      </section>
    </div>

    <section class="list-section scroll-animate">
      <div class="container">
        <div class="section-header">
          <div>
            <h2 class="section-title">评价最高的全科医生</h2>
            <p class="section-subtitle">90% 的患者给这些医生打出了 5 星好评</p>
          </div>
          <a href="#" class="see-all">查看全部 ›</a>
        </div>
        <div class="scroll-container">
          <div class="doctor-card" v-for="(doc, index) in topDoctors" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="card-shine"></div>
            <div class="card-top">
              <div class="avatar-wrapper">
                <img :src="doc.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="Avatar" class="avatar" />
              </div>
              <div class="doc-info">
                <h3 class="doc-name">{{ doc.name }}</h3>
                <p class="doc-title">{{ doc.title }}</p>
                <div class="rating">
                  <svg class="star-icon" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                  <span class="score">{{ (doc.rating || 5.0).toFixed(1) }}</span>
                  <span class="reviews">({{ doc.reviewCount || 0 }})</span>
                </div>
              </div>
            </div>
            <div class="card-body">
              <div class="location-row">
                <svg class="icon-xs" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                {{ doc.departmentName || '暂无地址' }}
              </div>
            </div>
            <button class="btn-book" @click="() => bookDoctor(doc)">在线预约</button>
          </div>
          
          <div class="see-more-card">
             <div class="icon-circle-bg">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
             </div>
             <h3>还有更多</h3>
             <button class="btn-see-more">浏览列表</button>
          </div>
        </div>
      </div>
    </section>

    <section class="list-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">热门搜索科室</h2>
        </div>
        <div class="scroll-container">
          <div class="specialty-card" v-for="(item, index) in specialties" :key="index" @click="goToDepartment(item.id)">
            <div class="specialty-icon-circle">
              <component :is="getDepartmentIconComponent(item.name)" />
            </div>
            <span class="specialty-name">{{ item.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="container">
        <h2 class="section-title center-text">为您匹配最合适的医生</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-illus illus-green">
               <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="1.5"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            </div>
            <h3 class="feature-text">浏览各类专业医生<br>满足您的就医需求</h3>
            <button class="feature-btn" @click="goToDepartments">查看科室</button>
          </div>
          <div class="feature-card">
            <div class="feature-illus illus-yellow">
               <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="1.5"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
            </div>
            <h3 class="feature-text">查看真实患者评价<br>选择更放心</h3>
            <button class="feature-btn" @click="goToDoctors">查看医生</button>
          </div>
          <div class="feature-card">
             <div class="feature-illus illus-mint">
               <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="1.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <h3 class="feature-text">今天即可在线预约<br>方便快捷</h3>
            <button class="feature-btn" @click="goToSchedule">查看排期</button>
          </div>
        </div>
      </div>
    </section>

    <section class="app-promo-section">
      <div class="yellow-blob"></div>
      <div class="container app-promo-container">
        <div class="app-text-content">
          <h2 class="app-title">数千名医生，一个应用搞定</h2>
          <p class="app-subtitle">优医 App 是预约医生、管理健康档案最快、最简单的方式。</p>
          <div class="store-buttons">
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M17.523 15.341a.5.5 0 0 0-.5.5v3.659a.5.5 0 0 0 .5.5h1.454a.5.5 0 0 0 .5-.5v-3.659a.5.5 0 0 0-.5-.5h-1.454zm-12.5 0a.5.5 0 0 0-.5.5v3.659a.5.5 0 0 0 .5.5h1.454a.5.5 0 0 0 .5-.5v-3.659a.5.5 0 0 0-.5-.5H5.023zM15.5 5.5l1.5-2.5-1-.5-1.5 2.5c-1-.5-2.5-.5-3.5 0L9.5 2.5l-1 .5L10 5.5C8 6.5 7 8.5 7 10.5h10c0-2-1-4-2.5-5zM9.5 9a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm5 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zM7 11.5v7a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-7H7z"/></svg>
              </div>
              <span class="download-label">Android</span>
            </div>
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.81-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M13 3.5c.73-.83 1.94-1.46 2.94-1.5.13 1.17-.34 2.35-1.04 3.19-.69.85-1.83 1.51-2.95 1.42-.15-1.15.41-2.35 1.05-3.11z"/></svg>
              </div>
              <span class="download-label">iOS</span>
            </div>
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <rect x="3" y="3" width="18" height="18" rx="3" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  <text x="5" y="10" font-size="5" font-weight="bold" fill="currentColor">H</text>
                  <text x="13" y="10" font-size="5" font-weight="bold" fill="currentColor">M</text>
                  <text x="5" y="18" font-size="5" font-weight="bold" fill="currentColor" text-decoration="underline">O</text>
                  <text x="13" y="18" font-size="5" font-weight="bold" fill="currentColor">S</text>
                </svg>
              </div>
              <span class="download-label">HarmonyOS</span>
            </div>
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 12V6.75l6-1.32v6.48L3 12zm17-9v8.75l-10 .15V5.21L20 3zM3 13l6 .09v6.81l-6-1.15V13zm17 .25V22l-10-1.91V13.1l10 .15z"/></svg>
              </div>
              <span class="download-label">Windows</span>
            </div>
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.81-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M13 3.5c.73-.83 1.94-1.46 2.94-1.5.13 1.17-.34 2.35-1.04 3.19-.69.85-1.83 1.51-2.95 1.42-.15-1.15.41-2.35 1.05-3.11z"/></svg>
              </div>
              <span class="download-label">macOS</span>
            </div>
            <div class="download-item">
              <div class="download-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12.504 0c-.155 0-.311.001-.465.003-.658.014-1.317.07-1.97.18-.654.11-1.296.28-1.914.51-.617.23-1.21.52-1.77.87-.56.35-1.08.76-1.55 1.23-.47.47-.88.99-1.23 1.55-.35.56-.64 1.15-.87 1.77-.23.62-.4 1.26-.51 1.91-.11.65-.17 1.31-.18 1.97-.002.154-.003.31-.003.465v11.08c0 .155.001.311.003.465.01.658.07 1.317.18 1.97.11.654.28 1.296.51 1.914.23.617.52 1.21.87 1.77.35.56.76 1.08 1.23 1.55.47.47.99.88 1.55 1.23.56.35 1.15.64 1.77.87.62.23 1.26.4 1.91.51.65.11 1.31.17 1.97.18.154.002.31.003.465.003h11.08c.155 0 .311-.001.465-.003.658-.01 1.317-.07 1.97-.18.654-.11 1.296-.28 1.914-.51.617-.23 1.21-.52 1.77-.87.56-.35 1.08-.76 1.55-1.23.47-.47.88-.99 1.23-1.55.35-.56.64-1.15.87-1.77.23-.62.4-1.26.51-1.91.11-.65.17-1.31.18-1.97.002-.154.003-.31.003-.465V12.504c0-.155-.001-.311-.003-.465-.01-.658-.07-1.317-.18-1.97-.11-.654-.28-1.296-.51-1.914-.23-.617-.52-1.21-.87-1.77-.35-.56-.76-1.08-1.23-1.55-.47-.47-.99-.88-1.55-1.23-.56-.35-1.15-.64-1.77-.87-.62-.23-1.26-.4-1.91-.51-.65-.11-1.31-.17-1.97-.18-.154-.002-.31-.003-.465-.003H12.504zm-.004 3.5h11c.276 0 .5.224.5.5v16c0 .276-.224.5-.5.5h-11c-.276 0-.5-.224-.5-.5V4c0-.276.224-.5.5-.5z"/></svg>
              </div>
              <span class="download-label">网页版</span>
            </div>
          </div>
        </div>
        <div class="phone-mockup-container">
          <!-- 手绘风格箭头 - 从文字区域指向手机 -->
          <div class="arrow-hand-drawn">
            <svg width="350" height="250" viewBox="0 0 350 250" fill="none" stroke="#2A2A2A" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
              <!-- 箭头从左侧文字区域指向手机左上角 -->
              <path d="M15 50 Q 70 40, 125 45 Q 180 50, 235 60 Q 270 70, 295 85 Q 315 100, 330 120" 
                    style="stroke-dasharray: 6,4; opacity: 0.8;" />
              <path d="M327 118 L 337 123 L 332 113" />
            </svg>
          </div>
          <div class="phone-mockup">
            <div class="phone-notch"></div>
            <div class="phone-screen">
              <div class="app-header">
                <div class="app-logo-small">优</div>
                <div class="app-title-text">我的预约</div>
              </div>
              <div class="app-content">
                <div class="app-card" v-for="(appointment, index) in mockAppointments" :key="index">
                  <div class="app-card-row">
                    <div class="app-avatar-placeholder">
                      <img :src="appointment.avatar" :alt="appointment.doctor" />
                    </div>
                    <div class="app-info">
                      <div class="app-doctor-name">{{ appointment.doctor }}</div>
                      <div class="app-time">{{ appointment.time }}</div>
                      <div class="app-department">{{ appointment.department }}</div>
                    </div>
                    <div class="app-status" :class="appointment.status">{{ appointment.statusText }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <footer class="main-footer">
      <div class="container footer-grid">
        <div class="footer-col">
          <h4>关于优医</h4>
          <a href="#">关于我们</a><a href="#">联系我们</a>
        </div>
        <div class="footer-col">
          <h4>发现</h4>
          <a href="#">按科室</a><a href="#">按地区</a>
        </div>
        <div class="footer-col social-col">
          <p>© 2025 优医预约系统</p>
        </div>
      </div>
    </footer>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, h } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { getCurrentLocation, getLocationByIP } from '@/utils/location';

const router = useRouter();
const searchKeyword = ref('');
const searchLocation = ref('');
const isLocating = ref(false);
const locationError = ref('');
const currentLocation = ref(''); // 左上角显示的定位信息

// 自动获取位置
const autoLocate = async () => {
  isLocating.value = true;
  locationError.value = '';
  
  try {
    // 先尝试精确定位
    const location = await getCurrentLocation();
    const locationStr = location.district 
      ? `${location.city}${location.district}` 
      : location.city;
    searchLocation.value = locationStr;
    currentLocation.value = locationStr; // 更新左上角显示
  } catch (error) {
    console.warn('精确定位失败，尝试IP定位:', error.message);
    // 精确定位失败，尝试IP定位
    try {
      const ipLocation = await getLocationByIP();
      if (ipLocation && ipLocation.city) {
        searchLocation.value = ipLocation.city;
        currentLocation.value = ipLocation.city; // 更新左上角显示
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

// 华为风格动效 - 滚动和鼠标跟随
const scrollY = ref(0);
const isHeaderScrolled = ref(false);
const mouseX = ref(0);
const mouseY = ref(0);

const handleScroll = () => {
  scrollY.value = window.scrollY;
  isHeaderScrolled.value = window.scrollY > 50;
  
  // 滚动触发动画
  const elements = document.querySelectorAll('.scroll-animate');
  elements.forEach(el => {
    const rect = el.getBoundingClientRect();
    const isVisible = rect.top < window.innerHeight * 0.85;
    if (isVisible) {
      el.classList.add('animate-in');
    }
  });
};

const handleMouseMove = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 30;
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 30;
};

// 粒子样式生成
const getParticleStyle = (n) => {
  const size = Math.random() * 6 + 2;
  const left = Math.random() * 100;
  const delay = Math.random() * 20;
  const duration = Math.random() * 10 + 15;
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  };
};

// 流星样式生成
const getShootingStarStyle = (n) => {
  const top = Math.random() * 50;
  const left = Math.random() * 100;
  const delay = Math.random() * 15 + n * 3;
  const duration = Math.random() * 2 + 1;
  return {
    top: `${top}%`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  };
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  handleScroll();
  fetchLandingData();
  autoLocate(); // 自动获取用户位置
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

const goLogin = () => router.push('/login');
const goRegister = () => router.push('/register');
const findCare = () => {
  // 搜索按钮跳转到搜索结果页面，传递关键词和地区
  const query = {};
  if (searchKeyword.value && searchKeyword.value.trim()) {
    query.keyword = searchKeyword.value.trim();
  }
  if (searchLocation.value && searchLocation.value.trim()) {
    query.location = searchLocation.value.trim();
  }
  // 即使没有输入任何内容，也跳转到搜索结果页面显示所有医生
  router.push({ path: '/search-results', query });
};

// 查看科室 - 跳转到检查项目选择页面
const goToDepartments = () => {
  router.push('/search-triage');
};

// 查看医生 - 跳转到检查项目选择页面
const goToDoctors = () => {
  router.push('/search-triage');
};

// 查看排期 - 跳转到检查项目选择页面
const goToSchedule = () => {
  router.push('/search-triage');
};

// 预约医生 - 跳转到搜索结果页面，筛选5星医生并优先显示选中的医生
const bookDoctor = (doctor) => {
  router.push({
    path: '/search-results',
    query: {
      minRating: '5.0',
      priorityDoctorId: doctor.id
    }
  });
};

const goToDepartment = (deptId) => {
  router.push({
    path: '/search-results',
    query: { departmentId: deptId }
  });
};

// 科室图标组件 - 使用SVG图标，确保所有科室都有图标
const getDepartmentIconComponent = (name) => {
  if (!name) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: 'none', 
      stroke: 'currentColor', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('path', { d: 'M12 2L2 7l10 5 10-5-10-5z' }),
      h('path', { d: 'M2 17l10 5 10-5' }),
      h('path', { d: 'M2 12l10 5 10-5' })
    ]);
  }
  
  const normalizedName = name.trim().replace(/\s+/g, '');
  
  // 心血管内科 - 红色心脏图标
  if (normalizedName.includes('心血管')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#E91E63', 
      stroke: '#C2185B', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('path', { d: 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z' })
    ]);
  }
  
  // 骨科 - 棕色骨头图标
  if (normalizedName.includes('骨科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#8D6E63', 
      stroke: '#6D4C41', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('path', { d: 'M16.5 8.5l-5-5a2.5 2.5 0 0 0-3.54 0l-2 2a2.5 2.5 0 0 0 0 3.54l5 5a2.5 2.5 0 0 0 3.54 0l2-2a2.5 2.5 0 0 0 0-3.54z' }),
      h('path', { d: 'M8.5 15.5l-5-5a2.5 2.5 0 0 0-3.54 0l-2 2a2.5 2.5 0 0 0 0 3.54l5 5a2.5 2.5 0 0 0 3.54 0l2-2a2.5 2.5 0 0 0 0-3.54z' })
    ]);
  }
  
  // 眼科 - 蓝色眼睛图标
  if (normalizedName.includes('眼科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#2196F3', 
      stroke: '#1976D2', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('path', { d: 'M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z' }),
      h('circle', { cx: '12', cy: '12', r: '3', fill: '#1976D2' })
    ]);
  }
  
  // 妇科 - 粉色女性符号
  if (normalizedName.includes('妇科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#E91E63', 
      stroke: '#C2185B', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('circle', { cx: '12', cy: '8', r: '5' }),
      h('path', { d: 'M12 13v8' }),
      h('path', { d: 'M8 17h8' })
    ]);
  }
  
  // 皮肤科 - 橙色毛囊图标
  if (normalizedName.includes('皮肤科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#FF9800', 
      stroke: '#F57C00', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('circle', { cx: '12', cy: '8', r: '2' }),
      h('path', { d: 'M12 10v12' }),
      h('path', { d: 'M8 14h8' })
    ]);
  }
  
  // 儿科 - 橙色婴儿图标
  if (normalizedName.includes('儿科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#FF9800', 
      stroke: '#F57C00', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('circle', { cx: '12', cy: '10', r: '4' }),
      h('path', { d: 'M12 6v4' }),
      h('path', { d: 'M12 14v4' }),
      h('path', { d: 'M8 12h8' })
    ]);
  }
  
  // 口腔科 - 浅蓝色牙齿图标
  if (normalizedName.includes('口腔科')) {
    return () => h('svg', { 
      class: 'spec-svg',
      viewBox: '0 0 24 24', 
      fill: '#4FC3F7', 
      stroke: '#0288D1', 
      'stroke-width': '1.5',
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round'
    }, [
      h('rect', { x: '6', y: '8', width: '12', height: '12', rx: '2' }),
      h('path', { d: 'M6 10h12' }),
      h('path', { d: 'M9 14h6' })
    ]);
  }
  
  // 默认图标
  return () => h('svg', { 
    class: 'spec-svg',
    viewBox: '0 0 24 24', 
    fill: 'none', 
    stroke: 'currentColor', 
    'stroke-width': '1.5',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round'
  }, [
    h('path', { d: 'M12 2L2 7l10 5 10-5-10-5z' }),
    h('path', { d: 'M2 17l10 5 10-5' }),
    h('path', { d: 'M2 12l10 5 10-5' })
  ]);
};

const topDoctors = ref([]);
const specialties = ref([]);

// 处理图标加载错误
const handleIconError = (event) => {
  // 如果图标加载失败，隐藏img标签，显示默认SVG
  if (event.target) {
    event.target.style.display = 'none';
    // 查找同级的SVG元素
    const parent = event.target.parentElement;
    if (parent) {
      const svg = parent.querySelector('.spec-svg');
      if (svg) {
        svg.style.display = 'block';
      }
    }
  }
};

// 手机mockup中的预约数据
const mockAppointments = ref([
  {
    doctor: '张医生',
    time: '今天 14:00',
    department: '内科',
    status: 'pending',
    statusText: '待就诊',
    avatar: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=100&h=100&fit=crop'
  },
  {
    doctor: '李医生',
    time: '明天 10:30',
    department: '外科',
    status: 'confirmed',
    statusText: '已确认',
    avatar: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=100&h=100&fit=crop'
  },
  {
    doctor: '王医生',
    time: '后天 16:00',
    department: '儿科',
    status: 'pending',
    statusText: '待就诊',
    avatar: 'https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=100&h=100&fit=crop'
  }
]);

const fetchLandingData = async () => {
  try {
    // Use allSettled to ensure single interface failure doesn't affect others
    const results = await Promise.allSettled([
      request.get('/api/doctors/top', { params: { limit: 10 } }),
      request.get('/api/departments')
    ]);
    
    // The interceptor in `request` already unwraps the .data property from the response.
    // The value of a fulfilled promise is the data itself.
    topDoctors.value = (results[0].status === 'fulfilled' && results[0].value) ? results[0].value : [];
    specialties.value = (results[1].status === 'fulfilled' && results[1].value) ? results[1].value : [];

    results.forEach((res, i) => {
      if (res.status === 'rejected') {
        // The error message is now in res.reason.message due to the interceptor
        console.error(`API ${i} failed:`, res.reason.message);
      }
    });
  } catch (error) {
    console.error('Failed to fetch landing page data:', error);
  }
};
</script>

<style>
/* 全局设置 */
body { margin: 0; padding: 0; background-color: #fff; font-family: "Microsoft YaHei", "Heiti SC", -apple-system, BlinkMacSystemFont, sans-serif; color: #2A2A2A; font-size: 14px; }
</style>

<style scoped>
/* ========== 华为风格动效 ========== */
/* 页面加载动画 */
.landing-page { 
  width: 100%; 
  overflow-x: hidden; 
  animation: pageLoad 0.8s ease-out;
  position: relative;
  background: linear-gradient(135deg, #FFF9E5 0%, #FFFDF5 50%, #FFF9E5 100%);
  min-height: 100vh;
}

/* ========== 星河背景 ========== */
.starfield {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
  display: none; /* 浅色模式下隐藏星空 */
}

/* 星星层 - 使用 box-shadow 创建多个星星 */
.stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 200%;
  background: transparent;
}

.stars-1 {
  background-image: 
    radial-gradient(3px 3px at 20px 30px, #fff, transparent),
    radial-gradient(3px 3px at 40px 70px, rgba(255,255,255,0.9), transparent),
    radial-gradient(2px 2px at 90px 40px, #fff, transparent),
    radial-gradient(3px 3px at 160px 120px, rgba(255,215,0,1), transparent),
    radial-gradient(2px 2px at 230px 80px, #fff, transparent),
    radial-gradient(3px 3px at 300px 150px, rgba(255,255,255,0.9), transparent),
    radial-gradient(2px 2px at 370px 50px, #fff, transparent),
    radial-gradient(3px 3px at 450px 180px, rgba(255,215,0,1), transparent),
    radial-gradient(2px 2px at 520px 90px, #fff, transparent),
    radial-gradient(3px 3px at 600px 130px, rgba(255,255,255,1), transparent),
    radial-gradient(2px 2px at 80px 160px, rgba(100,149,237,0.9), transparent),
    radial-gradient(3px 3px at 200px 200px, rgba(255,182,193,0.9), transparent),
    radial-gradient(2px 2px at 350px 220px, rgba(144,238,144,0.8), transparent),
    radial-gradient(3px 3px at 500px 250px, rgba(255,215,0,0.9), transparent),
    radial-gradient(2px 2px at 150px 280px, #fff, transparent),
    radial-gradient(3px 3px at 420px 300px, rgba(255,255,255,0.9), transparent);
  background-size: 650px 350px;
  animation: starsMove 60s linear infinite;
}

.stars-2 {
  background-image: 
    radial-gradient(2px 2px at 50px 60px, rgba(255,255,255,0.8), transparent),
    radial-gradient(3px 3px at 120px 100px, rgba(255,215,0,0.9), transparent),
    radial-gradient(2px 2px at 200px 40px, rgba(255,255,255,0.7), transparent),
    radial-gradient(2px 2px at 280px 140px, #fff, transparent),
    radial-gradient(3px 3px at 350px 70px, rgba(255,215,0,0.8), transparent),
    radial-gradient(2px 2px at 420px 160px, rgba(255,255,255,0.9), transparent),
    radial-gradient(2px 2px at 500px 30px, #fff, transparent),
    radial-gradient(3px 3px at 580px 110px, rgba(255,255,255,1), transparent),
    radial-gradient(2px 2px at 100px 180px, rgba(173,216,230,0.8), transparent),
    radial-gradient(2px 2px at 250px 220px, rgba(255,192,203,0.7), transparent),
    radial-gradient(3px 3px at 400px 260px, rgba(255,215,0,0.8), transparent),
    radial-gradient(2px 2px at 550px 200px, #fff, transparent);
  background-size: 620px 300px;
  animation: starsMove 80s linear infinite;
  opacity: 0.9;
}

.stars-3 {
  background-image: 
    radial-gradient(1px 1px at 30px 80px, rgba(255,255,255,0.6), transparent),
    radial-gradient(2px 2px at 100px 30px, rgba(255,215,0,0.7), transparent),
    radial-gradient(1px 1px at 180px 120px, rgba(255,255,255,0.5), transparent),
    radial-gradient(2px 2px at 260px 60px, #fff, transparent),
    radial-gradient(1px 1px at 340px 150px, rgba(255,255,255,0.6), transparent),
    radial-gradient(2px 2px at 420px 90px, rgba(255,215,0,0.6), transparent),
    radial-gradient(1px 1px at 500px 40px, rgba(255,255,255,0.7), transparent),
    radial-gradient(1px 1px at 70px 200px, rgba(255,255,255,0.5), transparent),
    radial-gradient(2px 2px at 220px 240px, rgba(255,215,0,0.6), transparent),
    radial-gradient(1px 1px at 380px 280px, #fff, transparent);
  background-size: 550px 320px;
  animation: starsMove 100s linear infinite;
  opacity: 0.7;
}

@keyframes starsMove {
  from { transform: translateY(0); }
  to { transform: translateY(-200px); }
}

/* 流星 */
.shooting-stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.shooting-star {
  position: absolute;
  width: 150px;
  height: 3px;
  background: linear-gradient(90deg, rgba(255,215,0,0), rgba(255,215,0,1), rgba(255,255,255,1));
  border-radius: 50%;
  transform: rotate(-45deg);
  opacity: 0;
  animation: shootingStar 4s ease-in-out infinite;
}

.shooting-star::after {
  content: '';
  position: absolute;
  right: 0;
  top: -2px;
  width: 8px;
  height: 8px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 15px 4px rgba(255,215,0,1), 0 0 30px 8px rgba(255,215,0,0.6), 0 0 50px 12px rgba(255,152,0,0.3);
}

@keyframes shootingStar {
  0% {
    opacity: 0;
    transform: rotate(-45deg) translateX(0);
  }
  5% {
    opacity: 1;
  }
  35% {
    opacity: 1;
    transform: rotate(-45deg) translateX(-400px);
  }
  100% {
    opacity: 0;
    transform: rotate(-45deg) translateX(-600px);
  }
}

/* 星云效果 */
.nebula {
  position: absolute;
  width: 800px;
  height: 800px;
  background: radial-gradient(ellipse at center, 
    rgba(255,215,0,0.15) 0%, 
    rgba(255,152,0,0.1) 25%, 
    rgba(255,87,34,0.05) 50%,
    transparent 70%);
  border-radius: 50%;
  top: -200px;
  right: -200px;
  animation: nebulaPulse 8s ease-in-out infinite;
  filter: blur(60px);
}

.nebula-2 {
  width: 700px;
  height: 700px;
  background: radial-gradient(ellipse at center, 
    rgba(138,43,226,0.12) 0%, 
    rgba(75,0,130,0.08) 25%, 
    rgba(123,104,238,0.04) 50%,
    transparent 70%);
  top: auto;
  bottom: -200px;
  left: -200px;
  right: auto;
  animation: nebulaPulse 10s ease-in-out infinite reverse;
}

.nebula-3 {
  width: 500px;
  height: 500px;
  background: radial-gradient(ellipse at center, 
    rgba(0,191,255,0.1) 0%, 
    rgba(30,144,255,0.06) 25%, 
    rgba(65,105,225,0.03) 50%,
    transparent 70%);
  top: 40%;
  left: 30%;
  right: auto;
  animation: nebulaPulse 12s ease-in-out infinite;
  animation-delay: -4s;
}

/* 新增：银河带效果 */
.starfield::before {
  content: '';
  position: absolute;
  top: 20%;
  left: -10%;
  width: 120%;
  height: 200px;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(255,255,255,0.03) 20%,
    rgba(255,215,0,0.05) 40%,
    rgba(255,255,255,0.08) 50%,
    rgba(255,215,0,0.05) 60%,
    rgba(255,255,255,0.03) 80%,
    transparent 100%);
  transform: rotate(-15deg);
  filter: blur(30px);
  animation: milkyWay 20s ease-in-out infinite;
}

@keyframes milkyWay {
  0%, 100% { opacity: 0.6; transform: rotate(-15deg) translateX(0); }
  50% { opacity: 1; transform: rotate(-15deg) translateX(50px); }
}

@keyframes nebulaPulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

/* 闪烁星星效果 */
.starfield::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(4px 4px at 100px 50px, rgba(255,255,255,0.9), transparent),
    radial-gradient(3px 3px at 300px 150px, rgba(255,215,0,0.8), transparent),
    radial-gradient(4px 4px at 500px 80px, rgba(255,255,255,0.9), transparent),
    radial-gradient(3px 3px at 700px 200px, rgba(100,149,237,0.8), transparent),
    radial-gradient(4px 4px at 200px 300px, rgba(255,182,193,0.8), transparent),
    radial-gradient(3px 3px at 600px 350px, rgba(255,215,0,0.9), transparent),
    radial-gradient(4px 4px at 400px 450px, rgba(255,255,255,0.8), transparent),
    radial-gradient(3px 3px at 150px 500px, rgba(144,238,144,0.7), transparent);
  background-size: 800px 600px;
  animation: twinkle 3s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

/* 背景粒子 */
.particles-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.4), rgba(255, 152, 0, 0.3));
  border-radius: 50%;
  bottom: -20px;
  animation: particleRise linear infinite;
}

@keyframes particleRise {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) rotate(720deg);
    opacity: 0;
  }
}

/* 动态光晕 */
.hero-glow {
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.25) 0%, transparent 70%);
  border-radius: 50%;
  top: -100px;
  right: 10%;
  pointer-events: none;
  animation: glowPulse 4s ease-in-out infinite;
  transition: transform 0.3s ease-out;
}

.hero-glow-2 {
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 152, 0, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  bottom: -50px;
  left: 5%;
  pointer-events: none;
  animation: glowPulse 5s ease-in-out infinite reverse;
  transition: transform 0.3s ease-out;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

@keyframes pageLoad {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 滚动触发动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(60px);
  transition: opacity 0.8s cubic-bezier(0.4, 0, 0.2, 1), transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.scroll-animate.animate-in {
  opacity: 1;
  transform: translateY(0);
}

/* 淡入上浮动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 下滑动画 */
@keyframes slideDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Logo 3D旋转脉冲 */
@keyframes logoPulse {
  0%, 100% { transform: scale(1) rotateY(0deg); }
  25% { transform: scale(1.1) rotateY(10deg); }
  50% { transform: scale(1.05) rotateY(0deg); }
  75% { transform: scale(1.1) rotateY(-10deg); }
}

/* 浮动动画 */
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-10px) rotate(2deg); }
  50% { transform: translateY(-20px) rotate(0deg); }
  75% { transform: translateY(-10px) rotate(-2deg); }
}

/* 文字逐字显示 */
@keyframes textReveal {
  from { 
    clip-path: inset(0 100% 0 0);
    opacity: 0;
  }
  to { 
    clip-path: inset(0 0 0 0);
    opacity: 1;
  }
}

/* 渐变流动 */
@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 光泽扫过 */
@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 200%; }
}

/* 弹跳进入 */
@keyframes bounceIn {
  0% { opacity: 0; transform: scale(0.3); }
  50% { transform: scale(1.05); }
  70% { transform: scale(0.9); }
  100% { opacity: 1; transform: scale(1); }
}

/* ========== 基础样式 ========== */
.container { max-width: 1080px; margin: 0 auto; padding: 0 24px; position: relative; height: 100%; }

/* --- Header 动效增强 --- */
.cream-wrapper { 
  background: linear-gradient(180deg, #FFF9E5 0%, rgba(255, 249, 229, 0.95) 100%);
  position: relative;
  animation: slideDown 0.6s ease-out;
  overflow: hidden;
}

.main-header { 
  height: 60px; 
  display: flex; 
  align-items: center; 
  position: relative; 
  z-index: 50;
  animation: slideDown 0.6s ease-out;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.main-header.scrolled {
  background: rgba(255, 249, 229, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.nav-container { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.logo-area { 
  display: flex; 
  align-items: center; 
  gap: 6px; 
  cursor: pointer;
  transition: transform 0.3s ease;
  perspective: 1000px;
}
.logo-area:hover { transform: scale(1.08); }
.logo-box { 
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
  width: 28px; 
  height: 28px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  font-weight: bold; 
  border-radius: 6px 0 6px 0; 
  color: #000; 
  font-size: 14px;
  animation: logoPulse 3s ease-in-out infinite;
  box-shadow: 0 4px 12px rgba(255, 211, 0, 0.4);
  position: relative;
  overflow: hidden;
}
.logo-box::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255,255,255,0.3), transparent);
  animation: shimmer 3s infinite;
}
.logo-char {
  position: relative;
  z-index: 1;
}
.logo-text { font-size: 18px; font-weight: 700; color: #2A2A2A; letter-spacing: -0.5px; }

/* 左上角定位显示 */
.location-display {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 16px;
  padding: 4px 10px;
  background: rgba(255, 211, 0, 0.15);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 211, 0, 0.3);
}
.location-display:hover {
  background: rgba(255, 211, 0, 0.25);
  transform: scale(1.02);
}
.location-display.locating {
  background: rgba(200, 200, 200, 0.15);
  border-color: rgba(200, 200, 200, 0.3);
}
.location-display.failed {
  background: rgba(200, 200, 200, 0.1);
  border-color: rgba(200, 200, 200, 0.2);
}
.location-display.failed .location-icon {
  color: #999;
}
.location-display.failed .location-text {
  color: #999;
}
.location-icon {
  width: 14px;
  height: 14px;
  color: #FF9800;
  flex-shrink: 0;
}
.location-icon.spinning {
  animation: spin 1.5s linear infinite;
  color: #999;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.location-text {
  font-size: 12px;
  color: #666;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-nav { display: flex; align-items: center; gap: 20px; }
.nav-link { 
  text-decoration: none; 
  color: #2A2A2A; 
  font-size: 14px; 
  font-weight: 500; 
  display: flex; 
  align-items: center; 
  gap: 4px;
  position: relative;
  transition: color 0.3s ease;
}
.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: #FFD300;
  transition: width 0.3s ease;
}
.nav-link:hover { color: #000; }
.nav-link:hover::after { width: 100%; }
.icon-xs { width: 10px; height: 10px; margin-top: 1px; opacity: 0.6; }
.nav-divider { width: 1px; height: 20px; background-color: #DDD; margin: 0 2px; }
.btn-signup { 
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
  background-size: 200% 200%;
  border: none; 
  padding: 10px 22px; 
  font-size: 14px; 
  font-weight: 700; 
  border-radius: 25px; 
  cursor: pointer; 
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); 
  color: #2A2A2A;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 211, 0, 0.3);
  animation: gradientFlow 3s ease infinite;
}
.btn-signup span { position: relative; z-index: 1; }
.btn-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.6s ease, height 0.6s ease;
}
.btn-signup:hover .btn-ripple {
  width: 300px;
  height: 300px;
}
.btn-signup:hover { 
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(255, 211, 0, 0.5);
}
.btn-signup:active {
  transform: translateY(-1px) scale(1.02);
}

/* --- 2. Hero 区域 (您要求的窄高度 + 对齐修复) --- */
.hero-section { 
  position: relative; 
  /* 进一步收窄背景高度 */
  height: 320px; 
  padding-top: 0; /* 去掉 padding，完全靠 margin 控制位置 */
}

/* 文字区域 */
.hero-text { 
  z-index: 2; 
  max-width: 500px; 
  position: relative;
  margin-top: 40px;
}

/* Hero 徽章 */
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(255, 211, 0, 0.2);
  border-radius: 20px;
  font-size: 13px;
  color: #996600;
  font-weight: 600;
  margin-bottom: 16px;
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #FFD300, #FF9800);
  border-radius: 50%;
  animation: badgePulse 1.5s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.3); opacity: 0.7; }
}

.hero-text h1 { 
  font-size: 46px;
  line-height: 1.15; 
  font-weight: 700; 
  color: #2A2A2A; 
  margin: 0 0 16px 0; 
  letter-spacing: -1px;
}

.text-line {
  display: inline-block;
  color: #2A2A2A;
  position: relative;
  animation: textReveal 0.8s ease-out both;
  background: linear-gradient(90deg, #2A2A2A 0%, #2A2A2A 40%, #fff 50%, #2A2A2A 60%, #2A2A2A 100%);
  background-size: 200% 100%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: textReveal 0.8s ease-out both, whiteShimmer 6s ease-in-out infinite;
}

@keyframes whiteShimmer {
  0%, 70% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.line-1 { animation-delay: 0.3s; }
.line-2 { animation-delay: 0.5s; }

.hero-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
  animation: fadeInUp 0.6s ease-out 0.7s both;
  letter-spacing: 2px;
}

/* 浮动装饰形状 */
.floating-shapes {
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
  transition: transform 0.3s ease-out;
}

.shape-1 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.3), rgba(255, 152, 0, 0.2));
  top: 20%;
  left: 5%;
  animation: float 8s ease-in-out infinite;
}

.shape-2 {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.25), rgba(255, 87, 34, 0.15));
  top: 60%;
  right: 25%;
  animation: float 6s ease-in-out infinite reverse;
}

.shape-3 {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.4), rgba(255, 193, 7, 0.2));
  bottom: 30%;
  left: 15%;
  animation: float 7s ease-in-out infinite;
  animation-delay: -2s;
}

/* 插图调整 */
.hero-image-box { 
  position: absolute; 
  right: 60px; 
  top: -10px; 
  width: 380px; 
  z-index: 1; 
  pointer-events: none;
  animation: float 6s ease-in-out infinite;
}

.image-glow {
  position: absolute;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 211, 0, 0.3) 0%, transparent 60%);
  animation: glowPulse 3s ease-in-out infinite;
}

.blended-image { 
  width: 100%; 
  height: auto; 
  mix-blend-mode: multiply; 
  display: block; 
  filter: saturate(1.05) contrast(1.02); 
}

/* --- 3. 搜索栏 (完全在黄色区域内部) --- */
.search-bar-wrapper { 
  position: absolute; 
  /* 紧贴底部或微调 */
  bottom: 15px; 
  left: 0; 
  right: 0; 
  z-index: 10; 
  padding: 0 24px; 
}

.search-bar { 
  max-width: 1080px; 
  margin: 0 auto; 
  background: #fff; 
  border-radius: 6px; 
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06); 
  display: flex; 
  align-items: center; 
  padding: 6px; 
  height: 60px;
  box-sizing: border-box;
  animation: fadeInUp 0.8s ease-out 0.5s both;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.search-bar:hover {
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.input-item { flex: 1; display: flex; align-items: center; padding: 0 12px; height: 100%; }
.icon-wrap { margin-right: 10px; opacity: 0.4; display: flex; align-items: center; }
.icon-sm { width: 18px; height: 18px; }
.text-area { display: flex; flex-direction: column; width: 100%; justify-content: center; }
.text-area label { font-size: 10px; font-weight: 700; color: #666; margin-bottom: 2px; text-transform: uppercase; letter-spacing: 0.5px; }
.text-area input { border: none; font-size: 14px; color: #2A2A2A; width: 100%; outline: none; font-weight: 500; padding: 0; background: transparent; }
.line { width: 1px; height: 32px; background: #E8E8E8; flex-shrink: 0; }

.btn-search { 
  background: #FFD300; 
  border: none; 
  height: 48px; 
  width: 48px; 
  border-radius: 4px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  margin-left: 6px; 
  cursor: pointer; 
  flex-shrink: 0; 
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.btn-search::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255,255,255,0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
}
.btn-search:hover::before {
  width: 150%;
  height: 150%;
}
.icon-search-btn { width: 20px; height: 20px; position: relative; z-index: 1; transition: transform 0.3s ease; }
.btn-search:hover .icon-search-btn { transform: scale(1.2) rotate(90deg); }
.btn-search:hover { 
  background: linear-gradient(135deg, #FF9800 0%, #FFD300 100%);
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(255, 211, 0, 0.5);
}

/* 滚动提示 */
.scroll-hint {
  position: absolute;
  bottom: -80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  animation: fadeInUp 0.6s ease-out 1.2s both;
}

.mouse {
  width: 24px;
  height: 40px;
  border: 2px solid rgba(42, 42, 42, 0.3);
  border-radius: 12px;
  position: relative;
}

.wheel {
  width: 4px;
  height: 8px;
  background: #FFD300;
  border-radius: 2px;
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  animation: scrollWheel 1.5s ease-in-out infinite;
}

@keyframes scrollWheel {
  0%, 100% { opacity: 1; transform: translateX(-50%) translateY(0); }
  50% { opacity: 0.3; transform: translateX(-50%) translateY(12px); }
}

.scroll-hint span {
  font-size: 12px;
  color: #999;
  letter-spacing: 1px;
}

/* --- 列表通用 --- */
.list-section { 
  padding: 50px 0; 
  background: linear-gradient(180deg, #fff 0%, #FAFAFA 100%);
  position: relative;
  z-index: 1;
}
.list-section.grey-bg { background: linear-gradient(180deg, #FAFAFA 0%, #fff 100%); }

.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.section-title { font-size: 20px; font-weight: 700; color: #2A2A2A; margin: 0 0 6px 0; }
.section-subtitle { font-size: 13px; color: #666; margin: 0; }
.see-all { font-size: 13px; color: #2A2A2A; text-decoration: none; font-weight: 600; }
.scroll-container { display: flex; gap: 20px; overflow-x: auto; padding-bottom: 16px; scroll-snap-type: x mandatory; scrollbar-width: none; }
.scroll-container::-webkit-scrollbar { display: none; }

/* 医生卡片 */
.doctor-card { 
  min-width: 260px; 
  background: #fff; 
  border: 1px solid #E8E8E8; 
  border-radius: 12px; 
  padding: 16px; 
  display: flex; 
  flex-direction: column; 
  cursor: pointer; 
  scroll-snap-align: start; 
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: bounceIn 0.6s ease-out both;
}
.card-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 211, 0, 0.2), transparent);
  transform: skewX(-20deg);
  transition: left 0.8s ease;
  pointer-events: none;
}
.doctor-card:hover .card-shine { left: 200%; }
.doctor-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FFD300, #FF9800, #FFD300);
  background-size: 200% 100%;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s ease;
  animation: gradientFlow 2s ease infinite;
}
.doctor-card:hover::before { transform: scaleX(1); }
.doctor-card:hover { 
  box-shadow: 0 20px 50px rgba(0,0,0,0.15);
  transform: translateY(-10px) scale(1.02);
  border-color: #FFD300;
}
.card-top { display: flex; gap: 12px; margin-bottom: 12px; }
.avatar-wrapper { position: relative; }
.avatar { width: 50px; height: 50px; border-radius: 50%; object-fit: cover; border: 1px solid #f0f0f0; }
.video-badge { position: absolute; bottom: -2px; right: -2px; background: #fff; border-radius: 50%; padding: 3px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); display: flex; }
.doc-name { font-size: 15px; font-weight: 700; margin: 0 0 2px 0; color: #2A2A2A; }
.doc-title { font-size: 12px; color: #666; margin: 0 0 4px 0; }
.rating { display: flex; align-items: center; font-size: 12px; }
.star-icon { width: 12px; height: 12px; color: #FFD300; margin-right: 2px; }
.score { font-weight: 700; margin-right: 4px; }
.reviews { color: #888; }
.card-body { margin-bottom: 16px; flex: 1; }
.location-row { font-size: 12px; color: #666; margin-bottom: 6px; display: flex; align-items: center; gap: 4px; }
.availability { font-size: 12px; background-color: #FFF9E5; padding: 4px 8px; border-radius: 4px; display: inline-block; margin-top: 8px; }
.btn-book { 
  width: 100%; 
  background-color: #FFD300; 
  border: none; 
  padding: 10px; 
  font-weight: 700; 
  border-radius: 4px; 
  cursor: pointer; 
  font-size: 13px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.btn-book::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transition: left 0.5s ease;
}
.btn-book:hover::before { left: 100%; }
.btn-book:hover {
  background-color: #F4CA00;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(255, 211, 0, 0.4);
}

/* 查看更多卡片 */
.see-more-card { min-width: 260px; background: #fff; border: 1px solid #E8E8E8; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 16px; scroll-snap-align: start; }
.see-more-card h3 { color: #2A2A2A; }
.icon-circle-bg { width: 44px; height: 44px; background-color: #E8F0FE; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #1A73E8; margin-bottom: 12px; }
.btn-see-more { padding: 8px 20px; background: white; border: 1px solid #2A2A2A; border-radius: 4px; font-weight: 600; cursor: pointer; font-size: 13px; color: #2A2A2A; transition: all 0.3s ease; }
.btn-see-more:hover { background: #FFD300; border-color: #FFD300; }

/* 科室卡片 */
.specialty-card { 
  min-width: 120px; 
  height: 140px; 
  background: linear-gradient(135deg, #FFF9E5 0%, #FFF3CD 100%);
  border-radius: 12px; 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  justify-content: center; 
  cursor: pointer; 
  scroll-snap-align: start; 
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 2px solid transparent;
}
.specialty-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent);
  transition: left 0.6s ease;
}
.specialty-card:hover::before { left: 100%; }
.specialty-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255, 211, 0, 0.1), rgba(255, 152, 0, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}
.specialty-card:hover::after { opacity: 1; }
.specialty-card:hover { 
  transform: translateY(-10px) scale(1.05) rotateX(5deg);
  box-shadow: 0 20px 40px rgba(255, 211, 0, 0.3);
  border-color: #FFD300;
}
.specialty-icon-circle { 
  width: 60px; 
  height: 60px; 
  background: linear-gradient(135deg, #fff 0%, #f5f5f5 100%);
  border-radius: 50%; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  margin-bottom: 12px; 
  box-shadow: 0 4px 15px rgba(0,0,0,0.08); 
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.specialty-card:hover .specialty-icon-circle {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 8px 25px rgba(255, 211, 0, 0.4);
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
}
.specialty-card:hover .spec-svg {
  color: #fff !important;
  stroke: #fff !important;
}
.spec-svg { 
  width: 28px; 
  height: 28px; 
  color: #2A2A2A; 
  display: block;
}
.specialty-icon-img { 
  width: 36px; 
  height: 36px; 
  object-fit: contain; 
  display: block;
}
.specialty-name { font-size: 13px; font-weight: 600; color: #2A2A2A; }

/* --- 特性引导 --- */
.features-section { 
  background: linear-gradient(180deg, #FFF9E5 0%, rgba(255, 249, 229, 0.95) 100%);
  padding: 50px 0;
  position: relative;
  z-index: 1;
}
.center-text { text-align: center; margin-bottom: 30px; }
.features-grid { display: flex; gap: 24px; justify-content: center; flex-wrap: wrap; }
.feature-card { 
  background: #fff; 
  flex: 1; 
  max-width: 280px; 
  min-width: 240px; 
  border-radius: 12px; 
  padding: 30px 20px; 
  text-align: center; 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  box-shadow: 0 4px 12px rgba(0,0,0,0.02);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}
.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FFD300, #FF9800, #FFD300);
  background-size: 200% 100%;
  transform: scaleX(0);
  transition: transform 0.3s ease;
  animation: gradientShift 2s ease infinite;
}
.feature-card:hover::before { transform: scaleX(1); }
.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.1);
}
.feature-illus { 
  width: 64px; 
  height: 64px; 
  border-radius: 50%; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.feature-card:hover .feature-illus {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 12px 30px rgba(0,0,0,0.2);
}
.illus-green { background: linear-gradient(135deg, #E0F2F1 0%, #B2DFDB 100%); color: #00695C; }
.illus-yellow { background: linear-gradient(135deg, #FFF8E1 0%, #FFECB3 100%); color: #FF8F00; }
.illus-mint { background: linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%); color: #2E7D32; }
.feature-text { font-size: 16px; font-weight: 600; color: #2A2A2A; margin-bottom: 24px; line-height: 1.4; flex: 1; }
.feature-btn { 
  margin-top: auto; 
  background: white; 
  border: 1px solid #DDD; 
  padding: 8px 20px; 
  border-radius: 4px; 
  font-size: 13px; 
  font-weight: 600; 
  color: #2A2A2A; 
  cursor: pointer; 
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.feature-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 211, 0, 0.3), transparent);
  transition: left 0.5s ease;
}
.feature-btn:hover::before { left: 100%; }
.feature-btn:hover { 
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
  border-color: transparent;
  transform: scale(1.08);
  box-shadow: 0 8px 25px rgba(255, 211, 0, 0.4);
  color: #2A2A2A;
}

/* --- App 推广 --- */
.app-promo-section { 
  background: linear-gradient(135deg, #FACCB2 0%, #FFD8B8 100%);
  padding: 60px 0; 
  position: relative; 
  overflow: hidden;
  z-index: 1;
}
.yellow-blob { 
  position: absolute; 
  width: 600px; 
  height: 600px; 
  background: linear-gradient(135deg, #FFD300 0%, #FF9800 100%);
  border-radius: 50%; 
  right: -100px; 
  top: 50%; 
  transform: translateY(-50%); 
  z-index: 0;
  animation: blobPulse 8s ease-in-out infinite;
  box-shadow: 0 0 100px rgba(255, 211, 0, 0.5);
}

@keyframes blobPulse {
  0%, 100% { transform: translateY(-50%) scale(1); }
  50% { transform: translateY(-50%) scale(1.05); }
}
.app-promo-container { display: flex; align-items: center; justify-content: space-between; position: relative; z-index: 1; }
.app-text-content { max-width: 420px; }
.app-title { 
  font-size: 36px; 
  font-weight: 700; 
  margin-bottom: 16px; 
  line-height: 1.1;
  background: linear-gradient(135deg, #2A2A2A 0%, #666 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.app-subtitle { font-size: 16px; line-height: 1.5; margin-bottom: 24px; opacity: 0.9; color: #2A2A2A; }
.store-buttons { display: flex; gap: 24px; margin-bottom: 20px; flex-wrap: wrap; justify-content: flex-start; }
.download-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.download-item:hover { transform: translateY(-4px); }
.download-item:hover .download-icon { 
  background: rgba(255, 255, 255, 1); 
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}
.download-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.download-icon svg {
  width: 28px;
  height: 28px;
  color: #2A2A2A;
}
.download-label {
  font-size: 13px;
  font-weight: 600;
  color: #2A2A2A;
}
.store-btn { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  background-color: #000; 
  color: #fff; 
  border: none; 
  padding: 10px 16px; 
  border-radius: 6px; 
  font-weight: 700; 
  font-size: 13px; 
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}
.store-btn .store-icon {
  width: 18px;
  height: 18px;
}
.store-btn-ios { background: linear-gradient(135deg, #333 0%, #000 100%); }
.store-btn-android { background: linear-gradient(135deg, #3DDC84 0%, #2DA866 100%); color: #fff; }
.store-btn-windows { background: linear-gradient(135deg, #0078D4 0%, #005A9E 100%); }
.store-btn-mac { background: linear-gradient(135deg, #555 0%, #333 100%); }
.store-btn-harmony { background: linear-gradient(135deg, #E52E2E 0%, #C41E1E 100%); }
.store-btn:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.2); }
.store-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}
.store-btn:hover::before { left: 100%; }
.store-btn:hover {
  background-color: #333;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.3);
}
.arrow-hand-drawn {
  position: absolute;
  top: 30px;
  left: -200px;
  z-index: 2;
  pointer-events: none;
  opacity: 0.75;
}

.phone-mockup-container { 
  padding-right: 30px; 
  position: relative;
  display: flex;
  align-items: flex-end;
}
.phone-mockup { 
  width: 260px; 
  height: 520px; 
  background: #2A2A2A; 
  border-radius: 36px; 
  padding: 8px; 
  box-shadow: 0 16px 40px rgba(0,0,0,0.15); 
  position: relative; 
  transform: rotate(-3deg);
  z-index: 2;
  animation: float 8s ease-in-out infinite;
  transition: transform 0.3s ease;
}
.phone-mockup:hover {
  transform: rotate(0deg) scale(1.02);
}

.phone-notch { position: absolute; top: 0; left: 50%; transform: translateX(-50%); width: 100px; height: 22px; background: #2A2A2A; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px; z-index: 10; }
.phone-screen { background: #FFF9E5; width: 100%; height: 100%; border-radius: 28px; overflow: hidden; display: flex; flex-direction: column; }
.app-header { padding: 36px 16px 12px; background: #FFD300; display: flex; align-items: center; gap: 8px; }
.app-logo-small { font-weight: bold; font-size: 14px; }
.app-title-text { font-weight: bold; font-size: 14px; flex: 1; text-align: center; }
.app-content { flex: 1; padding: 12px; overflow-y: hidden; }
.app-card { background: #fff; padding: 10px; border-radius: 8px; margin-bottom: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.app-card-row { display: flex; align-items: center; gap: 10px; }
.app-avatar-placeholder { width: 40px; height: 40px; background: #EEE; border-radius: 50%; overflow: hidden; flex-shrink: 0; }
.app-avatar-placeholder img { width: 100%; height: 100%; object-fit: cover; }
.app-info { flex: 1; min-width: 0; }
.app-doctor-name { font-size: 13px; font-weight: 600; color: #2A2A2A; margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.app-time { font-size: 11px; color: #666; margin-bottom: 2px; }
.app-department { font-size: 10px; color: #999; }
.app-status { font-size: 10px; padding: 2px 6px; border-radius: 4px; font-weight: 600; white-space: nowrap; }
.app-status.pending { background: #FFF8E1; color: #F57C00; }
.app-status.confirmed { background: #E8F5E9; color: #2E7D32; }

/* Footer */
.main-footer { 
  background: linear-gradient(180deg, #fff 0%, #FAFAFA 100%);
  border-top: 1px solid #EEE; 
  padding: 40px 0; 
  margin-top: 0;
  position: relative;
  z-index: 1;
}
.footer-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; }
.footer-col h4 { font-size: 14px; font-weight: 700; margin-bottom: 16px; color: #2A2A2A; }
.footer-col a { display: block; color: #666; text-decoration: none; margin-bottom: 8px; font-size: 13px; transition: color 0.3s ease; }
.footer-col a:hover { color: #FFD300; }
.social-col p { font-size: 12px; color: #999; }

@media (max-width: 900px) {
  .hero-image-box { display: none; }
  .main-nav { display: none; }
  .app-promo-container { flex-direction: column; text-align: center; }
  .yellow-blob { right: 50%; transform: translateX(50%); top: 10%; width: 300px; height: 300px; }
  .phone-mockup-container { padding: 0; margin-top: 30px; }
  .footer-grid { grid-template-columns: 1fr; text-align: center; }
}
</style>