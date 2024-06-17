package com.fiafeng.mybatis.annotation;


import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageAnnotation {

    int page() default -1;

    /**
     * -1 时取系统默认值，0时不进行分页
     * @return
     */
    int pageSize() default -1;

    int suffix() default 0;
}
