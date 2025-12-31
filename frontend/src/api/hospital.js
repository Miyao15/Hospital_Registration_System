import request from '@/utils/request'

// 获取所有医院
export function getAllHospitals() {
  return request({
    url: '/hospitals',
    method: 'get'
  })
}

// 获取医院详情
export function getHospitalById(id) {
  return request({
    url: `/hospitals/${id}`,
    method: 'get'
  })
}
