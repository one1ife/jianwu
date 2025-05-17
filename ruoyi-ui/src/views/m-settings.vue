<template>
  <div class="m-settings">
    <div class="header">
      <h1>设置</h1>
    </div>
    
    <div class="user-profile">
      <div class="avatar" @click="uploadAvatar">
        <img :src="avatarUrl" alt="头像" :key="avatarRefreshKey">
        <div class="avatar-mask">
          <i class="el-icon-plus"></i>
        </div>
      </div>
      <div class="user-info">
        <h2>{{ user.nickName || user.userName || '用户名' }}</h2>
        <p>{{ user.phonenumber || user.email || '未设置联系方式' }}</p>
      </div>
    </div>
    
    <div class="settings-section">
      <div class="section-title">账户设置</div>
      <ul class="settings-list">
        <li class="settings-item" @click="editUserInfo">
          <i class="el-icon-user"></i>
          <span>个人信息</span>
          <i class="el-icon-arrow-right"></i>
        </li>
        <li class="settings-item" @click="changePassword">
          <i class="el-icon-lock"></i>
          <span>修改密码</span>
          <i class="el-icon-arrow-right"></i>
        </li>
      </ul>
    </div>
    
    <div class="settings-section">
      <div class="section-title">物品管理</div>
      <ul class="settings-list">
        <li class="settings-item" @click="goToCategory">
          <i class="el-icon-folder"></i>
          <span>分类管理</span>
          <i class="el-icon-arrow-right"></i>
        </li>
      </ul>
    </div>
    
    <div class="settings-section">
      <div class="section-title">应用设置</div>
      <ul class="settings-list">
        <li class="settings-item">
          <i class="el-icon-bell"></i>
          <span>消息通知</span>
          <el-switch v-model="appSettings.notifications"></el-switch>
        </li>
        <li class="settings-item">
          <i class="el-icon-data-analysis"></i>
          <span>成本统计</span>
          <el-select v-model="appSettings.costStatInterval" size="small" style="width: 100px;">
            <el-option label="每日" value="daily"></el-option>
            <el-option label="每周" value="weekly"></el-option>
            <el-option label="每月" value="monthly"></el-option>
          </el-select>
        </li>
      </ul>
    </div>
    
    <div class="settings-section">
      <div class="section-title">关于</div>
      <ul class="settings-list">
        <li class="settings-item">
          <i class="el-icon-info"></i>
          <span>版本信息</span>
          <span class="value">v1.0.0</span>
        </li>
        <li class="settings-item">
          <i class="el-icon-document"></i>
          <span>使用条款</span>
          <i class="el-icon-arrow-right"></i>
        </li>
        <li class="settings-item">
          <i class="el-icon-question"></i>
          <span>帮助与反馈</span>
          <i class="el-icon-arrow-right"></i>
        </li>
      </ul>
    </div>
    
    <div class="logout-btn" @click="handleLogout">
      退出登录
    </div>
  </div>
</template>

<script>
import { getUserProfile } from "@/api/system/user";
import defaultAvatar from "@/assets/images/profile.jpg";
import store from "@/store";

export default {
  name: 'MSettings',
  data() {
    return {
      defaultAvatar,
      user: {},
      appSettings: {
        notifications: true,
        costStatInterval: 'daily'
      },
      avatarRefreshKey: 0 // 用于强制刷新头像
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
      } else if (store.getters.avatar) {
        // 如果用户对象中没有头像，但store中有，则使用store中的
        return store.getters.avatar;
      } else {
        return this.defaultAvatar;
      }
    }
  },
  created() {
    this.getUser();
  },
  // 每次组件激活时刷新用户信息和头像
  activated() {
    this.getUser();
    this.refreshAvatar();
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data || {};
        this.refreshAvatar();
      }).catch(error => {
        console.error("获取用户信息失败:", error);
      });
    },
    refreshAvatar() {
      // 强制刷新头像
      this.avatarRefreshKey += 1;
    },
    uploadAvatar() {
      this.$router.push('/m-userinfo');
    },
    editUserInfo() {
      this.$router.push('/m-userinfo');
    },
    changePassword() {
      this.$router.push('/m-resetpwd');
    },
    goToCategory() {
      this.$router.push('/m-category');
    },
    handleLogout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/m-login';
        }).catch(error => {
          console.error("退出登录失败:", error);
          // 使用前端登出方式
          this.$store.dispatch('FedLogOut').then(() => {
            location.href = '/m-login';
          });
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.m-settings {
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

.user-profile {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
  
  .avatar {
    margin-right: 15px;
    position: relative;
    cursor: pointer;
    
    img {
      width: 70px;
      height: 70px;
      border-radius: 50%;
      object-fit: cover;
    }
    
    .avatar-mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background-color: rgba(0, 0, 0, 0.5);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;
      
      i {
        font-size: 24px;
      }
    }
    
    &:hover .avatar-mask {
      opacity: 1;
    }
  }
  
  .user-info {
    h2 {
      margin: 0 0 5px 0;
      font-size: 18px;
      color: #333;
    }
    
    p {
      margin: 0;
      font-size: 14px;
      color: #909399;
    }
  }
}

.settings-section {
  margin-bottom: 20px;
  
  .section-title {
    font-size: 15px;
    font-weight: 600;
    color: #909399;
    margin-bottom: 10px;
    padding-left: 5px;
  }
  
  .settings-list {
    background-color: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    padding: 0;
    margin: 0;
    list-style: none;
    
    .settings-item {
      padding: 15px 20px;
      display: flex;
      align-items: center;
      border-bottom: 1px solid #f5f5f5;
      
      &:last-child {
        border-bottom: none;
      }
      
      i:first-child {
        font-size: 18px;
        color: #409EFF;
        margin-right: 10px;
        width: 20px;
        text-align: center;
      }
      
      span {
        flex: 1;
        font-size: 15px;
        color: #333;
      }
      
      .value {
        color: #909399;
        margin-right: 5px;
        font-size: 14px;
      }
      
      .el-icon-arrow-right {
        color: #c0c4cc;
      }
    }
  }
}

.logout-btn {
  background-color: white;
  border-radius: 16px;
  padding: 15px;
  text-align: center;
  color: #f56c6c;
  font-size: 16px;
  font-weight: 500;
  margin-top: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
</style> 