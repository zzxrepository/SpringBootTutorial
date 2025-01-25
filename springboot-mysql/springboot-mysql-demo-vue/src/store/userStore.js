// 从 Pinia 中导入 defineStore 函数，用于创建一个新的 Store
import { defineStore } from 'pinia';

// 导入自定义的 Axios 实例，用于发起 HTTP 请求
import axios from '../services/axios';

// 使用 defineStore 创建一个名为 'userStore' 的 Store
export const useUserStore = defineStore('userStore', {
  // 定义 Store 的状态
  state: () => ({
    users: [],  // 用于存储多个用户的信息，初始为空数组
  }),

  // 定义 Store 的动作（方法）
  actions: {
    /**
     * 异步方法：获取所有用户信息
     * 该方法使用 Axios 实例发送 GET 请求到后端 API，
     * 并将返回的用户数据存储在 state 中的 users 数组里
     */
    async fetchUserInfo() {
      try {
        // 使用自定义的 Axios 实例发送 GET 请求到 '/user/queryAllUserInfo' 端点
        const response = await axios.get('/user/queryAllUserInfo');

        // 检查响应数据中的 code 是否为 200，表示请求成功
        if (response.data.code === 200) {
          // 假设返回的数据结构为 { code: 200, message: '成功', content: [...] }
          // 将返回的用户数组赋值给 state 中的 users
          this.users = response.data.content;
        } else {
          // 如果 code 不是 200，表示请求成功但业务逻辑失败
          // 打印错误信息到控制台，方便调试
          console.error('获取用户信息失败', response.data.message);
        }
      } catch (error) {
        // 捕捉并处理请求过程中发生的任何错误
        // 例如网络问题、服务器错误等
        console.error('请求失败', error);
      }
    },
  },
});
