import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login as apiLogin, registerPatient, registerDoctor, registerAdmin } from '@/api/auth';
import { ElMessage } from 'element-plus';
import router from '@/router';

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '');
  const userRole = ref(localStorage.getItem('userRole') || '');
  const userInfo = ref(null); // Add userInfo ref

  // Getters
  const isLoggedIn = computed(() => !!token.value);

  // Actions
  /**
   * Handles user login.
   * @param {object} credentials - The user's login credentials { identifier, password }.
   */
  const login = async (credentials) => {
    try {
      const response = await apiLogin(credentials);
      // `response` is the unwrapped data: { accessToken, refreshToken, expiresIn, role }
      
      token.value = response.accessToken;
      userRole.value = response.role;

      localStorage.setItem('token', response.accessToken);
      localStorage.setItem('userRole', response.role);
      
      ElMessage.success('登录成功！');

      // 检查是否有待处理的预约信息（只有在未登录状态下点击预约后才会有）
      const pendingBooking = localStorage.getItem('pendingBooking');
      if (pendingBooking && response.role === 'PATIENT') {
        try {
          const bookingData = JSON.parse(pendingBooking);
          // 清除待处理的预约信息
          localStorage.removeItem('pendingBooking');
          // 跳转到预约信息填写页面
          router.push({
            path: '/booking/info',
            query: bookingData
          });
          return true;
        } catch (e) {
          console.error('恢复预约信息失败:', e);
        }
      }

      // 正常登录后，根据角色跳转到对应的首页
      // 不再使用redirect参数，确保登录后跳转的一致性
      switch (response.role) {
        case 'ADMIN':
          router.push('/admin/home');
          break;
        case 'DOCTOR':
          router.push('/doctor/home');
          break;
        case 'PATIENT':
          router.push('/patient/home');
          break;
        default:
          router.push('/');
      }
      return true;
    } catch (error) {
      // request.js的拦截器已经显示了错误消息，这里不需要重复显示
      // 只需要返回false表示登录失败
      console.error('登录失败:', error);
      return false;
    }
  };
  
  /**
   * Handles user registration.
   * @param {object} formData - The user's registration data.
   * @param {string} role - The role of the user to register ('patient', 'doctor', or 'admin').
   */
  const register = async (formData, role) => {
    try {
      let message = '注册申请已提交';
      if (role === 'patient') {
        await registerPatient(formData);
        message = '患者注册成功！';
      } else if (role === 'doctor') {
        await registerDoctor(formData);
        message = '医生注册申请已提交，请等待管理员审核。';
      } else if (role === 'admin') {
        await registerAdmin(formData);
        message = '管理员注册成功！';
      } else {
        throw new Error('不支持的注册类型');
      }
      
      ElMessage.success(message);
      return true;
    } catch (error) {
      // request.js的拦截器已经显示了错误消息，这里不需要重复显示
      console.error('注册失败:', error);
      return false;
    }
  };

  /**
   * Handles user logout.
   */
  const logoutAction = () => {
    token.value = '';
    userRole.value = '';
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    ElMessage.success('已退出登录');
    router.push('/login');
  };

  return {
    token,
    userRole,
    isLoggedIn,
    login,
    register,
    logout: logoutAction,
  };
}, {
  persist: true, // Automatically persists state to localStorage
});
