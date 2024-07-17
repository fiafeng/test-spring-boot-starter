package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class DynamicAnnotationMethod implements Serializable {

    private String name;

    private List<DynamicAnnotation> annotationList = new ArrayList<>();

    private List<DynamicArg> argList = new ArrayList<>();

    private boolean returnIsCollection;

    private Class<?> returnType;

    private Class<?> returnComponentType;

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
        HashSet<Class<?>> hashSet = new HashSet<>(Arrays.asList(returnType.getInterfaces()));
        if (hashSet.contains(Collection.class)){
            setReturnIsCollection(true);
            this.returnType =  returnType.getComponentType();
        }

    }

    private Object defaultValue;


}
