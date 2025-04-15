package com.tb.sensitiveword.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tb.sensitiveword.enums.HttpCodeEnum;

import java.io.Serializable;

/**
 * 统一响应返回对象
 *
 * @author tanb
 * @version 1.0
 * @date 2023/2/8 22:28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public ResponseResult() {
        this.code = HttpCodeEnum.SUCCESS.getCode();
        this.msg = HttpCodeEnum.SUCCESS.getMsg();
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 成功
     *
     * @return
     */
    public static ResponseResult okResult() {
        return new ResponseResult();
    }

    /**
     * 成功有返回消息
     *
     * @param code
     * @param msg
     * @return
     */
    public static ResponseResult okResult(int code, String msg) {
        return new ResponseResult(code, msg, null);
    }

    /**
     * 成功（带数据）
     *
     * @param data
     * @return
     */
    public static ResponseResult okResult(Object data) {
        ResponseResult result = setHttpCodeEnum(HttpCodeEnum.SUCCESS, HttpCodeEnum.SUCCESS.getMsg());
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    /**
     * 失败
     *
     * @param code
     * @param msg
     * @return
     */
    public static ResponseResult errorResult(int code, String msg) {
        return new ResponseResult(code, msg);
    }

    /**
     * 失败
     *
     * @param enums
     * @return
     */
    public static ResponseResult errorResult(HttpCodeEnum enums) {
        return setHttpCodeEnum(enums, enums.getMsg());
    }


    public static ResponseResult setHttpCodeEnum(HttpCodeEnum enums) {
        return okResult(enums.getCode(), enums.getMsg());
    }

    public static ResponseResult setHttpCodeEnum(HttpCodeEnum enums, String msg) {
        return okResult(enums.getCode(), msg);
    }


    /**
     * 失败
     *
     * @param code
     * @param msg
     * @return
     */
    public ResponseResult<T> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    /**
     * 成功
     *
     * @param code
     * @param data
     * @return
     */
    public ResponseResult<T> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    /**
     * 成功
     *
     * @param code
     * @param data
     * @param msg
     * @return
     */
    public ResponseResult<T> ok(Integer code, T data, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        return this;
    }

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public ResponseResult<T> ok(T data) {
        this.data = data;
        return this;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
