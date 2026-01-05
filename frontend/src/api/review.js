import request from '@/utils/request';

/**
 * 获取医生的评价列表
 */
export function getDoctorReviews(doctorId, page = 0, size = 10) {
  return request({
    url: `/api/reviews/doctor/${doctorId}`,
    method: 'get',
    params: { page, size }
  });
}

/**
 * 获取医生的评价统计
 */
export function getDoctorReviewStats(doctorId) {
  return request({
    url: `/api/reviews/doctor/${doctorId}/stats`,
    method: 'get'
  });
}

/**
 * 创建评价（需要登录）
 */
export function createReview(data) {
  return request({
    url: '/api/reviews',
    method: 'post',
    data
  });
}

/**
 * 获取我的评价列表
 */
export function getMyReviews(page = 0, size = 10) {
  return request({
    url: '/api/reviews/my',
    method: 'get',
    params: { page, size }
  });
}

/**
 * 删除评价
 */
export function deleteReview(id) {
  return request({
    url: `/api/reviews/${id}`,
    method: 'delete'
  });
}
