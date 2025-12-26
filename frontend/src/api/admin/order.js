import request from '@/utils/request'

export function getAdminOrders(params) {
  return request({
    url: '/admin/orders',
    method: 'get',
    params
  })
}

export function getAdminOrderDetail(orderNo) {
  return request({
    url: `/admin/orders/${orderNo}`,
    method: 'get'
  })
}

export function shipOrder(orderNo, data) {
  return request({
    url: `/admin/orders/${orderNo}/ship`,
    method: 'put',
    data
  })
}

