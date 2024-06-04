package com.fiafeng.validation.annotation;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationAnnotation {
}
