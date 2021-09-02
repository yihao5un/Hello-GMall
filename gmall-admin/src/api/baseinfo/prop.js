import request from '@/utils/request'

export default {

  // 查找一级分类
  getCategory1() {
    return request({
      url: '/admin/product/getCategory1',
      method: 'get'
    })
  },

  // 查找二级分类
  getCategory2(category1Id) {
    return request({
      url: '/admin/product/getCategory2/' + category1Id,
      method: 'get'
    })
  },

  // 查找三级分类
  getCategory3(category2Id) {
    return request({
      url: '/admin/product/getCategory3/' + category2Id,
      method: 'get'
    })
  },

  // 查找品牌
  getTrademarkList() {
    return request({
      url: '/admin/product/baseTrademark/getTrademarkList',
      method: 'get'
    })
  },

  // 根据分类id获取属性列表
  getAttrInfoList(category1Id, category2Id, category3Id) {
    return request({
      url: '/admin/product/attrInfoList/' + category1Id + '/' + category2Id + '/' + category3Id,
      method: 'get'
    })
  },

  // 根据属性id获取属性值列表
  getAttrValueList(attrId) {
    return request({
      url: '/admin/product/getAttrValueList/' + attrId,
      method: 'get'
    })
  },

  // 保存属性
  saveAttrInfo(attrForm) {
    return request({
      url: '/admin/product/saveAttrInfo',
      method: 'post',
      data: attrForm
    })
  }
}
