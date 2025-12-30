import request from '@/utils/request'

export function searchDoctors(params) {
  return request({
    url: '/api/doctors/search',
    method: 'get',
    params
  })
}

export function getAllDoctors(params) {
  return request({
    url: '/api/doctors',
    method: 'get',
    params
  })
}

export function getDoctorById(id) {
    return request({
        url: `/api/doctors/${id}`,
        method: 'get'
    })
}

export function getDoctorsByDepartment(departmentId) {
  return request({
    url: '/api/doctors/search',
    method: 'get',
    params: { departmentId }
  })
}

export function getDoctorReviews(doctorId, params) {
  return request({
    url: `/api/doctors/${doctorId}/reviews`,
    method: 'get',
    params
  })
}

export function getTopDoctors(limit = 10) {
  return request({
    url: '/api/doctors/top',
    method: 'get',
    params: { limit }
  })
}

// 获取当前登录医生的详细信息
export function getMyDoctorInfo() {
  return request({
    url: '/api/doctor/profile',
    method: 'get'
  })
}

// 获取所有医生列表（管理员用）
export function listAllDoctors(params) {
  return request({
    url: '/api/doctors',
    method: 'get',
    params
  })
}
