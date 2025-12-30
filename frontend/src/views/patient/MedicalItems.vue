<template>
  <div class="medical-items-section">
    <div class="section-header">
      <h2>热门检查项目</h2>
      <p class="subtitle">选择您需要的检查项目进行预约</p>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载...</p>
    </div>
    
    <div v-else-if="medicalItems.length === 0" class="empty-state">
      <p>暂无可预约的检查项目</p>
    </div>
    
    <div v-else class="items-grid">
      <div 
        v-for="item in medicalItems" 
        :key="item.id" 
        class="item-card"
        @click="handleBook(item)"
      >
        <div class="item-icon-wrapper">
          <component :is="getIconComponent(item.category)" class="item-icon" />
        </div>
        <div class="item-content">
          <h3 class="item-name">{{ item.name }}</h3>
          <p class="item-desc">{{ item.description || '专业检查项目' }}</p>
          <div class="item-footer">
            <!-- 价格为0时不显示价格 -->
            <span class="item-price" v-if="item.price && parseFloat(item.price) > 0">¥{{ item.price }}</span>
            <span class="item-category">{{ item.category }}</span>
          </div>
        </div>
        <div class="item-action">
          <button class="book-btn">
            <span>预约</span>
            <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import { useRouter } from 'vue-router';
import { getAllMedicalItems } from '@/api/medicalItem';

const router = useRouter();
const medicalItems = ref([]);
const loading = ref(true);

// 根据类别返回不同的 SVG 图标
const getIconComponent = (category) => {
  const icons = {
    '体检': () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '1.5' }, [
      h('path', { d: 'M9 12l2 2 4-4' }),
      h('circle', { cx: '12', cy: '12', r: '10' })
    ]),
    '影像': () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '1.5' }, [
      h('rect', { x: '3', y: '3', width: '18', height: '18', rx: '2' }),
      h('circle', { cx: '12', cy: '12', r: '3' })
    ]),
    '化验': () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '1.5' }, [
      h('path', { d: 'M9 3h6v2H9V3z' }),
      h('path', { d: 'M8 5v14a2 2 0 002 2h4a2 2 0 002-2V5' }),
      h('path', { d: 'M10 12h4' })
    ]),
    '心电': () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '1.5' }, [
      h('path', { d: 'M22 12h-4l-3 9L9 3l-3 9H2' })
    ]),
    'default': () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '1.5' }, [
      h('path', { d: 'M22 12h-4l-3 9L9 3l-3 9H2' })
    ])
  };
  return icons[category] || icons['default'];
};

onMounted(async () => {
  try {
    const data = await getAllMedicalItems();
    // request.js 响应拦截器已经解析了数据，直接返回的是 data 部分
    medicalItems.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('Failed to fetch medical items:', error);
    medicalItems.value = [];
  } finally {
    loading.value = false;
  }
});

const handleBook = (item) => {
  // 直接跳转到搜索结果页面，并传递项目ID和科室ID
  router.push({
    path: '/search-results',
    query: {
      medicalItemId: item.id,
      deptId: item.departmentId
    }
  });
};
</script>

<style scoped>
.medical-items-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #E5E5E5;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #2A2A2A;
  margin: 0 0 6px 0;
}

.subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* Loading & Empty States */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #666;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #E5E5E5;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Grid Layout */
.items-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* Item Card - Zocdoc 风格 */
.item-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border: 1px solid #E5E5E5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.item-card:hover {
  border-color: #FFD300;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

/* Icon Wrapper - 米黄色背景 */
.item-icon-wrapper {
  width: 56px;
  height: 56px;
  background-color: #FFF9E5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-icon {
  width: 28px;
  height: 28px;
  color: #2A2A2A;
}

/* Content */
.item-content {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #2A2A2A;
  margin: 0 0 6px 0;
}

.item-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-price {
  font-weight: 700;
  font-size: 16px;
  color: #2A2A2A;
}

.item-category {
  font-size: 12px;
  color: #666;
  background: #F5F5F5;
  padding: 2px 8px;
  border-radius: 4px;
}

/* Action Button */
.item-action {
  flex-shrink: 0;
}

.book-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #FFD300;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  color: #2A2A2A;
  cursor: pointer;
  transition: background-color 0.2s;
}

.book-btn:hover {
  background-color: #F4CA00;
}

.arrow-icon {
  width: 16px;
  height: 16px;
}

/* 响应式 */
@media (max-width: 600px) {
  .item-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .item-action {
    width: 100%;
  }
  
  .book-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
