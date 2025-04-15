package com.tb.sensitiveword.controller;

import com.tb.sensitiveword.annotation.FilterSensitiveWords;
import com.tb.sensitiveword.annotation.ValidSensitiveWords;
import com.tb.sensitiveword.model.entity.News;
import com.tb.sensitiveword.model.entity.WordDTO;
import com.tb.sensitiveword.service.ISensitiveWordFilterService;
import com.tb.sensitiveword.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * SensitiveWordFilterController
 * 敏感词过滤服务Controller
 *
 * @author tanb
 * @version 1.0
 * @date 2024/4/25 23:59
 */
@RestController
@RequestMapping("/sensitive-word")
public class SensitiveWordFilterController {

    @Resource
    private ISensitiveWordFilterService sensitiveWordFilterService;

    /**
     * 初始化敏感词到redis
     */
    @PostMapping("/initWords")
    public void initSensitiveWords2Redis(@RequestBody WordDTO wordDTO) {
        sensitiveWordFilterService.initSensitiveWord2Redis(wordDTO.getWords());

    }

    /**
     * 1、方法参数注解
     * <p>
     * 保存新闻
     * <p>
     * ValidSensitiveWords注解
     * isValid： 是否过滤校验，默认false（不过滤）
     * <p>
     * FilterSensitiveWords注解
     * isReplace： 是否替换敏感词，默认false（不替换）
     * replacement： 替换敏感词后的内容，默认替换为***，可自定义
     *
     * @param content
     * @return
     */
    @GetMapping("/saveNews/{content}")
    @ValidSensitiveWords(isValid = true)
    public ResponseResult<String> saveNews(@PathVariable("content") @FilterSensitiveWords(isReplace = true, replacement = "###") String content) {
        return ResponseResult.okResult(content);
    }


    /**
     * 2、实体类注解
     * <p>
     * 保存新闻
     * <p>
     * ValidSensitiveWords注解
     * isValid： 是否过滤校验，默认false（不过滤）
     * <p>
     * FilterSensitiveWords注解
     * isReplace： 是否替换敏感词，默认false（不替换）
     * replacement： 替换敏感词后的内容，默认替换为***，可自定义
     *
     * @param news
     * @return
     */
    @PostMapping("/saveNews")
    @ValidSensitiveWords(isValid = true)
    public ResponseResult<News> saveNews(@RequestBody News news) {
        return ResponseResult.okResult(news);
    }


}
