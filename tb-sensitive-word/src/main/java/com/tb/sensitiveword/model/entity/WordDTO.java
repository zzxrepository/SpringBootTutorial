package com.tb.sensitiveword.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Word
 *
 * @author tanb
 * @version 1.0
 * @date 2024/4/26 0:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 敏感词
     */
    List<String> words;
}
