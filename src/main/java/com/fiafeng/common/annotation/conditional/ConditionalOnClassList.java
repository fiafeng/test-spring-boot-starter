package com.fiafeng.common.annotation.conditional;

import com.fiafeng.common.annotation.conditional.matches.ConditionalOnClassListMatches;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target(value = {ElementType.FIELD,ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnClassListMatches.class)
public @interface ConditionalOnClassList {
    String[] name() default {""};
}
