package com.zzx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.zzx.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzx.model.vo.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author flyvideo
 * @description 针对表【t_article(文章信息表)】的数据库操作Service
 * @createDate 2025-01-03 20:54:38
 */
public interface ArticleService extends IService<Article> {
    /**
     * 查询所有文章的服务接口方法
     *
     * @return
     */
    List<Article> queryAllArticleInfo();

    Page<Article> queryAllArticleInfoByPagination();

    Page<Article> queryAllArticleInfoByParam(Page<Article> page);

    PageResult<Article> queryAllArticleInfoBySelf(int current, int size);

    PageInfo<Article> queryAllArticleInfoByPageHelper(Integer current, Integer size);
}
