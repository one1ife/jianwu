<template>
  <div class="m-category">
    <div class="header">
      <div class="back" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h1>分类管理</h1>
      <div class="add" @click="handleAdd">
        <i class="el-icon-plus"></i>
      </div>
    </div>
    
    <div class="category-list" v-loading="loading">
      <div class="empty-tip" v-if="categoryList.length === 0">
        <i class="el-icon-folder"></i>
        <p>暂无分类，点击右上角添加</p>
      </div>
      
      <div v-else class="category-items">
        <div 
          v-for="(item, index) in categoryList" 
          :key="item.categoryId" 
          class="category-item"
          :class="{ 'disabled': item.status === '1' }"
        >
          <div class="item-icon">
            <i :class="item.icon || 'el-icon-folder'"></i>
          </div>
          <div class="item-content">
            <div class="item-name">{{ item.categoryName }}</div>
            <div class="item-count" v-if="categoryCounts[item.categoryId] !== undefined">
              {{ categoryCounts[item.categoryId] }} 个物品
            </div>
          </div>
          <div class="item-actions">
            <i class="el-icon-edit" @click.stop="handleUpdate(item)"></i>
            <i class="el-icon-delete" @click.stop="handleDelete(item)"></i>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑分类的对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="90%" :show-close="false" custom-class="m-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" controls-position="right" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-popover
            placement="bottom"
            width="400"
            trigger="click"
            popper-class="icon-selector-popper"
          >
            <icon-select @selected="selected" />
            <el-input slot="reference" v-model="form.icon" placeholder="点击选择图标" readonly>
              <template slot="prepend">
                <i v-if="form.icon" :class="form.icon"></i>
                <i v-else class="el-icon-picture-outline"></i>
              </template>
            </el-input>
          </el-popover>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategoryByUser, getCategory, addCategory, updateCategory, delCategory, getCategoryProductCount } from "@/api/jianwu/category";
import { getUserProfile } from "@/api/system/user";
import IconSelect from "@/components/IconSelect";

export default {
  name: "MCategory",
  components: { IconSelect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 物品分类表格数据
      categoryList: [],
      // 物品数量统计
      categoryCounts: {},
      // 用户ID
      userId: null,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        categoryName: [
          { required: true, message: "分类名称不能为空", trigger: "blur" }
        ],
        orderNum: [
          { required: true, message: "显示顺序不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    getUserInfo() {
      getUserProfile().then(response => {
        this.userId = response.data.userId;
        this.getList();
        this.getCategoryCounts();
      });
    },
    /** 查询分类列表 */
    getList() {
      this.loading = true;
      listCategoryByUser(this.userId).then(response => {
        this.categoryList = response.data || [];
        this.loading = false;
      });
    },
    /** 获取分类物品数量 */
    getCategoryCounts() {
      getCategoryProductCount(this.userId).then(response => {
        this.categoryCounts = response.data || {};
      });
    },
    /** 图标选择 */
    selected(name) {
      this.form.icon = name;
    },
    /** 返回设置页面 */
    goBack() {
      this.$router.push('/m-settings');
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        categoryId: null,
        categoryName: null,
        userId: this.userId,
        orderNum: 0,
        icon: null,
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加分类";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const categoryId = row.categoryId;
      getCategory(categoryId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "编辑分类";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.categoryId != null) {
            updateCategory(this.form).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
              this.getCategoryCounts();
            });
          } else {
            addCategory(this.form).then(response => {
              this.$message.success("新增成功");
              this.open = false;
              this.getList();
              this.getCategoryCounts();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const categoryId = row.categoryId;
      this.$confirm('确定删除该分类吗？删除后该分类下的物品将无法归类。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delCategory(categoryId).then(() => {
          this.getList();
          this.getCategoryCounts();
          this.$message.success("删除成功");
        }).catch((error) => {
          console.error("删除失败:", error);
          this.$message.error("删除失败，请稍后重试");
        });
      }).catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.m-category {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.header {
  background-color: #409EFF;
  color: white;
  padding: 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  h1 {
    margin: 0;
    font-size: 18px;
    font-weight: 500;
  }
  
  .back, .add {
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    
    i {
      font-size: 20px;
    }
  }
}

.category-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  height: 200px;
  
  i {
    font-size: 48px;
    margin-bottom: 15px;
  }
  
  p {
    font-size: 15px;
  }
}

.category-items {
  .category-item {
    background-color: white;
    border-radius: 10px;
    margin-bottom: 10px;
    padding: 15px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    
    &.disabled {
      opacity: 0.6;
    }
    
    .item-icon {
      width: 40px;
      height: 40px;
      background-color: #f0f9ff;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      
      i {
        font-size: 22px;
        color: #409EFF;
      }
    }
    
    .item-content {
      flex: 1;
      
      .item-name {
        font-size: 16px;
        font-weight: 500;
        color: #333;
        margin-bottom: 5px;
      }
      
      .item-count {
        font-size: 13px;
        color: #909399;
      }
    }
    
    .item-actions {
      display: flex;
      
      i {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        color: #606266;
        
        &.el-icon-edit {
          margin-right: 5px;
        }
        
        &.el-icon-delete {
          color: #F56C6C;
        }
      }
    }
  }
}

.m-dialog {
  border-radius: 10px;
  
  .el-dialog__header {
    padding: 15px;
    text-align: center;
    border-bottom: 1px solid #ebeef5;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 500;
    }
  }
  
  .el-dialog__body {
    padding: 15px;
  }
}

.dialog-footer {
  display: flex;
  
  .el-button {
    flex: 1;
  }
}

.icon-selector-popper {
  max-height: 300px;
  overflow-y: auto;
}
</style> 