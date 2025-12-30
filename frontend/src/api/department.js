import request from '@/utils/request'

export function getAllDepartments() {
  return request({
    url: '/api/departments',
    method: 'get'
  })
}

// 别名函数，用于兼容性
export function listAllDepartments() {
  return getAllDepartments()
}

export function getDepartmentsGroupedByCategory() {
    return request({
        url: '/api/departments/grouped',
        method: 'get'
    })
}
