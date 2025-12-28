import request from '@/utils/request';

// 患者注册
export const registerPatient = (data) => {
  return request.post('/auth/register/patient', data);
};

// 医生注册
export const registerDoctor = (data) => {
  return request.post('/auth/register/doctor', data);
};

// 管理员注册
export const registerAdmin = (data) => {
  return request.post('/auth/register/admin', data);
};

// 通用注册接口 (满足 user.js 的导入需求)
export const register = (data, role) => {
  // 根据角色动态拼接路径，例如 /auth/register/patient
  const rolePath = role.toLowerCase();
  return request.post(`/auth/register/${rolePath}`, data);
};

// 登录接口 (支持三种角色)
export const login = (credentials) => {
  return request.post('/auth/login', credentials);
};

// 获取当前登录用户信息
export const getUserInfo = () => {
  return request.get('/auth/me');
};

// 更新用户信息
export const updateUserInfo = (data) => {
  return request.put('/auth/update', data);
};