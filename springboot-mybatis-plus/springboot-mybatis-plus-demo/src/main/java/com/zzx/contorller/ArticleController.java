package com.zzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.zzx.entity.Article;
import com.zzx.model.vo.PageResult;
import com.zzx.model.vo.ResultVO;
import com.zzx.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 根据ID获取文章信息
     */
    @GetMapping("/{id}")
    public ResultVO<Article> getArticleById(@PathVariable("id") Long id) {
        Article article = articleService.getById(id);
        return article != null ? ResultVO.ok(article) : ResultVO.fail("文章不存在");
    }

    /**
     * 获取所有文章信息
     */
    @GetMapping("/list")
    public ResultVO<List<Article>> listAllArticles() {
        List<Article> articles = articleService.list();
        return ResultVO.ok(articles);
    }

    /**
     * 删除指定文章
     */
    @DeleteMapping("/{id}")
    public ResultVO deleteArticleById(@PathVariable("id") Long id) {
        boolean result = articleService.removeById(id);
        return result ? ResultVO.ok("文章删除成功") : ResultVO.fail("文章删除失败");
    }

    /**
     * 批量保存或更新文章
     */
    @PostMapping("/saveOrUpdateBatch")
    public ResultVO saveOrUpdateBatch(@RequestBody List<Article> articles) {
        boolean result = articleService.saveOrUpdateBatch(articles);
        return result ? ResultVO.ok("文章批量保存或更新成功") : ResultVO.fail("文章批量保存或更新失败");
    }

    /**
     * 自定义方法查询所有文章信息
     */
    @GetMapping("/queryAllArticleInfo")
    public ResultVO<List<Article>> queryAllArticles() {
        List<Article> articles = articleService.queryAllArticleInfo();
        return ResultVO.ok(articles);
    }

    /**
     * 分页查询1：
     * 使用BaseMapper接口内置的分页方法查询所有文章
     */
    @GetMapping("/queryAllArticleInfoByPagination")
    public ResultVO<Page<Article>> queryAllArticleInfoByPagination() {
        Page<Article> pageVo = articleService.queryAllArticleInfoByPagination();
        return ResultVO.ok(pageVo);
    }

    /**
     * 分页查询2:
     * 根据前端的分页参数查询所有文章信息
     * @param current 当前页码
     * @param size    每页记录数
     * @return 包含分页结果的 ResultVO
     */
    @GetMapping("/queryAllArticleInfoByParam")
    public ResultVO<Page<Article>> queryAllArticleInfoByParam(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Article> page = new Page<>(current, size);
        Page<Article> pageVo = articleService.queryAllArticleInfoByParam(page);
        return ResultVO.ok(pageVo);
    }

    /**
     * 分页查询3:
     * 自定义方法实现分页查询
     * @param current 当前页码
     * @param size    每页记录数
     * @return 包含分页结果的 ResultVO
     */
    @GetMapping("/queryAllArticleInfoBySelf")
    public ResultVO<PageResult<Article>> queryAllArticleInfoBySelf(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        // 调用服务层方法获取分页结果
        PageResult<Article> pageResult = articleService.queryAllArticleInfoBySelf(current, size);

        return ResultVO.ok(pageResult);
    }

    /**
     * 分页查询4:
     * 自定义方法实现分页查询
     * @param current 当前页码
     * @param size    每页记录数
     * @return 包含分页结果的 ResultVO
     */
    @GetMapping("/queryAllArticleInfoByPageHelper")
    public ResultVO<PageInfo<Article>> queryAllArticleInfoByPageHelper(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        // 调用服务层方法获取分页结果
        PageInfo<Article> pageResult = articleService.queryAllArticleInfoByPageHelper(current, size);

        return ResultVO.ok(pageResult);
    }
}
