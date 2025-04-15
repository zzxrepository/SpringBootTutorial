package com.zzx.aspect;
import java.lang.annotation.*;
/** 
* @date 2023/10/6 
* @time 下午9:19 
* @discription 
**/ 
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {
    /**
    * 日志描述信息
    * @return
    **/ 
    String description() default "";
}