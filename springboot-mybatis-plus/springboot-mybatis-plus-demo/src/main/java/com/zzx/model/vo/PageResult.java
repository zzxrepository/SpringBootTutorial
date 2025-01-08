package com.zzx.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private int current; // 当前页
    private int size; // 每页条数
    private int total; // 总记录数
    private int pages; // 总页数
    private List<T> records; // 当前页数据
}