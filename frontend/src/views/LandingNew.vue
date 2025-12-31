<template>
  <div class="landing-page">
    <!-- 顶部导航 -->
    <header class="main-header" :class="{ scrolled: isHeaderScrolled }">
      <div class="container">
        <div class="logo-area" @click="goHome">
          <div class="logo-box">优</div>
          <span class="logo-text">优医预约</span>
        </div>
        <nav class="main-nav">
          <a href="#" class="nav-link" @click.prevent="goLogin">登录</a>
          <button class="btn-primary" @click="goRegister">立即注册</button>
        </nav>
      </div>
    </header>

    <!-- Hero 区域 - 华为风格 -->
    <section class="hero-section">
      <div class="container hero-container">
        <!-- 装饰元素 - 鼠标跟随 -->
        <div class="deco-orb orb-1" :style="{ transform: `translate(${mouseX * 0.5}px, ${mouseY * 0.5}px)` }"></div>
        <div class="deco-orb orb-2" :style="{ transform: `translate(${mouseX * -0.3}px, ${mouseY * -0.3}px)` }"></div>
        <div class="deco-orb orb-3" :style="{ transform: `translate(${mouseX * 0.4}px, ${mouseY * 0.4}px)` }"></div>
        <div class="deco-orb orb-4" :style="{ transform: `translate(${mouseX * -0.6}px, ${mouseY * -0.6}px)` }"></div>
        
        <!-- 背景光晕 -->
        <div class="hero-glow"></div>
        
        <div class="hero-badge">
          <span class="badge-dot"></span>
          智慧医疗
        </div>
        <h1 class="hero-title">
          <span class="gradient-text typing-text">新体验</span><br>
          <span class="gradient-text typing-text delay-1">新服务</span>
        </h1>
        <p class="hero-desc">
          <span class="desc-line">优医预约，全新升级</span><br>
          <span class="desc-line">精准匹配，AI智能推荐医生</span><br>
          <span class="desc-line">便捷预约，告别排队烦恼</span><br>
          <span class="desc-line">全程服务，就医体验更贴心</span>
        </p>
        <div class="hero-actions">
          <button class="btn-hero" @click="goRegister">
            <span class="btn-text">立即体验</span>
            <span class="btn-arrow">→</span>
          </button>
        </div>
        
        <!-- 滚动提示 -->
        <div class="scroll-indicator">
          <div class="scroll-mouse">
            <div class="scroll-wheel"></div>
          </div>
          <span>向下滚动</span>
        </div>
      </div>
    </section>

    <!-- 特性展示 - 华为风格 -->
    <section class="features-section">
      <div class="container">
        <div class="section-badge scroll-animate">精致交互</div>
        <h2 class="section-title scroll-animate">
          <span class="title-bold">细细看，</span><br>
          <span class="title-bold">更好看</span>
        </h2>
        
        <div class="features-grid">
          <div class="feature-card scroll-animate">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
            </div>
            <h3>智能匹配</h3>
            <p>AI分析症状，精准推荐最适合的医生</p>
            <div class="card-shine"></div>
          </div>
          
          <div class="feature-card scroll-animate">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
            </div>
            <h3>便捷预约</h3>
            <p>在线选择时间，一键完成预约</p>
            <div class="card-shine"></div>
          </div>
          
          <div class="feature-card scroll-animate">
            <div class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </div>
            <h3>全程服务</h3>
            <p>就诊提醒、报告查询、健康管理</p>
            <div class="card-shine"></div>
          </div>
        </div>
      </div>
    </section>

    <!-- 统计数据 -->
    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item scroll-animate">
            <div class="stat-number" data-count="1000">1000<span class="stat-unit">+</span></div>
            <div class="stat-label">合作医院</div>
          </div>
          <div class="stat-item scroll-animate">
            <div class="stat-number" data-count="5000">5000<span class="stat-unit">+</span></div>
            <div class="stat-label">专业医生</div>
          </div>
          <div class="stat-item scroll-animate">
            <div class="stat-number" data-count="100">100<span class="stat-unit">万+</span></div>
            <div class="stat-label">服务患者</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部 CTA -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2 class="scroll-animate">开始您的健康之旅</h2>
          <p class="scroll-animate">注册即可享受智能预约服务</p>
          <button class="btn-cta scroll-animate" @click="goRegister">
            <span>免费注册</span>
            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="main-footer">
      <div class="container">
        <p>© 2025 优医预约系统 · 让就医更简单</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';

const router = useRouter();

const goHome = () => router.push('/');
const goLogin = () => router.push('/login');
const goRegister = () => router.push('/register');

// 滚动视差效果
const scrollY = ref(0);
const isHeaderScrolled = ref(false);

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

// 鼠标跟随效果
const mouseX = ref(0);
const mouseY = ref(0);

const handleMouseMove = (e) => {
  mouseX.value = (e.clientX / window.innerWidth - 0.5) * 20;
  mouseY.value = (e.clientY / window.innerHeight - 0.5) * 20;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('mousemove', handleMouseMove);
  handleScroll(); // 初始检查
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('mousemove', handleMouseMove);
});
</script>


<style scoped>
/* 基础设置 */
.landing-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  color: #1a1a1a;
  overflow-x: hidden;
  animation: pageLoad 0.6s ease-out;
}

@keyframes pageLoad {
  from { opacity: 0; }
  to { opacity: 1; }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 导航栏 */
.main-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  animation: slideDown 0.5s ease-out;
  transition: all 0.3s ease;
}

.main-header.scrolled {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.main-header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.logo-area:hover {
  transform: scale(1.05);
}

.logo-box {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s;
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.nav-link:hover {
  color: #1a1a1a;
}

.nav-link:hover::after {
  width: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
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
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s ease;
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

/* Hero 区域 */
.hero-section {
  padding: 160px 0 100px;
  position: relative;
  text-align: center;
}

.hero-container {
  position: relative;
}

/* 装饰球体 - 玻璃质感 */
.deco-orb {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.1) 100%);
  backdrop-filter: blur(40px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 
    inset 0 0 20px rgba(255, 255, 255, 0.5),
    0 8px 32px rgba(102, 126, 234, 0.1);
}

.orb-1 {
  width: 120px;
  height: 120px;
  top: 20px;
  left: 10%;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 80px;
  height: 80px;
  top: 60%;
  right: 15%;
  animation: float 6s ease-in-out infinite;
  animation-delay: -2s;
}

.orb-3 {
  width: 60px;
  height: 60px;
  bottom: 10%;
  left: 20%;
  animation: float 7s ease-in-out infinite;
  animation-delay: -4s;
}

.orb-4 {
  width: 100px;
  height: 100px;
  top: 40%;
  right: 8%;
  animation: float 9s ease-in-out infinite;
  animation-delay: -1s;
}

/* 背景光晕 */
.hero-glow {
  position: absolute;
  width: 600px;
  height: 600px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(102, 126, 234, 0.15) 0%, transparent 70%);
  animation: glowPulse 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  font-size: 13px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 24px;
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: #667eea;
  border-radius: 50%;
  animation: badgePulse 1.5s ease-in-out infinite;
}

@keyframes badgePulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-title {
  font-size: 64px;
  font-weight: 700;
  line-height: 1.1;
  margin: 0 0 32px;
  letter-spacing: -2px;
  animation: fadeInUp 0.6s ease-out 0.4s both;
}

.gradient-text {
  background: linear-gradient(135deg, #1a1a1a 0%, #667eea 50%, #764ba2 100%);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 4s ease infinite;
}

.typing-text {
  display: inline-block;
  animation: fadeInUp 0.6s ease-out 0.4s both, textReveal 0.8s ease-out 0.4s both;
}

.typing-text.delay-1 {
  animation: fadeInUp 0.6s ease-out 0.6s both, textReveal 0.8s ease-out 0.6s both;
}

@keyframes textReveal {
  from { clip-path: inset(0 100% 0 0); }
  to { clip-path: inset(0 0 0 0); }
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-desc {
  font-size: 16px;
  line-height: 2;
  color: #666;
  margin: 0 0 40px;
  animation: fadeInUp 0.6s ease-out 0.6s both;
}

.desc-line {
  display: inline-block;
  opacity: 0;
  animation: descLineIn 0.5s ease-out forwards;
}

.desc-line:nth-child(1) { animation-delay: 0.8s; }
.desc-line:nth-child(2) { animation-delay: 1s; }
.desc-line:nth-child(3) { animation-delay: 1.2s; }
.desc-line:nth-child(4) { animation-delay: 1.4s; }

@keyframes descLineIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  animation: fadeInUp 0.6s ease-out 0.8s both;
}

.btn-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  padding: 16px 48px;
  border-radius: 28px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-hero .btn-text {
  position: relative;
  z-index: 1;
}

.btn-hero .btn-arrow {
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.btn-hero:hover .btn-arrow {
  transform: translateX(4px);
}

.btn-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s ease;
}

.btn-hero:hover::before {
  left: 100%;
}

.btn-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.btn-hero:hover::after {
  opacity: 1;
}

.btn-hero:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.5);
}

.btn-hero:active {
  transform: translateY(-2px) scale(1.02);
}

/* 滚动提示 */
.scroll-indicator {
  position: absolute;
  bottom: -60px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  animation: fadeInUp 0.6s ease-out 1.5s both;
}

.scroll-mouse {
  width: 24px;
  height: 40px;
  border: 2px solid rgba(102, 126, 234, 0.5);
  border-radius: 12px;
  position: relative;
}

.scroll-wheel {
  width: 4px;
  height: 8px;
  background: #667eea;
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

.scroll-indicator span {
  font-size: 12px;
  color: #999;
  letter-spacing: 1px;
}

/* 特性区域 */
.features-section {
  padding: 100px 0;
  background: #fff;
}

.section-badge {
  text-align: center;
  font-size: 13px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 16px;
}

.section-title {
  text-align: center;
  font-size: 48px;
  font-weight: 700;
  line-height: 1.2;
  margin: 0 0 60px;
  letter-spacing: -1px;
}

.title-bold {
  color: #1a1a1a;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.feature-card {
  text-align: center;
  padding: 40px 24px;
  background: #fafafa;
  border-radius: 16px;
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.feature-card:nth-child(1) { animation: fadeInUp 0.6s ease-out 0.2s both; }
.feature-card:nth-child(2) { animation: fadeInUp 0.6s ease-out 0.4s both; }
.feature-card:nth-child(3) { animation: fadeInUp 0.6s ease-out 0.6s both; }

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 卡片光泽效果 */
.card-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transform: skewX(-20deg);
  transition: left 0.6s ease;
}

.feature-card:hover .card-shine {
  left: 150%;
}

/* 滚动触发动画 */
.scroll-animate {
  opacity: 0;
  transform: translateY(40px);
  transition: opacity 0.6s ease, transform 0.6s ease;
}

.scroll-animate.animate-in {
  opacity: 1;
  transform: translateY(0);
}

.scroll-animate:nth-child(2) { transition-delay: 0.1s; }
.scroll-animate:nth-child(3) { transition-delay: 0.2s; }

.feature-icon svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px;
  color: #1a1a1a;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

/* 统计数据 */
.stats-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  text-align: center;
}

.stat-item {
  animation: fadeInUp 0.6s ease-out both;
}

.stat-item:nth-child(1) { animation-delay: 0.2s; }
.stat-item:nth-child(2) { animation-delay: 0.4s; }
.stat-item:nth-child(3) { animation-delay: 0.6s; }

.stat-number {
  font-size: 56px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
  margin-bottom: 8px;
  transition: transform 0.3s ease;
}

.stat-item:hover .stat-number {
  transform: scale(1.1);
}

.stat-unit {
  font-size: 32px;
}

.stat-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
}

/* CTA 区域 */
.cta-section {
  padding: 100px 0;
  background: #fafafa;
}

.cta-content {
  text-align: center;
}

.cta-content h2 {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 16px;
  color: #1a1a1a;
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.cta-content p {
  font-size: 16px;
  color: #666;
  margin: 0 0 32px;
  animation: fadeInUp 0.6s ease-out 0.4s both;
}

.btn-cta {
  background: #1a1a1a;
  color: #fff;
  border: none;
  padding: 16px 48px;
  border-radius: 28px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s ease, background 0.3s;
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-cta .btn-icon {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.btn-cta:hover .btn-icon {
  transform: translateX(4px);
}

.btn-cta::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.btn-cta:hover::before {
  left: 100%;
}

.btn-cta:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.btn-cta:active {
  transform: translateY(-2px) scale(1.02);
}

/* 页脚 */
.main-footer {
  padding: 40px 0;
  background: #fff;
  border-top: 1px solid #eee;
}

.main-footer p {
  text-align: center;
  font-size: 14px;
  color: #999;
  margin: 0;
  transition: color 0.3s ease;
}

.main-footer p:hover {
  color: #667eea;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }
  
  .features-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .stat-number {
    font-size: 40px;
  }
}
</style>
