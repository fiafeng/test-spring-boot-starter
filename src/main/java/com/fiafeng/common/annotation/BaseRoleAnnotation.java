package com.fiafeng.common.annotation;


import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Scope("prototype")
public @interface BaseRoleAnnotation {

}
