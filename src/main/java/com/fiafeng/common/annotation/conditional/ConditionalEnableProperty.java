package com.fiafeng.common.annotation.conditional;

import com.fiafeng.common.annotation.conditional.matches.ConditionalPropertyMatches;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalPropertyMatches.class)
public @interface ConditionalEnableProperty {
    String value() default "";
    boolean enable() default true;
}
