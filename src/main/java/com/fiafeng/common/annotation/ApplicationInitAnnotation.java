package com.fiafeng.common.annotation;


import java.lang.annotation.*;

@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
/*
  value 值越大，执行越早
 */
public @interface ApplicationInitAnnotation {
    int value() default 0;
}
