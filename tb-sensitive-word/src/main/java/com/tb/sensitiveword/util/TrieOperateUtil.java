package com.tb.sensitiveword.util;


import cn.hutool.core.collection.CollectionUtil;
import com.tb.sensitiveword.constant.GlobalConstants;
import com.tb.sensitiveword.model.entity.TrieNode;
import com.tb.sensitiveword.service.ISensitiveWordFilterService;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词-前缀树操作工具类
 */
@Component
public class TrieOperateUtil {

    @Autowired
    private ISensitiveWordFilterService sensitiveWordFilterService;

    //根节点
    private TrieNode rootNode = new TrieNode();


    /**
     * 添加m敏感词
     *
     * @param word 词
     */
    public void addWord(String word) {
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            TrieNode node = tmpNode.getSubNode(c);
            if (null == node) {
                //初始化子节点
                node = new TrieNode();
                tmpNode.addSubNode(c, node);
            }
            //指向子节点，进入下一轮循环
            tmpNode = node;
            //设置结束标志
            if (i == word.length() - 1) {
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 替换敏感词
     *
     * @param text         待处理文本
     * @param afterReplace 替换后的词
     * @return 处理后的文本
     */
    public String replace(String text, String afterReplace) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        TrieNode tmpNode = rootNode;
        //指针2、指针3
        int begin = 0, pos = 0;
        // 循环替换敏感词
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (isSymbol(c)) {
                //若处于根节点，对应情况一，将符号计入结果，让指针2向下走一步
                if (tmpNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                //无论符号在开头还是敏感词中间，指针3都向下走一步
                pos++;
                continue;
            }
            // 获取子节点
            tmpNode = tmpNode.getSubNode(c);
            if (null == tmpNode) {
                // 以begin开头的的字符串不是敏感词
                result.append(text.charAt(begin));
                // 指针2和指针3共同指向指针2的下一个位置
                pos = ++begin;
                // 指向根节点
                tmpNode = rootNode;
            } else if (tmpNode.isLastCharacter()) { //如果是最后一个词
                // 匹配完成, 进行替换
                result.append(StringUtils.isEmpty(afterReplace) ? GlobalConstants.REPLACEMENT : afterReplace);
                // 进入下一个位置
                begin = ++pos;
                // 重新指向根节点
                tmpNode = rootNode;
            } else {
                // 检查下一个字符
                pos++;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    /**
     * 判断是否为符号
     *
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 查找敏感词
     *
     * @param text 待处理文本
     * @return 统计数据 key: word value: count
     */
    public Map<String, Integer> find(String text) {
        Map<String, Integer> resultMap = new HashMap<>(16);
        TrieNode tmpNode = rootNode;
        StringBuilder word = new StringBuilder();
        // 指针2、指针3
        int begin = 0, pos = 0;
        // 循环查找敏感词
        while (pos < text.length()) {
            char c = text.charAt(pos);
            tmpNode = tmpNode.getSubNode(c);
            if (null == tmpNode) {
                // 指针2和指针3共同指向指针2的下一个位置
                pos = ++begin;
                // 指向根节点
                tmpNode = rootNode;
            } else if (tmpNode.isLastCharacter()) {
                // 匹配完成
                String w = word.append(c).toString();
                resultMap.put(w, resultMap.getOrDefault(w, 0) + 1);
                // 进入下一个位置
                begin = ++pos;
                // 指向根节点
                tmpNode = rootNode;
                // 重置
                word = new StringBuilder();
            } else {
                // 拼接
                word.append(c);
                // 匹配上向后移
                pos++;
            }
        }
        return resultMap;
    }


    /**
     * 获取敏感词列表并插入TrieNode
     *
     */
    public List<String> sensitiveWordsFromRedisAndSet() {
        // 获取Redis中的敏感词列表
        List<String> words = sensitiveWordFilterService.sensitiveWordsFromRedis();
        if (CollectionUtil.isNotEmpty(words)) {
            // 循环插入TrieNode
            for (String word : words) {
                addWord(word);
            }
        }

        return words;
    }
}
