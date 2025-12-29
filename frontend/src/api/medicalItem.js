import request from '@/utils/request';

export function getAllMedicalItems() {
  return request({
    url: '/api/medical-items',
    method: 'get'
  });
}

export function getMedicalItemById(id) {
  return request({
    url: `/api/medical-items/${id}`,
    method: 'get'
  });
}
