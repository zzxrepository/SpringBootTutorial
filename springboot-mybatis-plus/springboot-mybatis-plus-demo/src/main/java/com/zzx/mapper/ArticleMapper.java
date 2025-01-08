package com.zzx.mapper;

import com.zzx.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzx.model.vo.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author flyvideo
* @description 针对表【t_article(文章信息表)】的数据库操作Mapper
* @createDate 2025-01-03 20:54:38
* @Entity com.zzx.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询所有文章信息自定义方法
     * @return
     */
    List<Article> queryAllArticleInfo();

    /**
     * 查询文章列表（带分页）
     * @param offset 偏移量
     * @param limit  每页记录数
     * @return 文章列表
     */
    List<Article> selectArticles(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询文章总数
     * @return 总记录数
     */
    int countArticles();

}




