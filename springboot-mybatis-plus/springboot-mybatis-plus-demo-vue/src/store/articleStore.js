import { defineStore } from 'pinia';
import { fetchArticleInfoByPagination,fetchArticleInfoByParam,fetchArticleInfoBySelf,fetchArticleInfoByPageHelper } from "../api/api.js";

export const useArticleStoreByPagination = defineStore('articleStoreByPagination', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 10,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo() {
            try {

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByPagination();

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreByParam = defineStore('articleStoreByParam', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByParam(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreBySelf = defineStore('articleStoreBySelf', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoBySelf(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.flag && response.data.code === 20000) {
                    const { records, total, current} = response.data.data;

                    // 更新 Store 状态
                    this.articles = records;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = current;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});


export const useArticleStoreByPageHelper = defineStore('articleStoreByPageHelper', {
    state: () => ({
        articles: [], // 存储文章列表
        currentPage: 1, // 当前页
        pageSize: 3,   // 每页显示条数
        total: 0, // 总文章数
    }),
    actions: {
        async fetchArticleInfo(page) {
            try {
                // 设置当前页
                this.currentPage = page;

                // 发起请求，传递当前页和每页条数
                const response = await fetchArticleInfoByPageHelper(page, this.pageSize);

                // 检查响应是否成功
                if (response.data.code === 20000) {
                    const { list, total, pageNum} = response.data.data;

                    // 更新 Store 状态
                    this.articles = list;         // 当前页文章数据
                    this.total = total;              // 总记录数
                    this.currentPage = pageNum;      // 当前页码
                } else {
                    console.error('获取文章失败:', response.data.message);
                }
            } catch (error) {
                console.error('请求失败:', error);
            }
        },
    },
});
