package com.ruoyi.jianwu.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物品信息对象 jianwu_product
 * 
 * @author zouzy
 * @date 2025-04-10
 */
public class Product extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 物品ID */
    private Long productId;

    /** 物品名称 */
    @Excel(name = "物品名称")
    private String productName;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer productSort;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 购买时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "购买时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date buyDate;

    /** 购买价格 */
    @Excel(name = "购买价格")
    private BigDecimal price;

    /** 图标 */
    @Excel(name = "图标")
    private String icon;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setProductSort(Integer productSort) 
    {
        this.productSort = productSort;
    }

    public Integer getProductSort() 
    {
        return productSort;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setBuyDate(Date buyDate) 
    {
        this.buyDate = buyDate;
    }

    public Date getBuyDate() 
    {
        return buyDate;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productSort", getProductSort())
            .append("userId", getUserId())
            .append("buyDate", getBuyDate())
            .append("price", getPrice())
            .append("icon", getIcon())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
