import request from '@/utils/request'

export function createReview(data) {
  return request({
    url: '/api/reviews',
    method: 'post',
    data
  })
}

export function getMyReviews(params) {
  return request({
    url: '/api/reviews/my',
    method: 'get',
    params
  })
}

export function deleteReview(id) {
  return request({
    url: `/api/reviews/${id}`,
    method: 'delete'
  })
}

