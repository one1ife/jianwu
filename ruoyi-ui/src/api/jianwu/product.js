import request from '@/utils/request'

// 查询物品信息列表
export function listProduct(query) {
  return request({
    url: '/jianwu/product/list',
    method: 'get',
    params: query
  })
}

// 查询物品信息详细
export function getProduct(productId) {
  return request({
    url: '/jianwu/product/' + productId,
    method: 'get'
  })
}

// 新增物品信息
export function addProduct(data) {
  return request({
    url: '/jianwu/product',
    method: 'post',
    data: data
  })
}

// 修改物品信息
export function updateProduct(data) {
  return request({
    url: '/jianwu/product',
    method: 'put',
    data: data
  })
}

// 删除物品信息
export function delProduct(productId) {
  return request({
    url: '/jianwu/product/' + productId,
    method: 'delete'
  })
}
