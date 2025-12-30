import request from '@/utils/request'

export function getNotifications(params) {
  return request({
    url: '/api/notifications',
    method: 'get',
    params
  })
}

export function getUnreadCount() {
  return request({
    url: '/api/notifications/unread-count',
    method: 'get'
  })
}

export function markAsRead(id) {
  return request({
    url: `/api/notifications/${id}/read`,
    method: 'post'
  })
}

export function markAllAsRead() {
  return request({
    url: '/api/notifications/read-all',
    method: 'post'
  })
}

export function deleteNotification(id) {
  return request({
    url: `/api/notifications/${id}`,
    method: 'delete'
  })
}

