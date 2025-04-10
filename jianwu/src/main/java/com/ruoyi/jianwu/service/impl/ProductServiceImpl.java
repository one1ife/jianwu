package com.ruoyi.jianwu.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jianwu.mapper.ProductMapper;
import com.ruoyi.jianwu.domain.Product;
import com.ruoyi.jianwu.service.IProductService;

/**
 * 物品信息Service业务层处理
 * 
 * @author zouzy
 * @date 2025-04-10
 */
@Service
public class ProductServiceImpl implements IProductService 
{
    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询物品信息
     * 
     * @param productId 物品信息主键
     * @return 物品信息
     */
    @Override
    public Product selectProductByProductId(Long productId)
    {
        return productMapper.selectProductByProductId(productId);
    }

    /**
     * 查询物品信息列表
     * 
     * @param product 物品信息
     * @return 物品信息
     */
    @Override
    public List<Product> selectProductList(Product product)
    {
        return productMapper.selectProductList(product);
    }

    /**
     * 新增物品信息
     * 
     * @param product 物品信息
     * @return 结果
     */
    @Override
    public int insertProduct(Product product)
    {
        product.setCreateTime(DateUtils.getNowDate());
        return productMapper.insertProduct(product);
    }

    /**
     * 修改物品信息
     * 
     * @param product 物品信息
     * @return 结果
     */
    @Override
    public int updateProduct(Product product)
    {
        product.setUpdateTime(DateUtils.getNowDate());
        return productMapper.updateProduct(product);
    }

    /**
     * 批量删除物品信息
     * 
     * @param productIds 需要删除的物品信息主键
     * @return 结果
     */
    @Override
    public int deleteProductByProductIds(Long[] productIds)
    {
        return productMapper.deleteProductByProductIds(productIds);
    }

    /**
     * 删除物品信息信息
     * 
     * @param productId 物品信息主键
     * @return 结果
     */
    @Override
    public int deleteProductByProductId(Long productId)
    {
        return productMapper.deleteProductByProductId(productId);
    }
}
