import request from '@/utils/request'

export function getProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

export function getAddresses() {
  return request({
    url: '/user/addresses',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/user/addresses',
    method: 'post',
    data
  })
}

export function updateAddress(id, data) {
  return request({
    url: `/user/addresses/${id}`,
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/user/addresses/${id}`,
    method: 'delete'
  })
}

export function setDefaultAddress(id) {
  return request({
    url: `/user/addresses/${id}/default`,
    method: 'put'
  })
}

