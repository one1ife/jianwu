<template>
  <div class="m-add">
    <div class="header">
      <h1>添加物品</h1>
    </div>
    
    <div class="add-form">
      <el-form ref="productForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="物品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入物品名称" />
        </el-form-item>
        
        <el-form-item label="物品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择物品分类" style="width: 100%;" clearable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="购买价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入购买价格">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="购买日期" prop="buyDate">
          <el-date-picker
            v-model="form.buyDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            type="textarea"
            v-model="form.remark"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
            rows="3"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="submit-btn" @click="submitForm">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="daily-cost-preview" v-if="form.price && form.buyDate">
      <div class="preview-label">每日成本预览</div>
      <div class="preview-value">¥{{ getDailyCost }}</div>
    </div>
  </div>
</template>

<script>
import { addProduct } from "@/api/jianwu/product";
import { listCategoryByUser } from "@/api/jianwu/category";
import { getToken } from "@/utils/auth";
import { getUserProfile } from "@/api/system/user";

export default {
  name: 'MAdd',
  data() {
    return {
      // 用户信息
      userId: null,
      // 分类选项
      categoryOptions: [],
      form: {
        productName: '',
        categoryId: null,
        price: '',
        buyDate: new Date().toISOString().split('T')[0], // 今天
        remark: '',
        status: '0', // 默认正常
        userId: null
      },
      rules: {
        productName: [
          { required: true, message: '请输入物品名称', trigger: 'blur' }
        ],
        categoryId: [],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' },
          { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的金额', trigger: 'blur' }
        ],
        buyDate: [
          { required: true, message: '请选择购买日期', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getUserId();
  },
  computed: {
    getDailyCost() {
      if (!this.form.price) return '0.00';
      
      const price = parseFloat(this.form.price || 0);
      
      // 按购买日到今天的天数计算
      if (this.form.buyDate) {
        const buyDate = new Date(this.form.buyDate);
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
  methods: {
    // 获取当前用户ID
    getUserId() {
      getUserProfile().then(response => {
        console.log('用户信息:', response);
        if (response.data) {
          if (response.data.user) {
            this.userId = response.data.user.userId;
          } else if (response.data.userId) {
            this.userId = response.data.userId;
          }
        }
        this.form.userId = this.userId;
        this.getCategories();
      }).catch(error => {
        console.error('获取用户信息失败:', error);
      });
    },
    // 获取分类列表
    getCategories() {
      listCategoryByUser(this.userId).then(response => {
        console.log('添加页面-分类数据:', response);
        if (Array.isArray(response)) {
          this.categoryOptions = response;
        } else if (response.data && Array.isArray(response.data)) {
          this.categoryOptions = response.data;
        } else if (response.rows && Array.isArray(response.rows)) {
          this.categoryOptions = response.rows;
        } else {
          this.categoryOptions = [];
          console.error('未能识别分类数据格式');
        }
      }).catch(error => {
        console.error('获取分类失败:', error);
        this.categoryOptions = [];
      });
    },
    submitForm() {
      this.$refs['productForm'].validate(valid => {
        if (valid) {
          const productData = {
            ...this.form,
            productSort: 1 // 默认排序值
          };
          
          addProduct(productData).then(response => {
            this.$message({
              message: '添加成功',
              type: 'success'
            });
            this.resetForm();
          });
        }
      });
    },
    resetForm() {
      this.form = {
        productName: '',
        categoryId: null,
        price: '',
        buyDate: new Date().toISOString().split('T')[0],
        remark: '',
        status: '0',
        userId: this.userId
      };
    }
  }
}
</script>

<style lang="scss" scoped>
.m-add {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  padding: 20px 0;
  display: flex;
  align-items: center;
  
  .back-button {
    font-size: 20px;
    margin-right: 10px;
    color: #333;
    cursor: pointer;
  }
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
    color: #333;
  }
}

.add-form {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  
  ::v-deep .el-form-item {
    margin-bottom: 20px;
  }
  
  ::v-deep .el-form-item__label {
    font-weight: 500;
    font-size: 15px;
    padding-bottom: 8px;
    color: #333;
  }
  
  ::v-deep .el-input__inner {
    border-radius: 10px;
    height: 46px;
    border: 1px solid #e0e5eb;
    transition: all 0.3s;
    
    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
  
  ::v-deep .el-textarea__inner {
    border-radius: 10px;
    border: 1px solid #e0e5eb;
    transition: all 0.3s;
    
    &:focus {
      border-color: #409EFF;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
  
  .price-input {
    ::v-deep .el-input-group__prepend {
      background-color: #409EFF;
      color: white;
      border-top-left-radius: 10px;
      border-bottom-left-radius: 10px;
      border: none;
      width: 40px;
      font-weight: bold;
    }
  }
  
  .form-actions {
    display: flex;
    gap: 12px;
    margin-top: 24px;
    
    .cancel-btn {
      flex: 1;
      height: 46px;
      font-size: 16px;
      border-radius: 12px;
      border: 1px solid #dcdfe6;
    }
    
    .submit-btn {
      flex: 2;
      height: 46px;
      font-size: 16px;
      border-radius: 12px;
      background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
      border: none;
      box-shadow: 0 4px 10px rgba(64, 158, 255, 0.2);
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 12px rgba(64, 158, 255, 0.3);
      }
      
      &:active {
        transform: translateY(0);
      }
    }
  }
}

.daily-cost-preview {
  margin-top: 24px;
  
  .preview-card {
    background-color: white;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    
    .preview-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      
      i {
        font-size: 24px;
        color: white;
      }
    }
    
    .preview-content {
      flex: 1;
    }
    
    .preview-label {
      font-size: 15px;
      color: #606266;
      margin-bottom: 6px;
    }
    
    .preview-value {
      font-size: 28px;
      font-weight: 700;
      color: #409EFF;
      background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
}
</style>