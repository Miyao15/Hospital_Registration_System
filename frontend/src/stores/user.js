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
      const res = await login(loginForm)
      token.value = res.data.token
      userInfo.value = res.data.userInfo
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }
  
  // 注册
  const registerAction = async (registerForm) => {
    try {
      await register(registerForm)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
      return false
    }
  }
  
  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
  
  // 更新用户信息
  const updateUserInfoAction = async (data) => {
    try {
      await updateUserInfo(data)
      await fetchUserInfo()
      ElMessage.success('更新成功')
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
