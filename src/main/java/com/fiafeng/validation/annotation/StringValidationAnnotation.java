package com.fiafeng.validation.annotation;


import jdk.nashorn.internal.objects.annotations.Getter;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringValidationAnnotation {
    /**
     * 最小长度
     *
     * @return 最小长度
     */
    int min() default -1;

    /**
     * 最大长度
     *
     * @return 最大长度
     */
    int max() default -1;

    /**
     * 正则表达式
     *
     * @return 正则表达式
     */
    String regex() default "";

    /**
     * 参数名称
     *
     * @return 参数名称
     */
    @Getter
    String fieldName() default "";

    /**
     * 默认是不允许为null的
     */
    boolean allowNull() default true;

}