package com.tb.sensitiveword.annotation;

import com.tb.sensitiveword.constant.GlobalConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 敏感词过滤自定义参数（字段）注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface FilterSensitiveWords {

    /**
     * 敏感词替换字符
     * @return
     */
    String replacement() default GlobalConstants.REPLACEMENT;

    /**
     * 是否替换
     * @return
     */
    boolean isReplace() default GlobalConstants.IS_REPLACE;
}
