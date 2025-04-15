package com.tb.sensitiveword.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tb.sensitiveword.annotation.FilterSensitiveWords;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * News
 *
 * @author tb
 * @version 1.0
 * @date 2024/4/26 0:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News implements Serializable {
    private static final long serialVersionUID = 1L;

    // 主键
    String id;
    // 标题
    @FilterSensitiveWords
    String title;
    // 内容
    @FilterSensitiveWords
    String content;
    // 作者
    String author;
    // 来源
    String source;
    // 发布时间
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime publishTime;

}
