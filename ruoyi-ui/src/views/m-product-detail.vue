<template>
  <div class="m-product-detail">
    <div class="header">
      <div class="back-button" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h1>物品详情</h1>
    </div>
    
    <div class="product-info-card" v-loading="loading">
      <template v-if="product">
        <div class="product-name">{{ product.productName }}</div>
        <div class="product-meta">
          <span class="meta-category">{{ categoryName }}</span>
          <span class="meta-date">{{ product.buyDate }}</span>
        </div>
        
        <div class="price-info">
          <div class="price-item">
            <div class="price-label">价格</div>
            <div class="price-value">¥{{ product.price }}</div>
          </div>
          <div class="price-item">
            <div class="price-label">购买日期</div>
            <div class="price-value">{{ product.buyDate }}</div>
          </div>
          <div class="price-item">
            <div class="price-label">每日成本</div>
            <div class="price-value">¥{{ dailyCost }}/天</div>
          </div>
        </div>
        
        <div class="detail-section">
          <div class="section-title">备注</div>
          <div class="section-content">
            {{ product.remark || '未填写备注' }}
          </div>
        </div>
      </template>
    </div>
    
    <div class="action-buttons" v-if="product">
      <el-button type="primary" icon="el-icon-edit" @click="editProduct">编辑</el-button>
      <el-button type="danger" icon="el-icon-delete" @click="confirmDelete">删除</el-button>
    </div>
  </div>
</template>

<script>
import { getProduct, delProduct } from "@/api/jianwu/product";
import { getCategory } from "@/api/jianwu/category";

export default {
  name: 'MProductDetail',
  data() {
    return {
      loading: false,
      productId: null,
      product: null,
      categoryName: '无分类'
    }
  },
  computed: {
    dailyCost() {
      if (!this.product) return '0.00';
      
      const price = parseFloat(this.product.price || 0);
      
      // 按购买日到今天的天数计算
      if (this.product.buyDate) {
        const buyDate = new Date(this.product.buyDate);
        const today = new Date();
        
        // 计算购买日到今天的天数
        const diffTime = Math.abs(today - buyDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) || 1; // 至少1天
        
        return (price / diffDays).toFixed(2);
      }
      
      // 默认按100天计算
      return (price / 100).toFixed(2);
    }
  },
  created() {
    this.productId = this.$route.query.id;
    if (this.productId) {
      this.getProductDetail();
    }
  },
  methods: {
    getProductDetail() {
      this.loading = true;
      getProduct(this.productId).then(res => {
        this.product = res.data;
        
        // 获取分类名称
        if (this.product.categoryId) {
          this.getCategoryName(this.product.categoryId);
        }
        
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        this.$message.error('获取物品详情失败');
      });
    },
    getCategoryName(categoryId) {
      getCategory(categoryId).then(res => {
        if (res.data && res.data.categoryName) {
          this.categoryName = res.data.categoryName;
        }
      });
    },
    goBack() {
      this.$router.go(-1);
    },
    editProduct() {
      this.$router.push({
        path: '/m-edit',
        query: { id: this.productId }
      });
    },
    confirmDelete() {
      this.$confirm('确认删除该物品？此操作不可恢复', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteProduct();
      });
    },
    deleteProduct() {
      delProduct(this.productId).then(() => {
        this.$message.success('删除成功');
        this.$router.push('/m-home');
      }).catch(() => {
        this.$message.error('删除失败');
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.m-product-detail {
  padding: 16px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  
  .back-button {
    margin-right: 15px;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background-color: #f2f6fc;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    
    i {
      font-size: 16px;
      color: #606266;
    }
  }
  
  h1 {
    font-size: 20px;
    margin: 0;
    flex-grow: 1;
    text-align: center;
    padding-right: 30px;
  }
}

.product-info-card {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  
  .product-name {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .product-meta {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    
    .meta-category {
      background-color: #ecf5ff;
      color: #409eff;
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 12px;
      margin-right: 10px;
    }
    
    .meta-date {
      color: #909399;
      font-size: 13px;
    }
  }
  
  .price-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 25px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    
    .price-item {
      flex-grow: 1;
      text-align: center;
      padding: 15px 10px;
      
      &:nth-child(1) {
        background-color: #f0f9eb;
      }
      
      &:nth-child(2) {
        background-color: #f4f4f5;
      }
      
      &:nth-child(3) {
        background-color: #ecf5ff;
      }
      
      .price-label {
        font-size: 12px;
        color: #909399;
        margin-bottom: 5px;
      }
      
      .price-value {
        font-size: 16px;
        font-weight: 500;
        color: #333;
      }
    }
  }
  
  .detail-section {
    margin-bottom: 20px;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .section-title {
      font-size: 15px;
      font-weight: 500;
      color: #606266;
      margin-bottom: 8px;
    }
    
    .section-content {
      color: #606266;
      font-size: 14px;
      line-height: 1.5;
      word-break: break-all;
      
      .buy-link {
        color: #409eff;
        text-decoration: none;
      }
      
      .empty-info {
        color: #c0c4cc;
      }
    }
  }
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  
  .el-button {
    width: 48%;
  }
}
</style> 