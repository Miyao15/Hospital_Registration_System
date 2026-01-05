import request from '@/utils/request'

export function getAllHospitals() {
  return request({
    url: '/api/hospitals',
    method: 'get'
  })
}

export function getHospitalById(id) {
  return request({
    url: `/api/hospitals/${id}`,
    method: 'get'
  })
}
