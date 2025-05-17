package com.ruoyi.jianwu.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jianwu.domain.Product;
import com.ruoyi.jianwu.service.IProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 物品信息Controller
 * 
 * @author zouzy
 * @date 2025-04-10
 */
@RestController
@RequestMapping("/jianwu/product")
public class ProductController extends BaseController
{
    @Autowired
    private IProductService productService;

    /**
     * 查询物品信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Product product)
    {
        // 设置当前登录用户ID，确保用户只能查看自己的物品
        product.setUserId(SecurityUtils.getUserId());
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    /**
     * 导出物品信息列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, Product product)
    {
        // 设置当前登录用户ID，确保用户只能导出自己的物品
        product.setUserId(SecurityUtils.getUserId());
        List<Product> list = productService.selectProductList(product);
        ExcelUtil<Product> util = new ExcelUtil<Product>(Product.class);
        util.exportExcel(response, list, "物品信息数据");
    }

    /**
     * 获取物品信息详细信息
     */
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        Product product = productService.selectProductByProductId(productId);
        // 校验物品所属用户
        if (product != null && !SecurityUtils.getUserId().equals(product.getUserId()))
        {
            return AjaxResult.error("没有权限查看该物品");
        }
        return success(product);
    }

    /**
     * 新增物品信息
     */
    @Log(title = "物品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Product product)
    {
        // 设置当前登录用户ID
        product.setUserId(SecurityUtils.getUserId());
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 修改物品信息
     */
    @Log(title = "物品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Product product)
    {
        // 校验物品所属用户
        Product original = productService.selectProductByProductId(product.getProductId());
        if (original != null && !SecurityUtils.getUserId().equals(original.getUserId()))
        {
            return AjaxResult.error("没有权限修改该物品");
        }
        // 设置当前登录用户ID，确保不会修改物品所属用户
        product.setUserId(SecurityUtils.getUserId());
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 删除物品信息
     */
    @Log(title = "物品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        // 校验物品所属用户
        for (Long productId : productIds)
        {
            Product product = productService.selectProductByProductId(productId);
            if (product != null && !SecurityUtils.getUserId().equals(product.getUserId()))
            {
                return AjaxResult.error("没有权限删除ID为" + productId + "的物品");
            }
        }
        return toAjax(productService.deleteProductByProductIds(productIds));
    }
}
