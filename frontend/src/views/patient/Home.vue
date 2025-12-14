<template>
  <div class="patient-home">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card card-shadow">
          <div class="welcome-content">
            <div class="welcome-left">
              <h2>欢迎回来，{{ userStore.userInfo?.realName }}！</h2>
              <p>今天是 {{ currentDate }}，祝您身体健康</p>
            </div>
            <div class="welcome-right">
              <el-button type="primary" size="large" @click="goToAppointment">
                <el-icon><Calendar /></el-icon>
                立即预约
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷入口 -->
      <el-col :span="24" style="margin-top: 20px;">
        <el-card class="card-shadow">
          <template #header>
            <span class="card-title">快捷入口</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="6" v-for="item in quickLinks" :key="item.title">
              <div class="quick-link-item" @click="handleQuickLink(item.path)">
                <el-icon :size="40" :color="item.color">
                  <component :is="item.icon" />
                </el-icon>
                <h4>{{ item.title }}</h4>
                <p>{{ item.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- 待就诊预约 -->
      <el-col :span="16" style="margin-top: 20px;">
        <el-card class="card-shadow">
          <template #header>
            <div class="card-header">
              <span class="card-title">待就诊预约</span>
              <el-link type="primary" @click="router.push('/patient/appointments')">查看全部</el-link>
            </div>
          </template>
          <el-empty v-if="appointments.length === 0" description="暂无待就诊预约" />
          <div v-else class="appointment-list">
            <div 
              v-for="item in appointments" 
              :key="item.id"
              class="appointment-item"
            >
              <div class="appointment-time">
                <span class="date">{{ item.date }}</span>
                <span class="time">{{ item.time }}</span>
              </div>
              <div class="appointment-info">
                <h4>{{ item.doctorName }} - {{ item.title }}</h4>
                <p>{{ item.department }} | {{ item.location }}</p>
              </div>
              <div class="appointment-actions">
                <el-tag :type="getStatusType(item.status)">{{ item.status }}</el-tag>
                <el-button text type="primary">详情</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 健康提示 -->
      <el-col :span="8" style="margin-top: 20px;">
        <el-card class="card-shadow health-tips">
          <template #header>
            <span class="card-title">健康提示</span>
          </template>
          <div class="tips-content">
            <el-icon :size="60" color="#67C23A"><SuccessFilled /></el-icon>
            <h3>保持健康生活方式</h3>
            <ul class="tips-list">
              <li>✓ 规律作息，充足睡眠</li>
              <li>✓ 均衡饮食，适量运动</li>
              <li>✓ 定期体检，关注健康</li>
              <li>✓ 及时就医，遵医嘱用药</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const currentDate = computed(() => {
  const date = new Date()
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 星期${days[date.getDay()]}`
})

const quickLinks = [
  {
    title: '预约挂号',
    desc: '在线预约专家',
    icon: 'Calendar',
    color: '#409EFF',
    path: '/patient/appointment'
  },
  {
    title: '查找医生',
    desc: '查看医生信息',
    icon: 'Avatar',
    color: '#67C23A',
    path: '/patient/doctors'
  },
  {
    title: '我的预约',
    desc: '查看预约记录',
    icon: 'Document',
    color: '#E6A23C',
    path: '/patient/appointments'
  },
  {
    title: '就诊记录',
    desc: '历史就诊信息',
    icon: 'Notebook',
    color: '#F56C6C',
    path: '/patient/records'
  }
]

const appointments = ref([
  {
    id: 1,
    date: '2025-12-15',
    time: '09:00',
    doctorName: '张医生',
    title: '主任医师',
    department: '内科',
    location: '门诊楼3楼',
    status: '待就诊'
  }
])

const getStatusType = (status) => {
  const typeMap = {
    '待就诊': 'warning',
    '就诊中': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return typeMap[status] || 'info'
}

const goToAppointment = () => {
  router.push('/patient/appointment')
}

const handleQuickLink = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.patient-home {
  .welcome-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    :deep(.el-card__body) {
      padding: 40px;
    }
  }

  .welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h2 {
      font-size: 28px;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 16px;
      opacity: 0.9;
    }
  }

  .card-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .quick-link-item {
    text-align: center;
    padding: 30px 20px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: #f5f7fa;
      transform: translateY(-5px);
    }
    
    h4 {
      margin: 15px 0 5px;
      font-size: 16px;
      color: #333;
    }
    
    p {
      font-size: 14px;
      color: #999;
    }
  }

  .appointment-list {
    .appointment-item {
      display: flex;
      align-items: center;
      padding: 20px;
      margin-bottom: 15px;
      background: #f5f7fa;
      border-radius: 8px;
      transition: all 0.3s ease;
      
      &:hover {
        background: #e8f4ff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
      
      &:last-child {
        margin-bottom: 0;
      }
    }

    .appointment-time {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-right: 20px;
      border-right: 2px solid #ddd;
      min-width: 100px;
      
      .date {
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
      
      .time {
        font-size: 14px;
        color: #666;
        margin-top: 5px;
      }
    }

    .appointment-info {
      flex: 1;
      padding-left: 20px;
      
      h4 {
        font-size: 16px;
        color: #333;
        margin-bottom: 8px;
      }
      
      p {
        font-size: 14px;
        color: #666;
      }
    }

    .appointment-actions {
      display: flex;
      align-items: center;
      gap: 15px;
    }
  }

  .health-tips {
    .tips-content {
      text-align: center;
      padding: 20px 0;
      
      h3 {
        margin: 20px 0;
        color: #333;
      }
    }

    .tips-list {
      text-align: left;
      list-style: none;
      padding: 0;
      
      li {
        padding: 10px 0;
        font-size: 14px;
        color: #666;
      }
    }
  }
}
</style>
