import request from '@/utils/request'

export function getAdminProducts(params) {
  return request({
    url: '/admin/products',
    method: 'get',
    params
  })
}

export function getAdminProductDetail(id) {
  return request({
    url: `/admin/products/${id}`,
    method: 'get'
  })
}

export function createProduct(data) {
  return request({
    url: '/admin/products',
    method: 'post',
    data
  })
}

export function updateProduct(id, data) {
  return request({
    url: `/admin/products/${id}`,
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/admin/products/${id}`,
    method: 'delete'
  })
}

export function updateProductStatus(id, status) {
  return request({
    url: `/admin/products/${id}/status`,
    method: 'put',
    data: { status }
  })
}

