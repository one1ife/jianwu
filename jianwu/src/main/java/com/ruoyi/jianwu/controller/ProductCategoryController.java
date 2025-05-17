package com.ruoyi.jianwu.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.jianwu.domain.ProductCategory;
import com.ruoyi.jianwu.service.IProductCategoryService;
import com.ruoyi.jianwu.service.IProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 物品分类Controller
 * 
 * @author ruoyi
 * @date 2023-05-20
 */
@RestController
@RequestMapping("/jianwu/category")
public class ProductCategoryController extends BaseController
{
    @Autowired
    private IProductCategoryService productCategoryService;
    
    @Autowired
    private IProductService productService;

    /**
     * 查询物品分类列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ProductCategory productCategory)
    {
        startPage();
        List<ProductCategory> list = productCategoryService.selectProductCategoryList(productCategory);
        return getDataTable(list);
    }
    
    /**
     * 查询指定用户的物品分类列表
     */
    @GetMapping("/user/{userId}")
    public AjaxResult listByUser(@PathVariable("userId") Long userId)
    {
        List<ProductCategory> list = productCategoryService.selectProductCategoryByUserId(userId);
        return success(list);
    }
    
    /**
     * 获取分类下的物品数量统计
     */
    @GetMapping("/counts/{userId}")
    public AjaxResult getCategoryCounts(@PathVariable("userId") Long userId)
    {
        List<ProductCategory> categories = productCategoryService.selectProductCategoryByUserId(userId);
        Map<Long, Integer> countMap = new HashMap<>();
        
        for (ProductCategory category : categories)
        {
            int count = productService.countProductByCategoryId(category.getCategoryId(), userId);
            countMap.put(category.getCategoryId(), count);
        }
        
        return success(countMap);
    }

    /**
     * 导出物品分类列表
     */
    @PreAuthorize("@ss.hasPermi('jianwu:category:export')")
    @Log(title = "物品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductCategory productCategory)
    {
        List<ProductCategory> list = productCategoryService.selectProductCategoryList(productCategory);
        ExcelUtil<ProductCategory> util = new ExcelUtil<ProductCategory>(ProductCategory.class);
        util.exportExcel(response, list, "物品分类数据");
    }

    /**
     * 获取物品分类详细信息
     */
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(productCategoryService.selectProductCategoryByCategoryId(categoryId));
    }

    /**
     * 新增物品分类
     */
    @Log(title = "物品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductCategory productCategory)
    {
        return toAjax(productCategoryService.insertProductCategory(productCategory));
    }

    /**
     * 修改物品分类
     */
    @Log(title = "物品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductCategory productCategory)
    {
        return toAjax(productCategoryService.updateProductCategory(productCategory));
    }

    /**
     * 删除物品分类
     */
    @Log(title = "物品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(productCategoryService.deleteProductCategoryByCategoryIds(categoryIds));
    }
} 