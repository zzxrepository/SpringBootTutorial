import { createApp } from 'vue';// 引入 Vue 的 createApp 方法，用于创建应用实例
import App from './App.vue';// 引入根组件
import router from './router/router';// 引入路由配置
import pinia from './store/pinia';// 引入 Pinia 状态管理

// 创建 Vue 应用实例，并通过链式调用注册插件和功能
export const app = createApp(App)
                    .use(router) // 注册路由
                    .use(pinia) // 注册 Pinia 状态管理
app.mount('#app'); // 挂载到 DOM 中的 #app 节点



/**
 * 根据上述步骤，推荐的编写顺序如下：
 *  1.初始化项目：使用 Vite 创建 Vue 项目并进入项目目录。
 *  2.安装依赖：安装 Pinia、Vue Router、Axios 等必要的依赖。
 *  3.配置 Vite：编辑 vite.config.js，设置开发服务器和代理。
 *  4.配置 Axios：在 src/services/axios.js 中创建自定义 Axios 实例，并配置拦截器。
 *  5.设置 Pinia：创建 src/pinia.js，初始化 Pinia 并在 main.js 中引入。
 *  6.创建 Store：在 src/store/userStore.js 中定义用户相关的状态和操作。
 *  7.配置 Router：在 src/router/router.js 中定义路由规则和全局守卫。
 *  8.编写主入口文件：在 src/main.js 中创建 Vue 应用实例，挂载路由和 Pinia。
 *  9.创建根组件：编写 src/App.vue，主要负责渲染路由视图。
 *  10.创建展示组件：编写 src/components/ShowAllUserInfo.vue，展示用户信息并处理加载和错误状态。
 */
