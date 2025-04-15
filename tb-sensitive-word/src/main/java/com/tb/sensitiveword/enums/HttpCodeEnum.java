package com.tb.sensitiveword.enums;

/**
 * http请求枚举
 *
 * @author tanb
 * @version 1.0
 * @date 2023/2/9 12:17
 */
public enum HttpCodeEnum {

    // 成功
    SUCCESS(200,"操作成功"),
    // 系统异常
    SYSTEM_ERROR(500,"系统异常，请联系管理员");



    private int code;
    private String msg;


    HttpCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
