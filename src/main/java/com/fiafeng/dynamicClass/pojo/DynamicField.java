package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data
public class DynamicField implements IDynamicObject, Serializable {

    private String name;

    private Class<?> type;

    private boolean isCollection;

    private Class<?> componentType;

    public void setType(Class<?> type) {
        this.type = type;
        if (Collection.class.isAssignableFrom(type)){
            setCollection(true);
            this.componentType =  type.getComponentType();
        }

    }

    private List<DynamicAnnotation> annotatedList;

    /**
     * 访问范围
     */
    private int modifiers;
}
