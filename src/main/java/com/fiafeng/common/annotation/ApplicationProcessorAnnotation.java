package com.fiafeng.common.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApplicationProcessorAnnotation {
    int value() default 0;
}
