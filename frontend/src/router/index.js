import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/patient',
    name: 'PatientLayout',
    component: () => import('@/layouts/PatientLayout.vue'),
    meta: { requiresAuth: true, role: 'patient' },
    children: [
      {
        path: 'home',
        name: 'PatientHome',
        component: () => import('@/views/patient/Home.vue'),
        meta: { title: '患者首页' }
      },
      {
        path: 'profile',
        name: 'PatientProfile',
        component: () => import('@/views/patient/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/doctor',
    name: 'DoctorLayout',
    component: () => import('@/layouts/DoctorLayout.vue'),
    meta: { requiresAuth: true, role: 'doctor' },
    children: [
      {
        path: 'home',
        name: 'DoctorHome',
        component: () => import('@/views/doctor/Home.vue'),
        meta: { title: '医生工作台' }
      },
      {
        path: 'profile',
        name: 'DoctorProfile',
        component: () => import('@/views/doctor/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('@/views/admin/Home.vue'),
        meta: { title: '管理后台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.title) {
    document.title = `${to.meta.title} - 医院门诊预约挂号系统`
  }
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.meta.role && userStore.userInfo?.role !== to.meta.role) {
    next('/')
  } else {
    next()
  }
})

export default router
