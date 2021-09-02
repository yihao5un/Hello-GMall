import request from '@/utils/request'

const api_name = '/admin/order/orderRefundInfo'

export default {

  getPageList(page, limit, searchObj) {
    return request({
      url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: searchObj // url查询字符串或表单键值对
    })
  },

  approval(refundApproval) {
    return request({
      url: `${api_name}/approval`,
      method: 'post',
      data: refundApproval
    })
  },

  recieve(id) {
    return request({
      url: `${api_name}/recieve/${id}`,
      method: 'get'
    })
  }
}

