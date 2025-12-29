<template>
  <div class="landing-page">
    
    <div class="cream-wrapper">
      <header class="main-header">
        <div class="container nav-container">
          <div class="logo-area">
            <div class="logo-box">优</div>
            <span class="logo-text">优医预约</span>
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
            <button class="btn-signup" @click="goRegister">注册</button>
          </nav>
        </div>
      </header>

      <section class="hero-section">
        <div class="container hero-container">
          <div class="hero-text">
            <h1>
              在线预约医生<br>
              告别排队烦恼
            </h1>
          </div>
          
          <div class="hero-image-box">
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
        </div>
      </section>
    </div>

    <section class="list-section">
      <div class="container">
        <div class="section-header">
          <div>
            <h2 class="section-title">评价最高的全科医生</h2>
            <p class="section-subtitle">90% 的患者给这些医生打出了 5 星好评</p>
          </div>
          <a href="#" class="see-all">查看全部 ›</a>
        </div>
        <div class="scroll-container">
          <div class="doctor-card" v-for="(doc, index) in topDoctors" :key="index">
            <div class="card-top">
              <div class="avatar-wrapper">
                <img :src="doc.avatar" alt="Avatar" class="avatar" />
                <div class="video-badge" v-if="doc.video">
                  <svg viewBox="0 0 24 24" width="10" height="10" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
                </div>
              </div>
              <div class="doc-info">
                <h3 class="doc-name">{{ doc.name }}</h3>
                <p class="doc-title">{{ doc.title }}</p>
                <div class="rating">
                  <svg class="star-icon" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                  <span class="score">{{ doc.score }}</span>
                  <span class="reviews">({{ doc.reviews }})</span>
                </div>
              </div>
            </div>
            <div class="card-body">
              <div class="location-row">
                <svg class="icon-xs" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                {{ doc.location }}
              </div>
              <div class="availability">最早可约: <strong>{{ doc.nextSlot }}</strong></div>
            </div>
            <button class="btn-book" @click="findCare">在线预约</button>
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

    <section class="list-section grey-bg">
      <div class="container">
        <div class="section-header">
          <div>
            <h2 class="section-title">候诊时间最短的牙医</h2>
            <p class="section-subtitle">91% 的患者在这些诊所的等待时间少于 30 分钟</p>
          </div>
          <a href="#" class="see-all">查看全部 ›</a>
        </div>
        <div class="scroll-container">
          <div class="doctor-card" v-for="(doc, index) in dentists" :key="index">
            <div class="card-top">
              <div class="avatar-wrapper">
                <img :src="doc.avatar" alt="Avatar" class="avatar" />
              </div>
              <div class="doc-info">
                <h3 class="doc-name">{{ doc.name }}</h3>
                <p class="doc-title">{{ doc.title }}</p>
                <div class="rating">
                  <svg class="star-icon" viewBox="0 0 24 24" fill="currentColor"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                  <span class="score">{{ doc.score }}</span>
                  <span class="reviews">({{ doc.reviews }})</span>
                </div>
              </div>
            </div>
            <div class="card-body">
              <div class="location-row">
                <svg class="icon-xs" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                {{ doc.location }}
              </div>
              <div class="availability">最早可约: <strong>{{ doc.nextSlot }}</strong></div>
            </div>
            <button class="btn-book" @click="findCare">在线预约</button>
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
          <div class="specialty-card" v-for="(item, index) in specialties" :key="index">
            <div class="specialty-icon-circle">
              <svg class="spec-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" v-html="item.svgPath"></svg>
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
            <button class="feature-btn">查看科室</button>
          </div>
          <div class="feature-card">
            <div class="feature-illus illus-yellow">
               <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="1.5"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
            </div>
            <h3 class="feature-text">查看真实患者评价<br>选择更放心</h3>
            <button class="feature-btn">查看医生</button>
          </div>
          <div class="feature-card">
             <div class="feature-illus illus-mint">
               <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2A2A2A" stroke-width="1.5"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
            </div>
            <h3 class="feature-text">今天即可在线预约<br>方便快捷</h3>
            <button class="feature-btn">查看排期</button>
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
          <div class="arrow-container">
             <svg width="100" height="50" viewBox="0 0 120 60" fill="none" stroke="#2A2A2A" stroke-width="2" stroke-linecap="round">
                <path d="M10 40 Q 40 10, 80 20 T 110 50" />
                <path d="M100 50 L 110 50 L 105 40" />
             </svg>
          </div>
          <div class="store-buttons">
            <button class="store-btn">下载 App</button>
            <button class="store-btn">安卓版下载</button>
          </div>
        </div>
        <div class="phone-mockup-container">
          <div class="phone-mockup">
            <div class="phone-notch"></div>
            <div class="phone-screen">
              <div class="app-header">
                <div class="app-logo-small">优</div>
                <div class="app-title-text">我的预约</div>
              </div>
              <div class="app-content">
                <div class="app-card" v-for="i in 3" :key="i">
                  <div class="app-card-row">
                    <div class="app-avatar-placeholder"></div>
                    <div class="app-info">
                      <div class="bar-long"></div>
                      <div class="bar-short"></div>
                    </div>
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
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';

const router = useRouter();
const searchKeyword = ref('');
const searchLocation = ref('');

const goLogin = () => router.push('/login');
const goRegister = () => router.push('/register');
const findCare = () => {
  const query = {};
  if (searchKeyword.value) {
    query.keyword = searchKeyword.value;
  }
  if (searchLocation.value) {
    query.location = searchLocation.value;
  }
  router.push({ path: '/search-triage', query });
};

const topDoctors = ref([]);
const dentists = ref([]);
const specialties = ref([]);

const fetchLandingData = async () => {
  try {
    // Use allSettled to ensure single interface failure doesn't affect others
    const results = await Promise.allSettled([
      request.get('/api/doctors/top', { params: { limit: 10 } }),
      request.get('/api/doctors/specialty/DENTIST'),
      request.get('/api/departments')
    ]);
    
    // The interceptor in `request` already unwraps the .data property from the response.
    // The value of a fulfilled promise is the data itself.
    topDoctors.value = (results[0].status === 'fulfilled' && results[0].value) ? results[0].value : [];
    dentists.value = (results[1].status === 'fulfilled' && results[1].value) ? results[1].value : [];
    specialties.value = (results[2].status === 'fulfilled' && results[2].value) ? results[2].value : [];

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

onMounted(() => {
  fetchLandingData();
});
</script>

<style>
/* 全局设置 */
body { margin: 0; padding: 0; background-color: #fff; font-family: "Microsoft YaHei", "Heiti SC", -apple-system, BlinkMacSystemFont, sans-serif; color: #2A2A2A; font-size: 14px; }
</style>

<style scoped>
/* 容器基础设置 */
.landing-page { width: 100%; overflow-x: hidden; }
.container { max-width: 1080px; margin: 0 auto; padding: 0 24px; position: relative; height: 100%; }

/* --- 1. Header (保持精致高度) --- */
.cream-wrapper { background-color: #FFF9E5; position: relative; }

.main-header { height: 60px; display: flex; align-items: center; position: relative; z-index: 50; }
.nav-container { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.logo-area { display: flex; align-items: center; gap: 6px; cursor: pointer; }
.logo-box { background: #FFD300; width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; font-weight: bold; border-radius: 4px 0 4px 0; color: #000; font-size: 13px; }
.logo-text { font-size: 18px; font-weight: 700; color: #2A2A2A; letter-spacing: -0.5px; }
.main-nav { display: flex; align-items: center; gap: 20px; }
.nav-link { text-decoration: none; color: #2A2A2A; font-size: 14px; font-weight: 500; display: flex; align-items: center; gap: 4px; }
.nav-link:hover { color: #000; }
.icon-xs { width: 10px; height: 10px; margin-top: 1px; opacity: 0.6; }
.nav-divider { width: 1px; height: 20px; background-color: #DDD; margin: 0 2px; }
.btn-signup { background: #FFD300; border: none; padding: 8px 18px; font-size: 14px; font-weight: 700; border-radius: 4px; cursor: pointer; transition: background 0.2s; color: #2A2A2A; }
.btn-signup:hover { background: #F4CA00; }

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
  /* 调整文字位置以适应更窄的背景 */
  margin-top: 40px; 
}

.hero-text h1 { 
  font-size: 42px; /* 字体大小适中 */
  line-height: 1.2; 
  font-weight: 400; 
  color: #2A2A2A; 
  margin: 0; 
  letter-spacing: -1px; 
}

/* 插图调整 */
.hero-image-box { 
  position: absolute; 
  /* 关键修改：靠右位置 */
  right: 60px; 
  /* 调整插图位置 */
  top: -10px; 
  /* 关键修改：大小保持您之前满意的尺寸 */
  width: 380px; 
  z-index: 1; 
  pointer-events: none; 
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
  height: 60px; /* 高度适中 */
  box-sizing: border-box;
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
  transition: background 0.2s; 
}
.icon-search-btn { width: 20px; height: 20px; }
.btn-search:hover { background: #F4CA00; }

/* --- 列表通用 --- */
.list-section { 
  padding: 50px 0; 
  background-color: #fff; 
}
.list-section.grey-bg { background-color: #FAFAFA; }

.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.section-title { font-size: 20px; font-weight: 700; color: #2A2A2A; margin: 0 0 6px 0; }
.section-subtitle { font-size: 13px; color: #666; margin: 0; }
.see-all { font-size: 13px; color: #2A2A2A; text-decoration: none; font-weight: 600; }
.scroll-container { display: flex; gap: 20px; overflow-x: auto; padding-bottom: 16px; scroll-snap-type: x mandatory; scrollbar-width: none; }
.scroll-container::-webkit-scrollbar { display: none; }

/* 医生卡片 */
.doctor-card { min-width: 260px; background: #fff; border: 1px solid #E8E8E8; border-radius: 8px; padding: 16px; display: flex; flex-direction: column; cursor: pointer; scroll-snap-align: start; transition: box-shadow 0.2s; }
.doctor-card:hover { box-shadow: 0 6px 16px rgba(0,0,0,0.05); }
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
.btn-book { width: 100%; background-color: #FFD300; border: none; padding: 10px; font-weight: 700; border-radius: 4px; cursor: pointer; font-size: 13px; }

/* 查看更多卡片 */
.see-more-card { min-width: 260px; background: #fff; border: 1px solid #E8E8E8; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 16px; scroll-snap-align: start; }
.icon-circle-bg { width: 44px; height: 44px; background-color: #E8F0FE; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #1A73E8; margin-bottom: 12px; }
.btn-see-more { padding: 8px 20px; background: white; border: 1px solid #2A2A2A; border-radius: 4px; font-weight: 600; cursor: pointer; font-size: 13px; }

/* 科室卡片 */
.specialty-card { min-width: 120px; height: 140px; background-color: #FFF9E5; border-radius: 8px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; scroll-snap-align: start; transition: transform 0.2s; }
.specialty-card:hover { transform: translateY(-3px); }
.specialty-icon-circle { width: 56px; height: 56px; background-color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 12px; box-shadow: 0 4px 8px rgba(0,0,0,0.02); }
.spec-svg { width: 28px; height: 28px; color: #2A2A2A; }
.specialty-name { font-size: 13px; font-weight: 600; color: #2A2A2A; }

/* --- 特性引导 --- */
.features-section { background-color: #FFF9E5; padding: 50px 0; }
.center-text { text-align: center; margin-bottom: 30px; }
.features-grid { display: flex; gap: 24px; justify-content: center; flex-wrap: wrap; }
.feature-card { background: #fff; flex: 1; max-width: 280px; min-width: 240px; border-radius: 12px; padding: 30px 20px; text-align: center; display: flex; flex-direction: column; align-items: center; box-shadow: 0 4px 12px rgba(0,0,0,0.02); }
.feature-illus { width: 64px; height: 64px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }
.illus-green { background-color: #E0F2F1; color: #00695C; }
.illus-yellow { background-color: #FFF8E1; color: #FF8F00; }
.illus-mint { background-color: #E8F5E9; color: #2E7D32; }
.feature-text { font-size: 16px; font-weight: 600; color: #2A2A2A; margin-bottom: 24px; line-height: 1.4; flex: 1; }
.feature-btn { margin-top: auto; background: white; border: 1px solid #DDD; padding: 8px 20px; border-radius: 4px; font-size: 13px; font-weight: 600; color: #2A2A2A; cursor: pointer; transition: all 0.2s; }
.feature-btn:hover { background-color: #F5F5F5; border-color: #999; }

/* --- App 推广 --- */
.app-promo-section { background-color: #FACCB2; padding: 60px 0; position: relative; overflow: hidden; }
.yellow-blob { position: absolute; width: 500px; height: 500px; background-color: #FFD300; border-radius: 50%; right: -80px; top: 50%; transform: translateY(-50%); z-index: 0; }
.app-promo-container { display: flex; align-items: center; justify-content: space-between; position: relative; z-index: 1; }
.app-text-content { max-width: 420px; }
.app-title { font-size: 32px; font-weight: 700; margin-bottom: 16px; line-height: 1.1; }
.app-subtitle { font-size: 16px; line-height: 1.5; margin-bottom: 24px; opacity: 0.9; }
.store-buttons { display: flex; gap: 12px; }
.store-btn { display: flex; align-items: center; gap: 8px; background-color: #000; color: #fff; border: none; padding: 10px 20px; border-radius: 6px; font-weight: 700; font-size: 13px; cursor: pointer; }
.phone-mockup-container { padding-right: 30px; }
.phone-mockup { width: 260px; height: 520px; background: #2A2A2A; border-radius: 36px; padding: 8px; box-shadow: 0 16px 40px rgba(0,0,0,0.15); position: relative; transform: rotate(-2deg); }
.phone-notch { position: absolute; top: 0; left: 50%; transform: translateX(-50%); width: 100px; height: 22px; background: #2A2A2A; border-bottom-left-radius: 10px; border-bottom-right-radius: 10px; z-index: 10; }
.phone-screen { background: #FFF9E5; width: 100%; height: 100%; border-radius: 28px; overflow: hidden; display: flex; flex-direction: column; }
.app-header { padding: 36px 16px 12px; background: #FFD300; display: flex; align-items: center; gap: 8px; }
.app-logo-small { font-weight: bold; font-size: 14px; }
.app-title-text { font-weight: bold; font-size: 14px; flex: 1; text-align: center; }
.app-content { flex: 1; padding: 12px; overflow-y: hidden; }
.app-card { background: #fff; padding: 10px; border-radius: 8px; margin-bottom: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.app-card-row { display: flex; align-items: center; gap: 8px; }
.app-avatar-placeholder { width: 36px; height: 36px; background: #EEE; border-radius: 50%; }
.app-info { flex: 1; }
.bar-long { width: 80%; height: 8px; background: #EEE; border-radius: 4px; margin-bottom: 4px; }
.bar-short { width: 50%; height: 8px; background: #EEE; border-radius: 4px; }

/* Footer */
.main-footer { background-color: #fff; border-top: 1px solid #eee; padding: 40px 0; margin-top: 0; }
.footer-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 30px; }
.footer-col h4 { font-size: 14px; font-weight: 700; margin-bottom: 16px; }
.footer-col a { display: block; color: #666; text-decoration: none; margin-bottom: 8px; font-size: 13px; }
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