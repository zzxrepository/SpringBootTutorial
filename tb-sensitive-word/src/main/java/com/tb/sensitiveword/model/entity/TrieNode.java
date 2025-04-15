package com.tb.sensitiveword.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词-前缀数
 *
 * @author tb
 * @since 2024-04-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrieNode implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 关键词结束标识
     */
    private boolean isKeywordEnd = false;

    /**
     * 子节点
     */
    private Map<Character,TrieNode> subNodes = new HashMap<>();


    //添加子节点
    public void addSubNode(Character c,TrieNode node){
        subNodes.put(c,node);
    }

    //获取子节点
    public TrieNode getSubNode(Character c){
        return subNodes.get(c);
    }

    public boolean isLastCharacter(){
        return subNodes.isEmpty();
    }


}
