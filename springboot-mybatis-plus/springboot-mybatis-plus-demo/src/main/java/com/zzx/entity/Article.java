package com.zzx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_article")
public class Article {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 主键

    private String category; // 文章分类

    private String tags; // 文章标签

    private String articleCover; // 文章缩略图

    private String articleTitle; // 标题

    private String articleAbstract; // 摘要

    private String articleContent; // 内容

    private Integer isTop; // 是否置顶：0-否，1-是

    private Integer status; // 状态：0-草稿，1-已发布

    @TableLogic
    //逻辑删除字段 int mybatis-plus下,默认 逻辑删除值为1 未逻辑删除 0
    private Integer isDelete; // 是否删除：0-否，1-是

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime; // 更新时间

}
