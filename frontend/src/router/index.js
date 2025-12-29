import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/landing'
  },
  {
    path: '/landing',
    name: 'Landing',
    component: () => import('@/views/Landing.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/search-triage',
    name: 'SearchTriage',
    component: () => import('@/views/SearchTriage.vue'),
    meta: { title: '分诊' }
  },
  {
    path: '/search-results',
    name: 'SearchResults',
    component: () => import('@/views/SearchResults.vue'),
    meta: { title: '搜索结果' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/booking/info',
    name: 'BookingInfo',
    component: () => import('@/views/patient/BookingInfo.vue'),
    meta: { title: '预约信息' }
  },
  {
    path: '/department-doctors',
    name: 'DepartmentDoctors',
    component: () => import('@/views/patient/DepartmentDoctors.vue'),
    meta: { title: '选择医生' }
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
    meta: { requiresAuth: true, role: 'PATIENT' },
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
    meta: { requiresAuth: true, role: 'DOCTOR' },
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
    meta: { requiresAuth: true, role: 'ADMIN' },
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

  // Explicitly handle unauthenticated access to patient home as per instruction
  if (to.path === '/patient/home' && !userStore.isLoggedIn) {
    next('/login');
    return; // Important to return after next()
  }
  
  if (to.meta.title) {
    document.title = `${to.meta.title} - 医院门诊预约挂号系统`
  }
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.meta.role && userStore.userRole !== to.meta.role) {
    // Role check fails, redirect to a safe page (e.g., landing)
    // Avoid redirecting to login if they are already logged in but have the wrong role
    if (userStore.isLoggedIn) {
      next('/landing'); 
    } else {
      next('/login');
    }
  } else {
    next()
  }
})

export default router
