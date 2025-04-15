package com.tb.sensitiveword.exception;

import com.tb.sensitiveword.enums.HttpCodeEnum;
import com.tb.sensitiveword.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author tanb
 * @version 1.0
 * @date 2023/2/18 21:37
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 其他异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {
        //打印异常信息
        log.error("出现了异常！{}",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(HttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
