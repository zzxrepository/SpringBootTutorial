package com.zzx.util;


import lombok.Data;

@Data
public class ResVo<T> {
    private Integer code;      // 状态码
    private String message;    // 消息内容
    private T content;         // 内容，可以是任何类型的数据

    // 构造方法
    public ResVo(Integer code, String message, T content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    // 成功的返回，通常是常用的，内容可以为空
    public static <T> ResVo<T> success(T content) {
        return new ResVo<>(200, "成功", content);
    }

    // 失败的返回，通常返回错误信息
    public static <T> ResVo<T> error(Integer code, String message) {
        return new ResVo<>(code, message, null);
    }

}
