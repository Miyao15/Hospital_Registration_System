import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserInfo, updateUserInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value?.role || '')
  
  // 登录
  const loginAction = async (loginForm) => {
    try {
      // TODO: 临时模拟登录，等后端完成后替换为真实 API
      // const res = await login(loginForm)
      
      // 模拟登录成功
      const mockToken = 'mock-token-' + Date.now()
      const mockUserInfo = {
        id: 1,
        username: loginForm.username,
        realName: '测试用户',
        role: loginForm.role,
        phone: '13800138000',
        avatar: ''
      }
      
      token.value = mockToken
      userInfo.value = mockUserInfo
      localStorage.setItem('token', mockToken)
      localStorage.setItem('userInfo', JSON.stringify(mockUserInfo))
      ElMessage.success('登录成功（演示模式）')
      return true
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }
  
  // 注册
  const registerAction = async (registerForm) => {
    try {
      // TODO: 临时模拟注册，等后端完成后替换为真实 API
      // await register(registerForm)
      
      // 模拟注册成功
      await new Promise(resolve => setTimeout(resolve, 1000))
      ElMessage.success('注册成功，请登录（演示模式）')
      return true
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
      return false
    }
  }
  
  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      // TODO: 临时使用本地数据，等后端完成后替换为真实 API
      // const res = await getUserInfo()
      // userInfo.value = res.data
      // localStorage.setItem('userInfo', JSON.stringify(res.data))
      
      // 使用本地存储的用户信息
      const savedUserInfo = localStorage.getItem('userInfo')
      if (savedUserInfo) {
        userInfo.value = JSON.parse(savedUserInfo)
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
  
  // 更新用户信息
  const updateUserInfoAction = async (data) => {
    try {
      // TODO: 临时模拟更新，等后端完成后替换为真实 API
      // await updateUserInfo(data)
      
      // 模拟更新成功
      await new Promise(resolve => setTimeout(resolve, 500))
      
      // 更新本地数据
      userInfo.value = { ...userInfo.value, ...data }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      
      ElMessage.success('更新成功（演示模式）')
      return true
    } catch (error) {
      ElMessage.error(error.message || '更新失败')
      return false
    }
  }
  
  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
  }
  
  // 初始化用户信息
  const initUserInfo = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
    }
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    userRole,
    loginAction,
    registerAction,
    fetchUserInfo,
    updateUserInfoAction,
    logout,
    initUserInfo
  }
})
