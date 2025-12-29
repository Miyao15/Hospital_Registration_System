import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 后端返回的 ApiResponse 格式为 { success: boolean, data: T, error: ErrorInfo }
    // success 为 true 表示业务成功
    if (!res.success) {
      const errorInfo = res.error || {};
      if (errorInfo.details && Object.keys(errorInfo.details).length > 0) {
        // Display each detailed error message
        for (const field in errorInfo.details) {
          ElMessage.error(`${field}: ${errorInfo.details[field]}`);
        }
      } else {
        // Fallback to top-level message
        ElMessage.error(errorInfo.message || '请求失败');
      }

      // 根据后端的错误码进行特定处理，例如 token 失效
      // 在 BusinessException 中定义的 AUTH_003 表示 token 无效
      if (errorInfo.code === 'AUTH_001' || errorInfo.code === 'AUTH_002' || errorInfo.code === 'AUTH_003') {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        router.push('/login');
      }
      
      return Promise.reject(new Error(errorInfo.message || '请求失败'));
    }
    
    // 如果 success 为 true，直接返回 data 字段
    return res.data;
  },
  error => {
    // 处理网络层面的错误
    ElMessage.error(error.message || '网络错误');
    return Promise.reject(error);
  }
)

export default request
