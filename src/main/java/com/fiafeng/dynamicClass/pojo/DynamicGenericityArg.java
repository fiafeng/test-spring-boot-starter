package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class DynamicGenericityArg implements Serializable {

   private String name;

    private String packageName;

    private Class<?> type;

    public void setType(Class<?> type) {
        this.type = type;
        if (Collection.class.isAssignableFrom(type)){
            setCollection(true);
            this.componentType =  type.getComponentType();
        }
    }



    private boolean isCollection;

    private Class<?> componentType;

}
