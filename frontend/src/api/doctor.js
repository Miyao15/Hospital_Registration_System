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
