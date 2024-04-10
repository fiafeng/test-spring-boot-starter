package com.fiafeng.common.annotation;

import com.fiafeng.common.Enum.TypeOrmEnum;

import java.lang.annotation.*;

/**
 * @author Fiafeng
 * @create 2023/12/29
 * @description
 */
@Target(value = {ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoFiledAnnotation {

    String name() default ""; // 字段名称

    TypeOrmEnum type() default TypeOrmEnum.varcharType; // 对应的类型
    String comment() default "" ; //  注释

    int length() default 11;

    /**
     * 是否允许为空值
     * @return 是否允许为空值
     */
    boolean isNull() default true;
}
