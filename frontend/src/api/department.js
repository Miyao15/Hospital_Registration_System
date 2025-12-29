import request from '@/utils/request'

export function getAllDepartments() {
  return request({
    url: '/api/departments',
    method: 'get'
  })
}

export function getDepartmentsGroupedByCategory() {
    return request({
        url: '/api/departments/grouped',
        method: 'get'
    })
}
