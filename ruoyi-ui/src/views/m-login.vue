<template>
  <div class="mobile-login-container">
    <div class="login-card">
      <div class="login-header">
        <img src="@/assets/logo/logo.png" class="login-logo" alt="Logo">
        <h1 class="title">简物</h1>
        <p class="subtitle">记录物品，掌握每日成本</p>
      </div>
      
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username"
              type="text"
              placeholder="用户名"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item v-if="captchaEnabled" prop="code">
            <el-input
              v-model="loginForm.code"
              placeholder="验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            />
            <div class="login-code">
              <img :src="codeUrl" class="login-code-img" @click="getCode" />
            </div>
          </el-form-item>
          
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0 0 15px 0;">记住我</el-checkbox>
          
          <el-form-item style="margin-bottom:10px;">
            <el-button
              :loading="loading"
              type="primary"
              class="login-button"
              @click.native.prevent="handleLogin"
            >登录</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="login-footer">
        <p>记录你购买商品的每日平均成本</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "MLogin",
  data() {
    return {
      codeUrl: "",
      cookiePassword: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的用户名" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/m-home" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.mobile-login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f6f7fb;
  padding: 15px;
  box-sizing: border-box;
}

.login-card {
  width: 100%;
  max-width: 350px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  padding: 25px 20px;
  margin: 0 auto;
}

.login-header {
  text-align: center;
  margin-bottom: 25px;
  
  .login-logo {
    width: 70px;
    height: 70px;
    margin-bottom: 15px;
    border-radius: 0;
    padding: 0;
    background-color: transparent;
  }
  
  .title {
    font-size: 28px;
    color: #333;
    margin: 0 0 8px 0;
    font-weight: 600;
  }
  
  .subtitle {
    font-size: 15px;
    color: #777;
    margin: 0;
  }
}

.login-form {
  margin-bottom: 20px;
  
  .el-input {
    margin-bottom: 10px;
    
    ::v-deep .el-input__inner {
      height: 46px;
      border-radius: 12px;
      border: 1px solid #eaeaea;
      background-color: #f8f9fa;
      padding-left: 15px;
      font-size: 15px;
      
      &:focus {
        border-color: #409EFF;
        background-color: #fff;
      }
    }
    
    ::v-deep .el-input__prefix {
      left: 15px;
    }
  }
  
  .el-form-item {
    margin-bottom: 16px;
  }
  
  .login-button {
    width: 100%;
    height: 46px;
    font-size: 16px;
    border-radius: 12px;
    font-weight: 500;
    letter-spacing: 1px;
    background: linear-gradient(135deg, #4c84ff 0%, #409EFF 100%);
    border: none;
    box-shadow: 0 4px 10px rgba(64, 158, 255, 0.2);
    transition: all 0.3s;
    
    &:hover, &:focus {
      transform: translateY(-2px);
      box-shadow: 0 6px 15px rgba(64, 158, 255, 0.3);
    }
    
    &:active {
      transform: translateY(0);
    }
  }
  
  .login-code {
    position: absolute;
    right: 0;
    top: 0;
    height: 46px;
    width: 35%;
    
    img {
      cursor: pointer;
      height: 46px;
      border: 1px solid #eaeaea;
      border-radius: 12px;
    }
  }
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
}

// 移动设备适配
@media screen and (max-width: 576px) {
  .login-card {
    max-width: 90%;
  }
}
</style> 