import request from '@/utils/request';

export function getAvailableDates(doctorId, days = 14) {
  return request({
    url: `/api/schedules/doctor/${doctorId}/dates`,
    method: 'get',
    params: { days }
  });
}

export function getTimeSlots(doctorId, date) {
  return request({
    url: `/api/schedules/doctor/${doctorId}/slots`,
    method: 'get',
    params: { date }
  });
}