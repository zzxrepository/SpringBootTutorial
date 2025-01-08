// 从 Pinia 中导入 createPinia 函数，用于创建 Pinia 实例
import { createPinia } from 'pinia';

// 使用 createPinia 函数创建一个 Pinia 实例
// Pinia 是 Vue 3 官方推荐的状态管理工具，用于管理应用中的全局状态
let pinia = createPinia();

// 导出创建好的 Pinia 实例
// 在 main.js 中引入并注册到 Vue 应用中，使得所有组件都可以使用 Pinia 管理状态
export default pinia;
