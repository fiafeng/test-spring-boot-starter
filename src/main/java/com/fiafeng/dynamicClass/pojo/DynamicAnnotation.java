package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.List;

@Data
/**
 * 动态注解类
 */
public class DynamicAnnotation implements IDynamicObject , Serializable {

    /**
     * 注解名
     */
    private String name;

    /**
     * 实体类所在的包名
     */
    private String packageName;

    /**
     * 注解上面的注解
     */
    private List<DynamicAnnotation> parentAnnotated;

    /**
     * 对应的实际类型
     */
    private Class<? extends Annotation> type;

    private List<DynamicAnnotationMethod> valueList;

    /**
     * 访问范围
     */
    private int modifiers;

}
