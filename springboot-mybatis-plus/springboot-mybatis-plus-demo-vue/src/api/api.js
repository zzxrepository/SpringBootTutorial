import axios from '../services/axios';  // 引入自定义的 axios 实例

/**
 * 方式1：请求分页文章信息
 */
export const fetchArticleInfoByPagination = () => axios.get('/article/queryAllArticleInfoByPagination');

/**
 * 方式2：请求分页文章信息
 */
export const fetchArticleInfoByParam = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoByParam', {
        params: {
            current: page,
            size: pageSize
        }
    });
};

/**
 * 方式3：请求分页文章信息
 */
export const fetchArticleInfoBySelf = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoBySelf', {
        params: {
            current: page,
            size: pageSize
        }
    });
};

/**
 * 方式4：请求分页文章信息
 */
export const fetchArticleInfoByPageHelper = (page, pageSize) => {
    return axios.get('/article/queryAllArticleInfoByPageHelper', {
        params: {
            current: page,
            size: pageSize
        }
    });
};