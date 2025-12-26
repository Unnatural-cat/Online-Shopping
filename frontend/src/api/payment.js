import request from '@/utils/request'

export function pay(orderNo, data) {
  return request({
    url: `/payments/${orderNo}/pay`,
    method: 'post',
    data
  })
}

export function getPaymentRecords(orderNo) {
  return request({
    url: `/payments/${orderNo}`,
    method: 'get'
  })
}

