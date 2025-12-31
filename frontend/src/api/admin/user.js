import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情（包含购买记录和浏览记录）
 */
export function getAdminUserDetail(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}

