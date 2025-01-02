// vite.config.js

// 从 Vite 中导入 defineConfig 工具，用于类型提示和配置验证
import { defineConfig } from 'vite';

// 从 Vite 插件库中导入 Vue 插件，以支持 Vue 单文件组件（.vue 文件）
import vue from '@vitejs/plugin-vue';

// 导出 Vite 的配置，使用 defineConfig 包装配置对象
export default defineConfig(() => {
  return {
    // 配置 Vite 使用的插件
    plugins: [
      vue()  // 启用 Vue 插件，允许 Vite 识别和处理 .vue 文件
    ],

    // 配置开发服务器相关设置
    server: {
      port: 8081,  // 设置前端开发服务器的端口号为 8081
      open: true,  // 启动开发服务器后，自动在默认浏览器中打开应用

      // 配置开发服务器的代理，用于解决前后端分离时的跨域问题
      proxy: {
        '/api': {  // 代理匹配以 /api 开头的所有请求路径
          target: 'http://localhost:8080/',  // 代理目标地址，即后端服务器的地址
          changeOrigin: true,  // 改变请求头中的 Origin 字段为目标地址，避免因跨域而导致的问题
          
          // 重写请求路径，将 /api 前缀替换为空字符串
          // 例如，/api/user/queryAllUserInfo 会被重写为 /user/queryAllUserInfo
          rewrite: (path) => path.replace(/^\/api/, '')
        }
      }
    }
  };
});
