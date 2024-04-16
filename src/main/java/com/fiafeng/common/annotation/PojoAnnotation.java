package com.fiafeng.common.annotation;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
@Primary
@Scope("prototype")
public @interface PojoAnnotation {
}
