// 导入路由创建的相关方法
import { createRouter, createWebHashHistory,createWebHistory } from 'vue-router'



const routes = [
  {
    path: '/',
    redirect: '/user/queryAllUserInfo'  // 重定向到 /user/queryAllUserInfo 路由
  },
  {
    path: '/user/queryAllUserInfo',
    name: 'ShowAllUserInfo',
    component: () => import('../components/ShowAllUserInfo.vue') // 动态导入
  }
];



// 创建路由对象，声明路由规则
const router = createRouter({
  history: createWebHistory(),
  // history: createWebHashHistory(),
  routes
})

// 设置路由的全局前置守卫
router.beforeEach((to, from, next) => {
  /* 
  to 要去哪里
  from 从哪里来
  next 放行路由时需要调用的方法，不调用则不放行
  */
  console.log(to)
  next() // 直接放行所有路由
})

// 设置路由的全局后置守卫
router.afterEach((to, from) => {
  console.log(`从哪里来:${from.path}, 到哪里去:${to.path}`)
})

// 对外暴露路由对象
export default router
