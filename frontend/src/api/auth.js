import axios from 'axios';

// 创建 axios 实例
export const api = axios.create({
  baseURL: '/api', 
  timeout: 5000
});

// 请求拦截器：自动在请求头中注入 JWT Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：统一处理错误日志
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API请求详情:', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data
    });
    return Promise.reject(error);
  }
);

// 患者注册
export const registerPatient = (data) => {
  return api.post('/auth/register/patient', data);
};

// 医生注册
export const registerDoctor = (data) => {
  return api.post('/auth/register/doctor', data);
};

// 管理员注册
export const registerAdmin = (data) => {
  return api.post('/auth/register/admin', data);
};

// 通用注册接口 (满足 user.js 的导入需求)
export const register = (data, role) => {
  // 根据角色动态拼接路径，例如 /auth/register/patient
  const rolePath = role.toLowerCase();
  return api.post(`/auth/register/${rolePath}`, data);
};

// 登录接口 (支持三种角色)
export const login = (credentials) => {
  return api.post('/auth/login', credentials);
};

// 获取当前登录用户信息
export const getUserInfo = () => {
  return api.get('/auth/me');
};

// 更新用户信息
export const updateUserInfo = (data) => {
  return api.put('/auth/update', data);
};