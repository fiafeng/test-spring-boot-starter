package com.fiafeng.dynamicClass.pojo;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class DynamicClass implements IDynamicObject, Serializable {


    private boolean isInterface;

    private boolean isAnnotation;

    private String name;

    private String packageName;

    private List<String> importList = new ArrayList<>();

    private Class<?> parentClass;

    private Class<?> type;

    private List<DynamicClass> implementList = new ArrayList<>();

    private List<DynamicField> fieldList = new ArrayList<>();

    private List<DynamicMethod> methodList = new ArrayList<>();

    private List<DynamicAnnotation> annotationList = new ArrayList<>();

    /**
     * 是不是匿名类
     */
    private boolean isAnonymousClass;


    /**
     * 访问范围
     */
    private int modifiers;


}
