import request from '@/utils/request'

export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

export function getOrders(params) {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

export function getOrderDetail(orderNo) {
  return request({
    url: `/orders/${orderNo}`,
    method: 'get'
  })
}

export function cancelOrder(orderNo) {
  return request({
    url: `/orders/${orderNo}/cancel`,
    method: 'post'
  })
}

