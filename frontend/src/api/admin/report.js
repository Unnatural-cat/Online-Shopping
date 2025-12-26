import request from '@/utils/request'

export function getSalesSummary(params) {
  return request({
    url: '/admin/reports/summary',
    method: 'get',
    params
  })
}

export function getSalesTrend(params) {
  return request({
    url: '/admin/reports/sales-trend',
    method: 'get',
    params
  })
}

export function getTopProducts(params) {
  return request({
    url: '/admin/reports/top-products',
    method: 'get',
    params
  })
}

export function getOrderStatusDistribution(params) {
  return request({
    url: '/admin/reports/order-status',
    method: 'get',
    params
  })
}

