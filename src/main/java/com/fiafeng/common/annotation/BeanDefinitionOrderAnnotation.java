package com.fiafeng.common.annotation;


import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanDefinitionOrderAnnotation {
    int value() default 0;
}
