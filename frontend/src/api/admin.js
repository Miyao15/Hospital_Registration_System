import request from '@/utils/request'

// ==================== 用户管理 ====================
export function listUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(userId, status) {
  return request({
    url: `/api/admin/users/${userId}/status`,
    method: 'put',
    data: { status }
  })
}

// ==================== 医生审批 ====================
export function approveDoctor(doctorId) {
  return request({
    url: `/api/admin/doctors/${doctorId}/approve`,
    method: 'put'
  })
}

export function updateDoctorProfile(doctorId, data) {
  return request({
    url: `/api/admin/doctors/${doctorId}/profile`,
    method: 'put',
    data
  })
}

// ==================== 科室管理 ====================
export function getAllDepartments() {
  return request({
    url: '/api/admin/departments',
    method: 'get'
  })
}

export function createDepartment(data) {
  return request({
    url: '/api/admin/departments',
    method: 'post',
    data
  })
}

export function updateDepartment(deptId, data) {
  return request({
    url: `/api/admin/departments/${deptId}`,
    method: 'put',
    data
  })
}

export function deleteDepartment(deptId) {
  return request({
    url: `/api/admin/departments/${deptId}`,
    method: 'delete'
  })
}

export function enableDepartment(deptId) {
  return request({
    url: `/api/admin/departments/${deptId}/enable`,
    method: 'post'
  })
}

// ==================== 排班管理 ====================
export function getSchedules(params) {
  return request({
    url: '/api/admin/schedules',
    method: 'get',
    params
  })
}

export function batchCreateSchedules(data) {
  return request({
    url: '/api/admin/schedules',
    method: 'post',
    data
  })
}

export function cancelSchedule(doctorId, date) {
  return request({
    url: `/api/admin/schedules/${doctorId}/${date}`,
    method: 'delete'
  })
}

export function updateSlotCount(slotId, totalSlots) {
  return request({
    url: `/api/admin/time-slots/${slotId}`,
    method: 'put',
    data: { totalSlots }
  })
}

// ==================== 请假审批 ====================
export function getPendingLeaves(params) {
  return request({
    url: '/api/admin/leaves/pending',
    method: 'get',
    params
  })
}

export function getAllLeaves(params) {
  return request({
    url: '/api/admin/leaves',
    method: 'get',
    params
  })
}

export function approveLeave(leaveId) {
  return request({
    url: `/api/admin/leaves/${leaveId}/approve`,
    method: 'post'
  })
}

export function rejectLeave(leaveId, reason) {
  return request({
    url: `/api/admin/leaves/${leaveId}/reject`,
    method: 'post',
    data: { reason }
  })
}

// ==================== 预约管理 ====================
export function getAllAppointments(params) {
  return request({
    url: '/api/admin/appointments',
    method: 'get',
    params
  })
}

// ==================== 统计 ====================
export function getDashboardStatistics() {
  return request({
    url: '/api/statistics/dashboard',
    method: 'get'
  })
}

export function getAppointmentTrend(days = 7) {
  return request({
    url: '/api/statistics/trend',
    method: 'get',
    params: { days }
  })
}

// ==================== 患者信息管理 ====================
export function updatePatientInfo(userId, data) {
  return request({
    url: `/api/admin/patients/${userId}/info`,
    method: 'put',
    data
  })
}

// ==================== 用户密码管理 ====================
export function adminChangeUserPassword(userId, newPassword) {
  return request({
    url: `/api/admin/users/${userId}/password`,
    method: 'put',
    data: { newPassword }
  })
}

// ==================== 预约管理 ====================
export function adminCancelAppointment(appointmentId, reason) {
  return request({
    url: `/api/admin/appointments/${appointmentId}/cancel`,
    method: 'post',
    data: { reason }
  })
}

export function adminUpdateAppointment(appointmentId, data) {
  return request({
    url: `/api/admin/appointments/${appointmentId}`,
    method: 'put',
    data
  })
}

