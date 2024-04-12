package com.fiafeng.rbac.annotation;

import com.fiafeng.rbac.Enum.LogicEnum;

import java.lang.annotation.*;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
@Target( value = {ElementType.METHOD} )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface HasRoleAnnotation {
    String[] value() default "";

    /**
     * 如果方法上同时存在HasRoleAnnotation和HasRoleAnnotation两个注解时的行为。or是只要有一个满足就可以了，and是要两个都满足
     */
    LogicEnum logic() default LogicEnum.or;
}
