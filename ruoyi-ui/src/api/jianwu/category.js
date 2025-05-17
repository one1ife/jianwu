import request from '@/utils/request'

// 查询物品分类列表
export function listCategory(query) {
  return request({
    url: '/jianwu/category/list',
    method: 'get',
    params: query
  })
}

// 查询指定用户的物品分类列表
export function listCategoryByUser(userId) {
  return request({
    url: '/jianwu/category/user/' + userId,
    method: 'get'
  })
}

// 查询物品分类详细
export function getCategory(categoryId) {
  return request({
    url: '/jianwu/category/' + categoryId,
    method: 'get'
  })
}

// 新增物品分类
export function addCategory(data) {
  return request({
    url: '/jianwu/category',
    method: 'post',
    data: data
  })
}

// 修改物品分类
export function updateCategory(data) {
  return request({
    url: '/jianwu/category',
    method: 'put',
    data: data
  })
}

// 删除物品分类
export function delCategory(categoryId) {
  return request({
    url: '/jianwu/category/' + categoryId,
    method: 'delete'
  })
}

// 获取分类下物品数量
export function getCategoryProductCount(userId) {
  return request({
    url: '/jianwu/category/counts/' + userId,
    method: 'get'
  })
}