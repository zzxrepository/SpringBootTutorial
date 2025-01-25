import { createApp } from 'vue';// 引入 Vue 的 createApp 方法，用于创建应用实例
import App from './App.vue';// 引入根组件
import router from './router/router';// 引入路由配置
import pinia from './store/pinia';// 引入 Pinia 状态管理

// 创建 Vue 应用实例，并通过链式调用注册插件和功能
export const app = createApp(App)
                    .use(router) // 注册路由
                    .use(pinia) // 注册 Pinia 状态管理
app.mount('#app'); // 挂载到 DOM 中的 #app 节点