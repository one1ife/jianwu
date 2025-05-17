<template>
  <div class="mobile-container">
    <!-- 主内容区 -->
    <div class="content-container">
      <router-view />
    </div>
    
    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div 
        class="nav-item" 
        :class="{ active: activeIndex === 0 }"
        @click="switchTab(0, '/m-home')"
      >
        <i class="el-icon-s-home"></i>
        <span>首页</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ active: activeIndex === 1 }"
        @click="switchTab(1, '/m-add')"
      >
        <i class="el-icon-plus"></i>
        <span>添加</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ active: activeIndex === 2 }"
        @click="switchTab(2, '/m-settings')"
      >
        <i class="el-icon-setting"></i>
        <span>设置</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MLayout',
  data() {
    return {
      activeIndex: 0
    }
  },
  created() {
    // 根据当前路由设置激活的选项
    const path = this.$route.path;
    if (path.includes('m-home')) {
      this.activeIndex = 0;
    } else if (path.includes('m-add')) {
      this.activeIndex = 1;
    } else if (path.includes('m-settings')) {
      this.activeIndex = 2;
    }
  },
  methods: {
    switchTab(index, path) {
      this.activeIndex = index;
      this.$router.push(path);
    }
  }
}
</script>

<style lang="scss" scoped>
.mobile-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f6f7fb;
}

.content-container {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 60px; // 为底部导航留出空间
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background-color: #ffffff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
  width: 33.33%;
  color: #909399;
  transition: all 0.3s;
  
  i {
    font-size: 22px;
    margin-bottom: 3px;
  }
  
  span {
    font-size: 12px;
  }
  
  &.active {
    color: #409EFF;
    font-weight: 500;
  }
  
  &:active {
    opacity: 0.7;
  }
}
</style> 