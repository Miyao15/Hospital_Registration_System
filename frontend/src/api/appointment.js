import request from '@/utils/request'

export function createAppointment(data) {
  return request({
    url: '/api/appointments',
    method: 'post',
    data
  })
}

export function getMyAppointments(params) {
  return request({
    url: '/api/appointments/my',
    method: 'get',
    params
  })
}

export function cancelAppointment(id, reason) {
  return request({
    url: `/api/appointments/${id}/cancel`,
    method: 'post',
    params: { reason }
  })
}

export function rescheduleAppointment(id, data) {
  return request({
    url: `/api/appointments/${id}/reschedule`,
    method: 'post',
    data
  })
}
