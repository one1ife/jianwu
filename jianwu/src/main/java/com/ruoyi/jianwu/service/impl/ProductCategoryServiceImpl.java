package com.ruoyi.jianwu.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jianwu.mapper.ProductCategoryMapper;
import com.ruoyi.jianwu.domain.ProductCategory;
import com.ruoyi.jianwu.service.IProductCategoryService;

/**
 * 物品分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-20
 */
@Service
public class ProductCategoryServiceImpl implements IProductCategoryService 
{
    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    /**
     * 查询物品分类
     * 
     * @param categoryId 物品分类主键
     * @return 物品分类
     */
    @Override
    public ProductCategory selectProductCategoryByCategoryId(Long categoryId)
    {
        return productCategoryMapper.selectProductCategoryByCategoryId(categoryId);
    }

    /**
     * 查询物品分类列表
     * 
     * @param productCategory 物品分类
     * @return 物品分类
     */
    @Override
    public List<ProductCategory> selectProductCategoryList(ProductCategory productCategory)
    {
        return productCategoryMapper.selectProductCategoryList(productCategory);
    }
    
    /**
     * 查询指定用户的物品分类列表
     * 
     * @param userId 用户ID
     * @return 物品分类集合
     */
    @Override
    public List<ProductCategory> selectProductCategoryByUserId(Long userId)
    {
        return productCategoryMapper.selectProductCategoryByUserId(userId);
    }

    /**
     * 新增物品分类
     * 
     * @param productCategory 物品分类
     * @return 结果
     */
    @Override
    public int insertProductCategory(ProductCategory productCategory)
    {
        productCategory.setCreateTime(DateUtils.getNowDate());
        return productCategoryMapper.insertProductCategory(productCategory);
    }

    /**
     * 修改物品分类
     * 
     * @param productCategory 物品分类
     * @return 结果
     */
    @Override
    public int updateProductCategory(ProductCategory productCategory)
    {
        productCategory.setUpdateTime(DateUtils.getNowDate());
        return productCategoryMapper.updateProductCategory(productCategory);
    }

    /**
     * 批量删除物品分类
     * 
     * @param categoryIds 需要删除的物品分类主键
     * @return 结果
     */
    @Override
    public int deleteProductCategoryByCategoryIds(Long[] categoryIds)
    {
        return productCategoryMapper.deleteProductCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除物品分类信息
     * 
     * @param categoryId 物品分类主键
     * @return 结果
     */
    @Override
    public int deleteProductCategoryByCategoryId(Long categoryId)
    {
        return productCategoryMapper.deleteProductCategoryByCategoryId(categoryId);
    }
    
    /**
     * 为用户创建默认分类
     * 
     * @param userId 用户ID
     * @param createBy 创建者
     */
    @Override
    public void createDefaultCategories(Long userId, String createBy)
    {
        productCategoryMapper.createDefaultCategories(userId, createBy);
    }
} 