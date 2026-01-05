<template>
  <div id="app">
    <router-view v-slot="{ Component, route }">
      <transition :name="route.meta.transition || 'page-fade'" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
    <AIAssistant />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import AIAssistant from '@/components/AIAssistant.vue'

onMounted(() => {
  console.log('医院门诊预约挂号系统已启动')
  
  // 添加滚动触发动画观察器
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-in')
      }
    })
  }, {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  })
  
  // 观察所有需要滚动触发动画的元素
  document.querySelectorAll('.scroll-animate, .scroll-animate-left, .scroll-animate-right, .scroll-animate-scale').forEach(el => {
    observer.observe(el)
  })
})
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(180deg, #FFFEF8 0%, #FFF9E5 50%, #FFFEF8 100%);
}

/* 页面过渡动画 - 淡入淡出 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 页面过渡动画 - 滑动 */
.page-slide-enter-active,
.page-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.page-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* 页面过渡动画 - 缩放 */
.page-scale-enter-active,
.page-scale-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-scale-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.page-scale-leave-to {
  opacity: 0;
  transform: scale(1.02);
}

/* 页面过渡动画 - 3D翻转 */
.page-flip-enter-active,
.page-flip-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.page-flip-enter-from {
  opacity: 0;
  transform: perspective(1000px) rotateY(-10deg);
}

.page-flip-leave-to {
  opacity: 0;
  transform: perspective(1000px) rotateY(10deg);
}
</style>
