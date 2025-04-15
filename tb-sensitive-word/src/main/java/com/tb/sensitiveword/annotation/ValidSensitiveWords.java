package com.tb.sensitiveword.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 敏感词过滤自定义注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ValidSensitiveWords {

    /**
     * 是否过滤校验 (默认为false)
     * @return
     */
    boolean isValid() default false;
}
