package com.tb.sensitiveword.aop;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.tb.sensitiveword.annotation.FilterSensitiveWords;
import com.tb.sensitiveword.annotation.ValidSensitiveWords;
import com.tb.sensitiveword.util.TrieOperateUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏感词过滤 aop切面实现
 *
 * @author tb
 * @since 2024-04-22
 */
@Aspect
@Component
public class SensitiveWordsAspect {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordsAspect.class);


    @Autowired
    private TrieOperateUtil operateUtil;

    @Pointcut("@annotation(com.tb.sensitiveword.annotation.ValidSensitiveWords)")
    public void pointcut() {
    }

    /**
     * 环绕处理
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("SensitiveWordsAspect.pointcut()")
    public Object filterSensitiveWords(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取参数
        Object[] args = joinPoint.getArgs();

        // 获取是否需要进行敏感词校验
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            ValidSensitiveWords anno = methodSignature.getMethod().getAnnotation(ValidSensitiveWords.class);
            if (!anno.isValid()) {
                return joinPoint.proceed();
            }

            // 获取所有方法的参数名称
            Parameter[] parameters = methodSignature.getMethod().getParameters();
            // 遍历方法的参数名称
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                // 字段（参数）敏感词过滤
                JSONObject result = fieldSensitiveWorldFilter(args, i, parameter);
                if (result.containsKey("isExist") && result.getBoolean("isExist")) {
                    String words = result.getString("words");
                    throw new RuntimeException("存在敏感内容【" + words + "】，请重新输入！");
                }
            }
        }
        return joinPoint.proceed(args);
    }


    /**
     * 字段（参数）敏感词过滤
     *
     * @param args
     * @param i
     * @param parameter
     * @throws IllegalAccessException
     */
    private JSONObject fieldSensitiveWorldFilter(Object[] args, int i, Parameter parameter) throws IllegalAccessException {
        JSONObject jsonObj = new JSONObject();
        Set<String> set = new HashSet<>();
        // 获取参数类型
        Class<?> type = parameter.getType();
        // 如果是字符串类型
        if (type == String.class) {
            FilterSensitiveWords filterSensitiveWords = parameter.getAnnotation(FilterSensitiveWords.class);
            if (filterSensitiveWords != null) {
                // 获取参数值
                String text = String.valueOf(args[i]);
                if (filterSensitiveWords.isReplace()) {
                    // 敏感词过滤替换后的字段值
                    String newText = replaceWord(filterSensitiveWords, text);
                    // 替换原来值
                    args[i] = newText;
                } else {
                    // 查询敏感词并获取敏感词数据
                    JSONObject result = findWord(text);
                    if (result.getBoolean("isExist")) {
                        jsonObj.put("isExist", result.getBoolean("isExist"));
                        // set存放去重
                        set.addAll(result.getJSONObject("wordsMap").keySet());
                    }
                }

            }
        }


        // 判断是否自定义类，classLoader等于null的时候不是自定义类
        if (type.getClassLoader() != null) {
            // 获取自定义对象的所有字段
            Field[] declaredFields = type.getDeclaredFields();
            Object obj = args[i];

            for (Field declaredField : declaredFields) {
                // 判断该字段上是否有敏感词过滤注解
                if (declaredField.getAnnotation(FilterSensitiveWords.class) != null) {
                    FilterSensitiveWords filterSensitiveWords = declaredField.getAnnotation(FilterSensitiveWords.class);
                    if (declaredField.getType() == String.class) {
                        // 取消该字段的安全访问检查
                        declaredField.setAccessible(true);
                        // 获取字段值
                        String fieldValue = String.valueOf(declaredField.get(obj));
                        if (filterSensitiveWords.isReplace()) {
                            // 敏感词过滤替换后的字段值
                            String newText = replaceWord(filterSensitiveWords, fieldValue);
                            // 替换原来值
                            declaredField.set(obj, newText);
                        } else {

                            JSONObject result = findWord(fieldValue);
                            if (result.getBoolean("isExist")) {
                                jsonObj.put("isExist", result.getBoolean("isExist"));
                                // set存放去重
                                set.addAll(result.getJSONObject("wordsMap").keySet());
                            }
                        }
                    }
                }
            }
        }
        jsonObj.put("words", set.stream().map(String::valueOf).collect(Collectors.joining(",")));
        return jsonObj;
    }

    /**
     * 敏感词过滤替换
     *
     * @param filterSensitiveWords
     * @param fieldValue
     * @return
     */
    private String replaceWord(FilterSensitiveWords filterSensitiveWords, String fieldValue) {
        StringBuffer newText = new StringBuffer();
        // 从redis中获取敏感词列表并放入TrieNode
        List<String> words = sensitiveWordsFromRedisAndSet();
        if (CollectionUtil.isNotEmpty(words)) {
            // 获取敏感词过滤后的值
            String text = operateUtil.replace(fieldValue, StringUtils.hasText(filterSensitiveWords.replacement()) ? filterSensitiveWords.replacement() : null);
            // 值拼接
            newText.append(text);
        }
        return StringUtils.hasText(newText.toString()) ? newText.toString() : fieldValue;
    }

    /**
     * 敏感词查询
     *
     * @param fieldValue
     * @return
     */
    private JSONObject findWord(String fieldValue) {
        JSONObject result = new JSONObject();
        boolean isExist = false;
        Map<String, Integer> wordsMap = new HashMap<>();
        // 从redis中获取敏感词列表并放入TrieNode
        List<String> words = sensitiveWordsFromRedisAndSet();
        if (CollectionUtil.isNotEmpty(words)) {
            // 查询敏感词并返回对应的词map
            wordsMap = operateUtil.find(fieldValue);
            if (CollectionUtil.isNotEmpty(wordsMap)) {
                isExist = true;
            }
        }
        result.put("isExist", isExist);
        result.put("wordsMap", new JSONObject(wordsMap));
        return result;
    }

    /**
     * 从redis中获取敏感词列表并放入TrieNode
     *
     * @return
     */
    private List<String> sensitiveWordsFromRedisAndSet() {
        List<String> words = operateUtil.sensitiveWordsFromRedisAndSet();
        return words;
    }

}
