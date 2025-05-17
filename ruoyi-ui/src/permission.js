import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import { isMobile, isMobileView } from '@/utils/device'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/m-login', '/register']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    // 判断是否为移动端访问
    const isMobileDevice = isMobile() || isMobileView();
    /* has token*/
    if (to.path === '/login' || to.path === '/m-login') {
      next({ path: isMobileDevice ? '/m-home' : '/' })
      NProgress.done()
    } else {
      // 如果是移动端访问但路径不是/m开头的，重定向到移动端主页
      if (isMobileDevice && !to.path.startsWith('/m')) {
        next({ path: '/m-home' });
        NProgress.done();
        return;
      }
      
      // 正常访问逻辑
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        next()
      }
    }
  } else {
    /* has no token*/
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      // 其他没有访问权限的页面需要先登录
      // 判断是否为移动设备或窄屏幕，是则跳转到移动版登录页
      if (isMobile() || isMobileView()) {
        next(`/m-login?redirect=${encodeURIComponent(to.fullPath)}`)
      } else {
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      }
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
