import request from '@/utils/request'

export function getCart() {
  return request({
    url: '/cart',
    method: 'get'
  })
}

export function addCartItem(data) {
  return request({
    url: '/cart/items',
    method: 'post',
    data
  })
}

export function updateCartItem(id, data) {
  return request({
    url: `/cart/items/${id}`,
    method: 'put',
    data
  })
}

export function deleteCartItem(id) {
  return request({
    url: `/cart/items/${id}`,
    method: 'delete'
  })
}

export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

