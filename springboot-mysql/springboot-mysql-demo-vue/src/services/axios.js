import axios from 'axios';// 导入 Axios 库

// 创建 Axios 实例
const instance = axios.create({
    // 基础 URL，所有请求都会在此基础上拼接路径
    // '/api' 是前端代理路径，实际会被 Vite 配置代理到后端地址
    baseURL: "/api",
    
    // 超时时间，单位是毫秒。如果请求超过这个时间没有响应，将抛出超时错误
    timeout: 10000,
});

// 添加请求拦截器,在请求发送之前做一些处理
instance.interceptors.request.use(
    // 成功拦截的回调函数
    config => {
        // 处理请求头，例如添加 Content-Type 或自定义头
        console.log("before request"); // 打印日志，便于调试
        //告诉服务器请求期望的响应数据类型（即 JSON 格式）。但是，这并不会影响请求体的格式。
        config.headers.Accept = 'application/json, text/plain, text/html,*/*'; // 设置通用的 Accept 请求头
        return config; // 返回配置好的请求对象，继续发送请求
    },
    // 请求错误的回调函数
    error => {
        console.log("request error"); // 打印错误信息
        return Promise.reject(error); // 返回错误，阻止请求发送
    }
);

// 添加响应拦截器,在服务器响应后统一处理数据
instance.interceptors.response.use(
    // 处理成功响应的回调函数
    response => {
        console.log("after success response"); // 打印日志
        console.log("这是拦截器"); // 打印标志信息，便于调试
        console.log(response); // 打印完整的响应对象
        return response; // 返回响应数据，供后续逻辑使用
    },
    // 处理失败响应的回调函数
    error => {
        console.log("after fail response"); // 打印错误日志
        console.log(error); // 打印错误对象，便于调试
        return Promise.reject(error); // 返回错误，供调用者处理
    }
);

// 导出创建的 Axios 实例，便于在项目中复用
export default instance;
