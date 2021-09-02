import request from '@/utils/request'

export default {

  getPageList(page, limit) {
    return request({
      url: `/admin/product/list/${page}/${limit}`,
      method: 'get'
    })
  },

  // 保存Sku
  saveSkuInfo(skuForm) {
    return request({
      url: '/admin/product/saveSkuInfo',
      method: 'post',
      data: skuForm
    })
  },

  // 商品上架
  onSale(skuId) {
    return request({
      url: `/admin/product/onSale/${skuId}`,
      method: 'get'
    })
  },

  // 商品下架
  cancelSale(skuId) {
    return request({
      url: `/admin/product/cancelSale/${skuId}`,
      method: 'get'
    })
  },

  findSkuInfoByKeyword(keyword) {
    return request({
      url: `/admin/product/findSkuInfoByKeyword/${keyword}`,
      method: 'get'
    })
  }
}
