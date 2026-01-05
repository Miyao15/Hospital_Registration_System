import request from '@/utils/request';

export function getAvailableDates(doctorId, days = 14) {
  return request({
    url: `/api/schedules/doctor/${doctorId}/dates`,
    method: 'get',
    params: { days, _t: Date.now() } // 添加时间戳避免缓存
  });
}

export function getTimeSlots(doctorId, date) {
  return request({
    url: `/api/schedules/doctor/${doctorId}/slots`,
    method: 'get',
    params: { date, _t: Date.now() } // 添加时间戳避免缓存
  });
}