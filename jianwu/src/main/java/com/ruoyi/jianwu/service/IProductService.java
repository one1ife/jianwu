package com.ruoyi.jianwu.service;

import java.util.List;
import com.ruoyi.jianwu.domain.Product;

/**
 * 物品信息Service接口
 * 
 * @author zouzy
 * @date 2025-04-10
 */
public interface IProductService 
{
    /**
     * 查询物品信息
     * 
     * @param productId 物品信息主键
     * @return 物品信息
     */
    public Product selectProductByProductId(Long productId);

    /**
     * 查询物品信息列表
     * 
     * @param product 物品信息
     * @return 物品信息集合
     */
    public List<Product> selectProductList(Product product);

    /**
     * 新增物品信息
     * 
     * @param product 物品信息
     * @return 结果
     */
    public int insertProduct(Product product);

    /**
     * 修改物品信息
     * 
     * @param product 物品信息
     * @return 结果
     */
    public int updateProduct(Product product);

    /**
     * 批量删除物品信息
     * 
     * @param productIds 需要删除的物品信息主键集合
     * @return 结果
     */
    public int deleteProductByProductIds(Long[] productIds);

    /**
     * 删除物品信息信息
     * 
     * @param productId 物品信息主键
     * @return 结果
     */
    public int deleteProductByProductId(Long productId);
    
    /**
     * 统计分类下的物品数量
     * 
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @return 物品数量
     */
    public int countProductByCategoryId(Long categoryId, Long userId);
}
