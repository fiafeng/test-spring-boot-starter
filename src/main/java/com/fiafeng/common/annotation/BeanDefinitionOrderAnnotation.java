package com.fiafeng.common.annotation;


import com.fiafeng.common.constant.ModelConstant;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BeanDefinitionOrderAnnotation {
    int value() default ModelConstant.defaultOrder;
}

