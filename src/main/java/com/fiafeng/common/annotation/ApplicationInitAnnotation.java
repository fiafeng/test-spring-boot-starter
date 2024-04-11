package com.fiafeng.common.annotation;


import java.lang.annotation.*;

@Target(value = {ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApplicationInitAnnotation {
    int value() default 0;
}
