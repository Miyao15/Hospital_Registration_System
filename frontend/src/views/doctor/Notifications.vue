<template>
  <div class="notifications-page">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-info">
        <h2>消息通知</h2>
        <p>查看您的所有通知消息</p>
      </div>
      <button v-if="hasUnread" class="btn-mark-all" @click="markAllAsRead">
        全部已读
      </button>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <button 
        v-for="tab in filterTabs" 
        :key="tab.value"
        class="tab"
        :class="{ active: currentFilter === tab.value }"
        @click="filterNotifications(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 通知列表 -->
    <div class="notifications-container">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
      </div>

      <div v-else-if="notifications.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="64" height="64">
          <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 01-3.46 0"></path>
        </svg>
        <h3>暂无通知</h3>
        <p>您的通知消息将显示在这里</p>
      </div>

      <div v-else class="notifications-list">
        <div 
          v-for="notification in notifications" 
          :key="notification.id" 
          class="notification-item"
          :class="{ unread: !notification.isRead }"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon" :class="notification.type?.toLowerCase()">
            <svg v-if="notification.type === 'APPOINTMENT'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            <svg v-else-if="notification.type === 'SYSTEM'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="16" x2="12" y2="12"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            </svg>
          </div>
          
          <div class="notification-content">
            <div class="notification-header">
              <span class="notification-title">{{ notification.title }}</span>
              <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
            </div>
            <p class="notification-body">{{ notification.content }}</p>
          </div>

          <div class="notification-actions">
            <button class="btn-delete" @click.stop="deleteNotification(notification)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                <polyline points="3 6 5 6 21 6"></polyline>
                <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button 
          class="page-btn" 
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          class="page-btn" 
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const loading = ref(true);
const notifications = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const currentFilter = ref('all');

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '预约通知', value: 'APPOINTMENT' },
  { label: '系统消息', value: 'SYSTEM' }
];

const hasUnread = computed(() => notifications.value.some(n => !n.isRead));

onMounted(() => {
  fetchNotifications();
});

const fetchNotifications = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: 20
    };
    if (currentFilter.value !== 'all') {
      params.type = currentFilter.value;
    }
    const data = await request.get('/api/notifications', { params });
    notifications.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
  } catch (e) {
    console.error('获取通知失败:', e);
    notifications.value = [];
  } finally {
    loading.value = false;
  }
};

const filterNotifications = (filter) => {
  currentFilter.value = filter;
  currentPage.value = 0;
  fetchNotifications();
};

const changePage = (page) => {
  currentPage.value = page;
  fetchNotifications();
};

const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    try {
      await request.post(`/api/notifications/${notification.id}/read`);
      notification.isRead = true;
    } catch (e) {
      console.error('标记已读失败:', e);
    }
  }
};

const markAllAsRead = async () => {
  try {
    await request.post('/api/notifications/read-all');
    notifications.value.forEach(n => n.isRead = true);
    ElMessage.success('已全部标记为已读');
  } catch (e) {
    ElMessage.error('操作失败');
  }
};

const deleteNotification = async (notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await request.delete(`/api/notifications/${notification.id}`);
    notifications.value = notifications.value.filter(n => n.id !== notification.id);
    ElMessage.success('删除成功');
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const formatTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;
  
  return `${date.getMonth() + 1}/${date.getDate()}`;
};
</script>

<style scoped>
.notifications-page {
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-info h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}

.header-info p {
  font-size: 14px;
  color: #64748b;
  margin: 4px 0 0;
}

.btn-mark-all {
  padding: 8px 20px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
}

.btn-mark-all:hover {
  background: #e5e7eb;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tab {
  padding: 8px 20px;
  border: none;
  background: #f3f4f6;
  border-radius: 20px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.tab:hover {
  background: #e5e7eb;
}

.tab.active {
  background: #FFD300;
  color: #1a1a2e;
  font-weight: 600;
}

.notifications-container {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
}

.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-state h3 {
  margin: 16px 0 8px;
  color: #475569;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item.unread {
  background: #fffef5;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-icon.appointment {
  background: #dbeafe;
  color: #1d4ed8;
}

.notification-icon.system {
  background: #fef3c7;
  color: #92400e;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.notification-time {
  font-size: 12px;
  color: #94a3b8;
}

.notification-body {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

.btn-delete {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 8px;
  opacity: 0;
  transition: all 0.2s;
}

.notification-item:hover .btn-delete {
  opacity: 1;
}

.btn-delete:hover {
  color: #ef4444;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-top: 1px solid #f1f5f9;
}

.page-btn {
  padding: 8px 20px;
  background: #f3f4f6;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #64748b;
}
</style>

