<template>
  <div class="m-resetpwd">
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h1>修改密码</h1>
    </div>
    
    <div class="pwd-card">
      <el-form ref="form" :model="user" :rules="rules" label-width="90px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="user.oldPassword" 
            placeholder="请输入旧密码" 
            type="password" 
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="user.newPassword" 
            placeholder="请输入新密码" 
            type="password" 
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="user.confirmPassword" 
            placeholder="请确认新密码" 
            type="password" 
            show-password
          />
        </el-form-item>
        
        <div class="password-tips">
          <p><i class="el-icon-info"></i> 密码长度必须在6-20个字符之间</p>
          <p><i class="el-icon-info"></i> 不能包含非法字符（如: &lt; &gt; " ' \ |）</p>
        </div>
        
        <el-form-item class="submit-btn-container">
          <el-button type="primary" @click="submit" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { updateUserPwd } from "@/api/system/user";

export default {
  name: 'MResetPwd',
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.user.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      user: {
        oldPassword: undefined,
        newPassword: undefined,
        confirmPassword: undefined
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: "旧密码不能为空", trigger: "blur" }
        ],
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\ |", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ]
      },
      loading: false
    };
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true;
          updateUserPwd(this.user.oldPassword, this.user.newPassword).then(response => {
            this.$message({
              message: "修改成功，请重新登录",
              type: "success"
            });
            this.loading = false;
            
            // 密码修改成功后，提示用户重新登录
            setTimeout(() => {
              this.$store.dispatch('LogOut').then(() => {
                location.href = '/m-login';
              });
            }, 1500);
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.m-resetpwd {
  padding: 16px;
  min-height: 100vh;
  background-color: #f6f7fb;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  
  .back-btn {
    margin-right: 15px;
    font-size: 20px;
    color: #333;
    cursor: pointer;
    
    &:active {
      opacity: 0.7;
    }
  }
  
  h1 {
    font-size: 24px;
    margin: 0;
    font-weight: 600;
  }
}

.pwd-card {
  background: white;
  border-radius: 16px;
  padding: 25px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  ::v-deep .el-form-item__label {
    font-weight: 500;
  }
  
  ::v-deep .el-input__inner {
    border-radius: 8px;
    height: 46px;
    line-height: 46px;
  }
  
  .password-tips {
    padding: 10px 0;
    margin: 15px 0;
    border-top: 1px dashed #eee;
    border-bottom: 1px dashed #eee;
    
    p {
      margin: 8px 0;
      color: #909399;
      font-size: 13px;
      
      i {
        color: #E6A23C;
        margin-right: 5px;
      }
    }
  }
  
  .submit-btn-container {
    margin-top: 30px;
    margin-bottom: 0;
    
    .el-button {
      width: 100%;
      padding: 12px 0;
      font-size: 16px;
      border-radius: 8px;
    }
  }
}
</style> 