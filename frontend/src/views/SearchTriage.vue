<template>
  <div class="triage-page">
    <div class="progress-bar"></div>
    <div class="skip-link">
      <router-link :to="{ path: '/search-results', query: route.query }">跳过到搜索结果</router-link>
    </div>
    <div class="triage-container">
      <h1 class="triage-title">请选择检查项目</h1>
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>正在加载...</p>
      </div>
      <div v-else-if="medicalItems.length === 0" class="empty-state">
        <p>暂无可预约的检查项目</p>
        </div>
      <div v-else class="card-group">
        <div 
          v-for="item in medicalItems" 
          :key="item.id" 
          class="care-card" 
          @click="handleSelectItem(item)"
        >
          <h2 class="card-title">{{ item.name }}</h2>
          <p class="card-subtitle">{{ item.description || '专业检查项目' }}</p>
          <div v-if="item.price && parseFloat(item.price) > 0" class="card-price">¥{{ item.price }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getAllMedicalItems } from '@/api/medicalItem';

const router = useRouter();
const route = useRoute();

const medicalItems = ref([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const data = await getAllMedicalItems();
    // request.js 响应拦截器已经解析了数据，直接返回的是 data 部分
    medicalItems.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error('获取检查项目失败:', error);
    medicalItems.value = [];
  } finally {
    loading.value = false;
  }
});

const handleSelectItem = (item) => {
  // 跳转到搜索结果页面，传递项目ID和科室ID，同时保留原有的query参数
  router.push({
    path: '/search-results',
    query: {
      ...route.query,
      medicalItemId: item.id,
      deptId: item.departmentId
    }
  });
};
</script>

<style scoped lang="scss">
.triage-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  min-height: 100vh;
  background-color: #f0f2f5;
  padding-top: 1rem;
  position: relative;
}

.progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 4px;
  background-color: #007bff;
}

.skip-link {
  position: absolute;
  top: 1rem;
  right: 2rem;
  font-size: 0.9rem;
  a {
    color: #555;
    text-decoration: none;
    &:hover {
      text-decoration: underline;
    }
  }
}

.triage-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  width: 100%;
  max-width: 600px;
  flex-grow: 1;
  padding: 2rem;
}

.triage-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 3rem;
  color: #333;
}

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
  border: 3px solid #e0e0e0;
  border-top-color: #007bff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.card-group {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
}

.care-card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-align: left;
  position: relative;

  &:hover {
    border-color: #007bff;
    background-color: #f8f9fa;
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .card-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin: 0 0 0.5rem 0;
    color: #333;
  }

  .card-subtitle {
    font-size: 1rem;
    color: #666;
    margin: 0 0 0.5rem 0;
  }

  .card-price {
    font-size: 1.1rem;
    font-weight: 600;
    color: #007bff;
    margin-top: 0.5rem;
  }
}
</style>
