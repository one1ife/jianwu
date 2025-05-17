<template>
  <div class="m-edit">
    <div class="header">
      <div class="back-button" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h1>编辑物品</h1>
    </div>
    
    <div class="edit-form" v-loading="loading">
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
import { getProduct, updateProduct } from "@/api/jianwu/product";
import { listCategoryByUser } from "@/api/jianwu/category";
import { getUserProfile } from "@/api/system/user";

export default {
  name: 'MEdit',
  data() {
    return {
      // 加载状态
      loading: false,
      // 用户信息
      userId: null,
      // 产品ID
      productId: null,
      // 分类选项
      categoryOptions: [],
      form: {
        productId: null,
        productName: '',
        categoryId: null,
        price: '',
        buyDate: '',
        remark: '',
        status: '0',
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
    this.productId = this.$route.query.id;
    if (this.productId) {
      this.getProductDetail();
      this.getUserId();
    } else {
      this.$message.error('未指定物品ID');
      this.goBack();
    }
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
        this.userId = response.data.user ? response.data.user.userId : response.data.userId;
        this.getCategories();
      });
    },
    // 获取物品详情
    getProductDetail() {
      this.loading = true;
      getProduct(this.productId).then(res => {
        this.form = res.data;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        this.$message.error('获取物品详情失败');
        this.goBack();
      });
    },
    // 获取分类列表
    getCategories() {
      listCategoryByUser(this.userId).then(response => {
        console.log('分类数据:', response);
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
    // 提交表单
    submitForm() {
      this.$refs['productForm'].validate(valid => {
        if (valid) {
          this.loading = true;
          updateProduct(this.form).then(response => {
            this.$message({
              message: '修改成功',
              type: 'success'
            });
            this.loading = false;
            this.goBack();
          }).catch(() => {
            this.loading = false;
            this.$message.error('修改失败');
          });
        }
      });
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style lang="scss" scoped>
.m-edit {
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

.edit-form {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  
  ::v-deep .el-form-item__label {
    font-weight: 500;
  }
  
  ::v-deep .el-input__inner {
    border-radius: 8px;
  }
  
  ::v-deep .el-textarea__inner {
    border-radius: 8px;
  }
  
  .submit-btn {
    width: 100%;
    margin-top: 10px;
    height: 46px;
    font-size: 16px;
    border-radius: 12px;
    background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
    border: none;
    box-shadow: 0 4px 10px rgba(64, 158, 255, 0.2);
  }
  
  .life-unit {
    position: absolute;
    right: 10px;
    top: 0;
    height: 40px;
    line-height: 40px;
    color: #909399;
  }
  
  ::v-deep .el-input-number {
    width: 100%;
  }
  
  ::v-deep .el-input-number .el-input__inner {
    text-align: left;
  }
}

.daily-cost-preview {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  text-align: center;
  
  .preview-label {
    font-size: 16px;
    color: #606266;
    margin-bottom: 10px;
  }
  
  .preview-value {
    font-size: 30px;
    font-weight: 700;
    color: #409EFF;
  }
}
</style> 