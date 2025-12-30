<template>
  <div class="notifications-page">
    <div class="page-header">
      <div class="header-left">
        <h1>消息通知</h1>
        <p class="subtitle">查看您的系统消息和预约提醒</p>
      </div>
      <div class="header-actions">
        <button class="btn-text" @click="handleMarkAllRead" v-if="hasUnread">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
            <polyline points="9 11 12 14 22 4"></polyline>
            <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"></path>
          </svg>
          全部标记已读
        </button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <button 
        v-for="tab in typeTabs" 
        :key="tab.value"
        class="tab-btn"
        :class="{ active: currentType === tab.value }"
        @click="filterByType(tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载消息...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="notifications.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 01-3.46 0"></path>
        </svg>
      </div>
      <h3>暂无消息</h3>
      <p>您还没有任何通知消息</p>
    </div>

    <!-- 通知列表 -->
    <div v-else class="notifications-list">
      <div 
        v-for="notification in notifications" 
        :key="notification.id" 
        class="notification-card"
        :class="{ unread: !notification.isRead }"
        @click="handleNotificationClick(notification)"
      >
        <div class="notification-icon" :class="notification.type">
          <svg v-if="notification.type === 'APPOINTMENT'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          <svg v-else-if="notification.type === 'CANCEL'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="15" y1="9" x2="9" y2="15"></line>
            <line x1="9" y1="9" x2="15" y2="15"></line>
          </svg>
          <svg v-else-if="notification.type === 'REMINDER'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 01-3.46 0"></path>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="16" x2="12" y2="12"></line>
            <line x1="12" y1="8" x2="12.01" y2="8"></line>
          </svg>
        </div>

        <div class="notification-content">
          <div class="notification-header">
            <h3 class="notification-title">{{ notification.title }}</h3>
            <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
          </div>
          <p class="notification-body">{{ notification.content }}</p>
        </div>

        <div class="notification-actions">
          <button class="action-btn" @click.stop="handleDelete(notification)" title="删除">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <polyline points="3 6 5 6 21 6"></polyline>
              <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
            </svg>
          </button>
        </div>

        <div class="unread-dot" v-if="!notification.isRead"></div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">上一页</button>
      <span>第 {{ currentPage + 1 }} / {{ totalPages }} 页</span>
      <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { getNotifications, markAsRead, markAllAsRead, deleteNotification } from '@/api/notification';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();

// 获取父组件的刷新方法
const refreshUnreadCount = inject('refreshUnreadCount', () => {});

const notifications = ref([]);
const loading = ref(true);
const currentType = ref('');
const currentPage = ref(0);
const totalPages = ref(0);

const typeTabs = [
  { label: '全部', value: '' },
  { label: '预约通知', value: 'APPOINTMENT' },
  { label: '就诊提醒', value: 'REMINDER' },
  { label: '取消通知', value: 'CANCEL' },
  { label: '系统消息', value: 'SYSTEM' }
];

const hasUnread = computed(() => {
  return notifications.value.some(n => !n.isRead);
});

onMounted(() => {
  fetchNotifications();
});

const fetchNotifications = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: 15
    };
    if (currentType.value) {
      params.type = currentType.value;
    }
    
    const data = await getNotifications(params);
    notifications.value = data?.content || [];
    totalPages.value = data?.totalPages || 0;
  } catch (e) {
    console.error('获取通知失败:', e);
    notifications.value = [];
  } finally {
    loading.value = false;
  }
};

const filterByType = (type) => {
  currentType.value = type;
  currentPage.value = 0;
  fetchNotifications();
};

const changePage = (page) => {
  currentPage.value = page;
  fetchNotifications();
};

const formatTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)} 天前`;
  
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${year}/${month}/${day}`;
};

const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    try {
      await markAsRead(notification.id);
      notification.isRead = true;
      // 刷新顶部未读数量
      refreshUnreadCount();
    } catch (e) {
      console.error('标记已读失败:', e);
    }
  }
  
  // 根据类型跳转
  if (notification.relatedId) {
    if (notification.type === 'APPOINTMENT' || notification.type === 'CANCEL') {
      router.push('/patient/appointments');
    }
  }
};

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead();
    notifications.value.forEach(n => n.isRead = true);
    // 刷新顶部未读数量
    refreshUnreadCount();
    ElMessage.success('已全部标记为已读');
  } catch (e) {
    ElMessage.error('操作失败');
  }
};

const handleDelete = async (notification) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await deleteNotification(notification.id);
    notifications.value = notifications.value.filter(n => n.id !== notification.id);
    ElMessage.success('已删除');
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};
</script>

<style scoped>
.notifications-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.btn-text {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: none;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.btn-text:hover {
  background: #f3f4f6;
  color: #2a2a2a;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  border-color: #FFD300;
}

.tab-btn.active {
  background: #FFD300;
  border-color: #FFD300;
  font-weight: 600;
}

/* 加载和空状态 */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #FFD300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-icon {
  width: 80px;
  height: 80px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #2a2a2a;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #666;
  margin: 0;
}

/* 通知卡片 */
.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.notification-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.notification-card.unread {
  background: #FFFEF5;
  border-color: #FFD300;
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

.notification-icon.APPOINTMENT {
  background: #DBEAFE;
  color: #2563EB;
}

.notification-icon.CANCEL {
  background: #FEE2E2;
  color: #DC2626;
}

.notification-icon.REMINDER {
  background: #FEF3C7;
  color: #D97706;
}

.notification-icon.SYSTEM {
  background: #E5E7EB;
  color: #6B7280;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #2a2a2a;
  margin: 0;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
}

.notification-body {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.notification-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  color: #9ca3af;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #fee2e2;
  color: #dc2626;
}

.unread-dot {
  position: absolute;
  top: 24px;
  right: 16px;
  width: 8px;
  height: 8px;
  background: #FFD300;
  border-radius: 50%;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
}

.pagination button {
  padding: 8px 16px;
  background: #fff;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #666;
}
</style>

