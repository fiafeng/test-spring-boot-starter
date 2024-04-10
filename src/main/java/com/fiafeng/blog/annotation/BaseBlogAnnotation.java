package com.fiafeng.blog.annotation;

import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Scope("prototype")
public @interface BaseBlogAnnotation {
}
