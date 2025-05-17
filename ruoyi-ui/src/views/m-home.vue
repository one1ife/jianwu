<template>
  <div class="m-home">
    <div class="header">
      <h1>简物</h1>
    </div>
    
    
    <div class="daily-cost-card">
      <div class="card-title">我的物品</div>
      <div class="cost-value">{{ totalItems || 0 }}</div>
      
      <div class="additional-info">
        <div class="info-item">
          <span class="info-label">资产</span>
          <span class="info-value">¥{{ totalAssets }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">日均</span>
          <span class="info-value">¥{{ totalDailyCost }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">降低</span>
          <span class="info-value">¥{{ costReduction }}</span>
        </div>
      </div>
    </div>
    
    <div class="section-title">
      <h2>我的物品</h2>
      <div class="category-select" @click="showCategorySelect">
        <span class="current-category">{{ currentCategory.label }}</span>
        <i class="el-icon-arrow-down"></i>
      </div>
    </div>
    
    <!-- 分类选择弹窗 -->
    <el-dialog
      title="选择分类"
      :visible.sync="categoryDialogVisible"
      width="80%"
      custom-class="category-dialog"
    >
      <div class="category-list">
        <div 
          v-for="category in categories" 
          :key="category.categoryId || category.value"
          class="category-item"
          :class="{ active: currentCategory.categoryId === category.categoryId }"
          @click="selectCategory(category)"
        >
          {{ category.categoryName || category.label }}
        </div>
      </div>
    </el-dialog>
    
    <div class="item-list" v-loading="loading">
      <div v-if="recentItems.length === 0" class="empty-state">
        <i class="el-icon-box"></i>
        <p>暂无物品记录</p>
        <el-button size="small" type="primary" @click="goToAdd">添加物品</el-button>
      </div>
      <div 
        class="item-card" 
        v-for="(item, index) in recentItems" 
        :key="index"
        @click="viewItemDetail(item)"
      >
        <div class="item-info">
          <h3>{{ item.productName }}</h3>
          <p class="item-date">{{ item.buyDate }}</p>
        </div>
        <div class="item-price">
          <span class="price-value">¥{{ item.price }}</span>
          <span class="daily-cost">¥{{ item.dailyCost }}/天</span>
        </div>
      </div>
    </div>
    
    <div class="section-title">
      <h2>成本分析</h2>
    </div>
    
    <div class="analysis-card">
      <div class="analysis-chart">
        <!-- 图表占位 -->
        <div class="chart-placeholder">
          <i class="el-icon-data-line"></i>
          <p>成本走势图</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listProduct } from "@/api/jianwu/product";
import { listCategoryByUser, getCategoryProductCount } from "@/api/jianwu/category";
import { getUserProfile } from "@/api/system/user";

export default {
  name: 'MHome',
  data() {
    return {
      loading: false,
      recentItems: [],
      totalItems: 0,
      totalAssets: 0,
      totalDailyCost: 0,
      dailyExpense: 0,
      costReduction: 0, // 新增：成本降低值
      yesterdayDailyCost: 0, // 新增：昨日日均成本
      currentCategory: { label: '全部', value: 'all', categoryId: null },
      categoryDialogVisible: false,
      categories: [
        { label: '全部', value: 'all', categoryId: null }
      ],
      userId: null,
      categoryCounts: {}
    }
  },
  created() {
    this.getCurrentUser();
  },
  methods: {
    // 获取当前用户信息
    getCurrentUser() {
      getUserProfile().then(res => {
        this.userId = res.data.userId;
        this.getUserCategories();
        this.getRecentItems();
      });
    },
    
    // 获取用户分类
    getUserCategories() {
      if (!this.userId) return;
      
      listCategoryByUser(this.userId).then(res => {
        // 添加"全部"选项
        this.categories = [{ label: '全部', value: 'all', categoryId: null }];
        
        console.log('首页-分类数据:', res);
        // 处理不同的响应格式
        let categoriesData = [];
        if (Array.isArray(res)) {
          categoriesData = res;
        } else if (res && res.data && Array.isArray(res.data)) {
          categoriesData = res.data;
        } else if (res && res.rows && Array.isArray(res.rows)) {
          categoriesData = res.rows;
        }
        
        // 将后端分类数据添加到列表
        categoriesData.forEach(category => {
          this.categories.push({
            label: category.categoryName,
            value: category.categoryId,
            categoryId: category.categoryId
          });
        });
        
        // 获取各分类下的物品数量
        this.getCategoryCounts();
      }).catch(error => {
        console.error('获取分类失败:', error);
      });
    },
    
    // 获取各分类下的物品数量
    getCategoryCounts() {
      if (!this.userId) return;
      
      getCategoryProductCount(this.userId).then(res => {
        if (res) {
          this.categoryCounts = res;
        }
      });
    },
    
    // 获取物品列表
    getRecentItems() {
      this.loading = true;
      
      // 构建查询参数
      const query = {
        pageSize: 10, 
        pageNum: 1,
        orderByColumn: 'create_time',
        isAsc: 'desc'
      };
      
      // 如果选择了特定分类，添加分类过滤
      if (this.currentCategory.categoryId) {
        query.categoryId = this.currentCategory.categoryId;
      }
      
      // 先获取总数
      listProduct(query).then(response => {
        this.totalItems = response.total || 0;
        this.recentItems = response.rows || [];
        
        // 计算每日成本
        this.recentItems.forEach(item => {
          const dailyCost = this.calculateItemDailyCost(item);
          item.dailyCost = dailyCost.toFixed(2);
        });
        
        // 计算总资产和总日均成本
        this.calculateTotals();
        
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    
    // 计算总资产和总日均成本
    calculateTotals() {
      // 计算总资产
      this.totalAssets = this.recentItems.reduce((total, item) => 
        total + parseFloat(item.price || 0), 0).toFixed(2);
      
      // 计算今日总日均成本 - 所有物品的日均成本总和
      this.totalDailyCost = this.recentItems.reduce((total, item) => {
        const dailyCost = this.calculateItemDailyCost(item);
        return total + dailyCost;
      }, 0).toFixed(2);
      
      // 计算单日总开销 - 即每个物品当天的成本总和
      this.dailyExpense = this.recentItems.reduce((total, item) => {
        return total + (parseFloat(this.calculateItemDailyCost(item)));
      }, 0).toFixed(2);
      
      // 计算昨日总日均成本
      this.yesterdayDailyCost = this.recentItems.reduce((total, item) => {
        const yesterdayCost = this.calculateItemYesterdayDailyCost(item);
        return total + yesterdayCost;
      }, 0).toFixed(2);
      
      // 计算日均成本降低值 - 昨日日均成本减去今日日均成本
      this.costReduction = (parseFloat(this.yesterdayDailyCost) - parseFloat(this.totalDailyCost)).toFixed(2);
      if (this.costReduction < 0) {
        this.costReduction = '0.00'; // 如果是负值（成本上升），则显示为0
      }
    },
    
    // 计算物品的每日成本 (从购买日到今天)
    calculateItemDailyCost(item) {
      if (!item.price) return 0;
      
      const price = parseFloat(item.price || 0);
      
      // 按购买日到今天的天数计算
      if (item.buyDate) {
        const buyDate = new Date(item.buyDate);
        const today = new Date();
        
        // 计算购买日到今天的天数
        const diffTime = Math.abs(today - buyDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) || 1; // 至少1天
        
        return price / diffDays;
      }
      
      // 默认按100天计算
      return price / 100;
    },
    
    // 计算物品的昨日日均成本
    calculateItemYesterdayDailyCost(item) {
      if (!item.price) return 0;
      
      const price = parseFloat(item.price || 0);
      
      // 按购买日到昨天的天数计算
      if (item.buyDate) {
        const buyDate = new Date(item.buyDate);
        const yesterday = new Date();
        yesterday.setDate(yesterday.getDate() - 1); // 设置为昨天
        
        // 如果购买日期是今天或未来，昨天的成本为0
        if (buyDate > yesterday) return 0;
        
        // 计算购买日到昨天的天数
        const diffTime = Math.abs(yesterday - buyDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) || 1; // 至少1天
        
        return price / diffDays;
      }
      
      // 默认按99天计算（比今天少一天）
      return price / 99;
    },
    
    // 打开分类选择弹窗
    showCategorySelect() {
      this.categoryDialogVisible = true;
    },
    
    // 选择分类
    selectCategory(category) {
      this.currentCategory = category;
      this.categoryDialogVisible = false;
      // 根据分类重新加载数据
      this.getRecentItems();
    },
    
    // 跳转到添加物品页面
    goToAdd() {
      this.$router.push('/m-add');
    },
    
    // 查看物品详情
    viewItemDetail(item) {
      this.$router.push({
        path: '/m-product-detail',
        query: { id: item.productId }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.m-home {
  padding: 16px;
}

.header {
  padding: 20px 0;
  h1 {
    font-size: 28px;
    font-weight: 600;
    margin: 0;
    color: #333;
  }
}

.daily-cost-card {
  background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
  color: white;
  border-radius: 16px;
  padding: 24px 20px;
  box-shadow: 0 4px 12px rgba(76, 132, 255, 0.2);
  margin-bottom: 24px;
  
  .card-title {
    font-size: 16px;
    opacity: 0.85;
    margin-bottom: 12px;
    letter-spacing: 0.5px;
    font-weight: 500;
  }
  
  .cost-value {
    font-size: 48px;
    font-weight: 600;
    margin-bottom: 20px;
    letter-spacing: -0.5px;
  }
  
  .additional-info {
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px solid rgba(255, 255, 255, 0.15);
    
    .info-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      flex: 1;
      
      .info-label {
        font-size: 15px;
        opacity: 0.75;
        margin-bottom: 8px;
        font-weight: 400;
        letter-spacing: 0.3px;
      }
      
      .info-value {
        font-size: 20px;
        font-weight: 600;
        letter-spacing: -0.2px;
        
        &.up {
          color: #ffb1b1;
          font-weight: 500;
        }
        
        &.down {
          color: #95e395;
          font-weight: 500;
        }
      }
    }
  }
}

.empty-state {
  background-color: white;
  border-radius: 12px;
  padding: 30px 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
  
  i {
    font-size: 48px;
    color: #dcdfe6;
    margin-bottom: 10px;
  }
  
  p {
    color: #909399;
    margin-bottom: 15px;
  }
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0 15px;
  
  h2 {
    font-size: 18px;
    margin: 0;
    color: #333;
  }
  
  .category-select {
    display: flex;
    align-items: center;
    color: #409EFF;
    font-size: 14px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: all 0.3s;
    
    &:active {
      background-color: rgba(64, 158, 255, 0.1);
    }
    
    .current-category {
      margin-right: 4px;
    }
    
    i {
      font-size: 12px;
    }
  }
}

.item-list {
  margin-bottom: 20px;
}

.item-card {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  
  .item-info {
    h3 {
      margin: 0 0 5px 0;
      font-size: 16px;
      color: #333;
    }
    
    .item-date {
      margin: 0;
      font-size: 13px;
      color: #909399;
    }
  }
  
  .item-price {
    text-align: right;
    
    .price-value {
      display: block;
      font-weight: 600;
      font-size: 16px;
      color: #333;
      margin-bottom: 5px;
    }
    
    .daily-cost {
      font-size: 13px;
      color: #409EFF;
    }
  }
}

.analysis-card {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: 20px;
  
  .analysis-chart {
    height: 180px;
    
    .chart-placeholder {
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      color: #c0c4cc;
      
      i {
        font-size: 40px;
        margin-bottom: 10px;
      }
      
      p {
        margin: 0;
      }
    }
  }
}

.category-dialog {
  border-radius: 16px;
  
  ::v-deep .el-dialog__header {
    padding: 20px 20px 10px;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  ::v-deep .el-dialog__body {
    padding: 10px 20px 20px;
  }
}

.category-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  
  .category-item {
    padding: 12px;
    text-align: center;
    background-color: #f5f7fa;
    border-radius: 8px;
    font-size: 15px;
    color: #606266;
    cursor: pointer;
    transition: all 0.3s;
    
    &.active {
      background-color: #409EFF;
      color: white;
    }
    
    &:active {
      transform: scale(0.98);
    }
  }
}
</style>