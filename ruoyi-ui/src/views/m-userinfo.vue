<template>
  <div class="m-userinfo">
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h1>个人信息</h1>
    </div>
    
    <div class="user-avatar">
      <div class="avatar-wrapper" @click="uploadAvatar">
        <img :src="avatarUrl" alt="头像" :key="avatarRefreshKey">
        <div class="avatar-overlay">
          <i class="el-icon-camera"></i>
          <span>点击修改头像</span>
        </div>
      </div>
    </div>
    
    <div class="info-card">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="form.nickName" maxlength="30" placeholder="请输入用户昵称" />
        </el-form-item> 
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="form.phonenumber" maxlength="11" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" maxlength="50" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 额外信息显示 -->
        <div class="extra-info">
          <div class="info-item">
            <span class="info-label">用户名称</span>
            <span class="info-value">{{ user.userName }}</span>
          </div>
          <div class="info-item" v-if="user.dept">
            <span class="info-label">所属部门</span>
            <span class="info-value">{{ user.dept.deptName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">创建日期</span>
            <span class="info-value">{{ user.createTime }}</span>
          </div>
        </div>
        
        <el-form-item class="submit-btn-container">
          <el-button type="primary" @click="submit" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 头像上传弹窗 -->
    <el-dialog :visible.sync="avatarDialogVisible" title="修改头像" append-to-body width="90%" custom-class="avatar-dialog" @opened="initCropper">
      <div v-if="cropperVisible">
        <div class="avatar-upload-container">
          <div class="cropper-container">
            <vue-cropper
              ref="cropper"
              :img="options.img"
              :info="true"
              :autoCrop="options.autoCrop"
              :autoCropWidth="options.autoCropWidth"
              :autoCropHeight="options.autoCropHeight"
              :fixedBox="options.fixedBox"
              :outputType="options.outputType"
              @realTime="realTime"
            />
          </div>
          <div class="preview-container">
            <div class="preview-box">
              <img :src="previews.url" :style="previews.img" />
            </div>
          </div>
        </div>
        
        <div class="avatar-controls">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <el-button size="small" type="primary">选择图片</el-button>
          </el-upload>
          
          <div class="control-btns">
            <el-button icon="el-icon-zoom-in" circle @click="changeScale(1)"></el-button>
            <el-button icon="el-icon-zoom-out" circle @click="changeScale(-1)"></el-button>
            <el-button icon="el-icon-refresh-left" circle @click="rotateLeft()"></el-button>
            <el-button icon="el-icon-refresh-right" circle @click="rotateRight()"></el-button>
          </div>
          
          <el-button type="primary" @click="uploadImg" :loading="uploadLoading">上传头像</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserProfile, updateUserProfile, uploadAvatar } from "@/api/system/user";
import { VueCropper } from "vue-cropper";
import defaultAvatar from "@/assets/images/profile.jpg";
import store from "@/store";

export default {
  name: 'MUserInfo',
  components: { VueCropper },
  data() {
    return {
      user: {},
      form: {
        nickName: '',
        phonenumber: '',
        email: '',
        sex: '0'
      },
      rules: {
        nickName: [
          { required: true, message: "用户昵称不能为空", trigger: "blur" }
        ],
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          {
            pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      },
      defaultAvatar,
      loading: false,
      avatarRefreshKey: 0, // 用于强制刷新头像
      
      // 头像相关
      avatarDialogVisible: false,
      cropperVisible: false,
      uploadLoading: false,
      options: {
        img: '',  //裁剪图片的地址
        autoCrop: true,             // 是否默认生成截图框
        autoCropWidth: 200,         // 默认生成截图框宽度
        autoCropHeight: 200,        // 默认生成截图框高度
        fixedBox: true,             // 固定截图框大小 不允许改变
        outputType: "png",          // 默认生成截图为PNG格式
        filename: 'avatar'          // 文件名称
      },
      previews: {}
    }
  },
  computed: {
    // 计算属性，处理头像URL
    avatarUrl() {
      if (this.user.avatar) {
        if (this.user.avatar.startsWith('http')) {
          return this.user.avatar;
        } else {
          return process.env.VUE_APP_BASE_API + this.user.avatar;
        }
      } else {
        return this.defaultAvatar;
      }
    }
  },
  created() {
    this.getUser();
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data || {};
        this.form = {
          nickName: this.user.nickName,
          phonenumber: this.user.phonenumber,
          email: this.user.email,
          sex: this.user.sex
        };
        
        // 设置头像URL
        if (this.user.avatar) {
          if (this.user.avatar.startsWith('http')) {
            this.options.img = this.user.avatar;
          } else {
            this.options.img = process.env.VUE_APP_BASE_API + this.user.avatar;
          }
        } else {
          this.options.img = this.defaultAvatar;
        }
        
        // 刷新头像
        this.refreshAvatar();
      });
    },
    refreshAvatar() {
      // 强制刷新头像
      this.avatarRefreshKey += 1;
    },
    goBack() {
      this.$router.go(-1);
    },
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true;
          updateUserProfile(this.form).then(response => {
            this.$message({
              message: "修改成功",
              type: "success"
            });
            this.loading = false;
            // 更新用户信息
            this.user.nickName = this.form.nickName;
            this.user.phonenumber = this.form.phonenumber;
            this.user.email = this.form.email;
            this.user.sex = this.form.sex;
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },
    
    // 头像上传相关方法
    uploadAvatar() {
      this.avatarDialogVisible = true;
    },
    initCropper() {
      this.cropperVisible = true;
      // 延迟一下以确保DOM已经渲染
      setTimeout(() => {
        if (this.$refs.cropper) {
          this.$refs.cropper.refresh();
        }
      }, 300);
    },
    realTime(data) {
      this.previews = data;
    },
    changeScale(num) {
      num = num || 1;
      if (this.$refs.cropper) {
        this.$refs.cropper.changeScale(num);
      }
    },
    rotateLeft() {
      if (this.$refs.cropper) {
        this.$refs.cropper.rotateLeft();
      }
    },
    rotateRight() {
      if (this.$refs.cropper) {
        this.$refs.cropper.rotateRight();
      }
    },
    requestUpload() {
      // 仅占位，不做任何操作
    },
    beforeUpload(file) {
      if (file.type.indexOf("image/") == -1) {
        this.$message.error("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
        return false;
      } else {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.options.img = reader.result;
          this.options.filename = file.name;
        };
      }
    },
    uploadImg() {
      this.uploadLoading = true;
      if (!this.$refs.cropper) {
        this.$message.error("上传组件未初始化，请重试");
        this.uploadLoading = false;
        return;
      }

      this.$refs.cropper.getCropBlob(data => {
        let formData = new FormData();
        formData.append("avatarfile", data, this.options.filename || "avatar.png");
        
        uploadAvatar(formData).then(response => {
          this.uploadLoading = false;
          this.avatarDialogVisible = false;
          this.cropperVisible = false;
          
          // 设置新头像
          let avatarUrl;
          if (response.imgUrl.startsWith('http')) {
            avatarUrl = response.imgUrl;
          } else {
            avatarUrl = process.env.VUE_APP_BASE_API + response.imgUrl;
          }
          
          this.options.img = avatarUrl;
          store.commit('SET_AVATAR', avatarUrl);
          
          // 更新当前用户头像 - 确保是响应式更新
          this.$set(this.user, 'avatar', response.imgUrl);
          
          // 强制刷新头像
          this.refreshAvatar();
          
          this.$message.success("修改成功");
        }).catch(error => {
          console.error("上传头像失败:", error);
          this.uploadLoading = false;
          this.$message.error("上传失败，请重试");
        });
      });
    }
  },
  beforeDestroy() {
    // 清理资源
    this.cropperVisible = false;
  }
}
</script>

<style lang="scss" scoped>
.m-userinfo {
  padding: 16px;
  min-height: 100vh;
  background-color: #f6f7fb;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  
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

.user-avatar {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  
  .avatar-wrapper {
    position: relative;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .avatar-overlay {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background-color: rgba(0, 0, 0, 0.6);
      color: white;
      padding: 8px 0;
      font-size: 12px;
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      i {
        font-size: 16px;
        margin-bottom: 2px;
      }
    }
  }
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  ::v-deep .el-form-item__label {
    font-weight: 500;
  }
  
  ::v-deep .el-input__inner {
    border-radius: 8px;
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

.extra-info {
  margin: 25px 0 15px;
  border-top: 1px solid #eee;
  padding-top: 20px;
  
  .info-item {
    margin-bottom: 15px;
    display: flex;
    justify-content: space-between;
    
    .info-label {
      color: #606266;
      font-size: 14px;
    }
    
    .info-value {
      color: #333;
      font-weight: 500;
    }
  }
}

// 头像上传相关样式
.avatar-dialog {
  ::v-deep .el-dialog__header {
    padding: 15px;
    text-align: center;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  ::v-deep .el-dialog__body {
    padding: 15px;
  }
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  
  .cropper-container {
    height: 300px;
    margin-bottom: 15px;
  }
  
  .preview-container {
    margin: 0 auto;
    text-align: center;
    
    .preview-box {
      width: 100px;
      height: 100px;
      overflow: hidden;
      border-radius: 50%;
      border: 1px solid #ddd;
      display: inline-block;
    }
  }
}

.avatar-controls {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  
  .control-btns {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
}
</style> 