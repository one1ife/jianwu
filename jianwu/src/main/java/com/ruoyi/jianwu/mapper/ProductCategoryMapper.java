package com.ruoyi.jianwu.mapper;

import java.util.List;
import com.ruoyi.jianwu.domain.ProductCategory;

/**
 * 物品分类Mapper接口
 * 
 * @author ruoyi
 * @date 2023-05-20
 */
public interface ProductCategoryMapper 
{
    /**
     * 查询物品分类
     * 
     * @param categoryId 物品分类主键
     * @return 物品分类
     */
    public ProductCategory selectProductCategoryByCategoryId(Long categoryId);

    /**
     * 查询物品分类列表
     * 
     * @param productCategory 物品分类
     * @return 物品分类集合
     */
    public List<ProductCategory> selectProductCategoryList(ProductCategory productCategory);
    
    /**
     * 查询指定用户的物品分类列表
     * 
     * @param userId 用户ID
     * @return 物品分类集合
     */
    public List<ProductCategory> selectProductCategoryByUserId(Long userId);

    /**
     * 新增物品分类
     * 
     * @param productCategory 物品分类
     * @return 结果
     */
    public int insertProductCategory(ProductCategory productCategory);

    /**
     * 修改物品分类
     * 
     * @param productCategory 物品分类
     * @return 结果
     */
    public int updateProductCategory(ProductCategory productCategory);

    /**
     * 删除物品分类
     * 
     * @param categoryId 物品分类主键
     * @return 结果
     */
    public int deleteProductCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除物品分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProductCategoryByCategoryIds(Long[] categoryIds);
    
    /**
     * 创建用户默认分类
     * 
     * @param userId 用户ID
     * @param createBy 创建者
     */
    public void createDefaultCategories(Long userId, String createBy);
} 