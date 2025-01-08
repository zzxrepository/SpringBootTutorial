package com.zzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zzx.entity.Article;
import com.zzx.model.vo.PageResult;
import com.zzx.service.ArticleService;
import com.zzx.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author flyvideo
* @description 针对表【t_article(文章信息表)】的数据库操作Service实现
* @createDate 2025-01-03 20:54:38
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService{
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Article> queryAllArticleInfo() {
        return articleMapper.queryAllArticleInfo();
    }

    @Override
    public Page<Article> queryAllArticleInfoByPagination() {
        // 分页参数（当前页：1，每页条数：10）
        Page<Article> page = new Page<>(1, 10);

        // 调用 MyBatis-Plus 提供的分页查询方法
        page = articleMapper.selectPage(page, new QueryWrapper<Article>().eq("is_delete", 0));

        return page;
    }

    @Override
    public Page<Article> queryAllArticleInfoByParam(Page<Article> page) {
        Page<Article> articlePage = this.baseMapper.selectPage(page, new QueryWrapper<Article>().eq("is_delete", 0));
        return articlePage;
    }

    @Override
    public PageResult<Article> queryAllArticleInfoBySelf(int current, int size) {
        PageResult<Article> pageResult = new PageResult<>();
        pageResult.setCurrent(current);
        pageResult.setSize(size);

        // 计算偏移量
        int offset = (current - 1) * size;

        // 查询当前页的数据
        List<Article> articleList = articleMapper.selectArticles(offset, size);
        pageResult.setRecords(articleList);

        // 查询总记录数
        int total = articleMapper.countArticles();
        pageResult.setTotal(total);

        // 计算总页数
        int pages = (int) Math.ceil((double) total / size);
        pageResult.setPages(pages);

        return pageResult;
    }

    @Override
    public PageInfo<Article> queryAllArticleInfoByPageHelper(Integer current, Integer size) {
        // 使用 PageHelper 启动分页，参数为当前页码和每页大小
        PageHelper.startPage(current, size);

        // 调用查询mybatis-plus中的通用 Mapper 方法，返回所有文章的列表
        List<Article> articleList = articleMapper.selectList(null);

        // 使用 PageInfo 来封装查询结果
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);

        return pageInfo;
    }
}