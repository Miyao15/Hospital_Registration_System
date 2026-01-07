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
    // 严格检查：只有当 success 明确为 false 时才进入错误处理
    if (res && res.success === false) {
      const errorInfo = res.error || {};
      
      // 处理details字段（验证错误）
      if (errorInfo.details && typeof errorInfo.details === 'object' && Object.keys(errorInfo.details).length > 0) {
        // 显示每个字段的详细错误
        for (const field in errorInfo.details) {
          const fieldName = getFieldDisplayName(field);
          ElMessage.error(`${fieldName}: ${errorInfo.details[field]}`);
        }
      } else if (errorInfo.message) {
        // 显示顶层错误消息
        ElMessage.error(errorInfo.message);
      } else {
        ElMessage.error('请求失败，请重试');
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
    
    // 如果 success 为 true 或未定义（兼容旧接口），直接返回 data 字段或整个响应
    // 优先返回 data 字段，如果没有 data 字段则返回整个响应
    if (res && res.data !== undefined) {
      return res.data;
    }
    // 兼容没有 ApiResponse 包装的响应
    return res;
  },
  error => {
    // 处理HTTP错误（401, 400, 500等）
    if (error.response) {
      const res = error.response.data;
      
      // 调试日志（开发环境）
      if (process.env.NODE_ENV === 'development') {
        console.log('错误响应:', {
          status: error.response.status,
          data: res,
          error: error
        });
      }
      
      // 如果后端返回了ApiResponse格式的错误
      if (res && typeof res === 'object') {
        // 检查是否有error字段（ApiResponse格式）
        if (res.error) {
          const errorInfo = res.error;
          
          // 处理details字段（验证错误）
          if (errorInfo.details && typeof errorInfo.details === 'object' && Object.keys(errorInfo.details).length > 0) {
            // 显示每个字段的详细错误
            for (const field in errorInfo.details) {
              const fieldName = getFieldDisplayName(field);
              ElMessage.error(`${fieldName}: ${errorInfo.details[field]}`);
            }
          } else if (errorInfo.message) {
            // 显示顶层错误消息
            ElMessage.error(errorInfo.message);
          } else {
            // 如果没有message，使用code作为提示
            ElMessage.error('操作失败，请重试');
          }
          
          // 根据错误码进行特定处理
          if (errorInfo.code === 'AUTH_001' || errorInfo.code === 'AUTH_002' || errorInfo.code === 'AUTH_003') {
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            router.push('/login');
          }
          
          return Promise.reject(new Error(errorInfo.message || '请求失败'));
        }
        
        // 如果没有error字段，但直接有message字段
        if (res.message) {
          ElMessage.error(res.message);
          return Promise.reject(new Error(res.message));
        }
      }
      
      // 处理403 Forbidden错误（通常是token无效或权限不足）
      if (error.response.status === 403) {
        // 清除本地存储的token和用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('userRole');
        ElMessage.warning('登录已过期，请重新登录');
        router.push('/login');
        return Promise.reject(new Error('登录已过期'));
      }
      
      // 处理401 Unauthorized错误
      if (error.response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('userRole');
        ElMessage.warning('未授权，请重新登录');
        router.push('/login');
        return Promise.reject(new Error('未授权'));
      }
      
      // 如果无法解析错误信息，显示通用错误
      const statusText = error.response.statusText || `HTTP ${error.response.status}`;
      ElMessage.error(`请求失败: ${statusText}`);
      return Promise.reject(new Error(`请求失败: ${statusText}`));
    }
    
    // 处理网络层面的错误（无响应）
    ElMessage.error(error.message || '网络错误，请检查网络连接');
    return Promise.reject(error);
  }
)

// 字段名映射（用于显示友好的字段名）
function getFieldDisplayName(field) {
  const fieldMap = {
    'phone': '手机号',
    'password': '密码',
    'name': '姓名',
    'idCard': '身份证号',
    'employeeId': '工号',
    'licenseNumber': '资格证号',
    'adminRegistrationKey': '管理员密钥',
    'title': '职称',
    'departmentId': '科室'
  };
  return fieldMap[field] || field;
}

export default request
