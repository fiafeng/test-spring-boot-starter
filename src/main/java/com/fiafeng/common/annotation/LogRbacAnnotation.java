package com.fiafeng.common.annotation;


import com.fiafeng.common.Enum.OperateEnum;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRbacAnnotation {


    OperateEnum operateType();

    String otherOperateType();

    String desc();
}
