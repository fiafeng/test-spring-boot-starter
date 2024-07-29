package com.fiafeng.dynamicClass.pojo;


import com.fiafeng.dynamicClass.utils.DynamicUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

@Data
public class DynamicField implements Serializable {

    private String name;

    private Class<?> type;

    private boolean paramType = false;

    private HashSet<String> importList = new HashSet<>();

    private List<Class<?>> componentTypeList;

    private List<DynamicAnnotation> annotationList;

    private Object defaultValue;

    public void setType(Class<?> type) {
        this.type = type;
        this.importList.add(DynamicUtils.getImport(type));
    }

    public void setAnnotationList(List<DynamicAnnotation> annotationList) {
        if (annotationList != null && !annotationList.isEmpty()) {
            this.annotationList = annotationList;
            for (DynamicAnnotation dynamicAnnotation : annotationList) {
                this.importList.addAll(dynamicAnnotation.getImportList());
            }
        }
    }

    public void setComponentTypeList(List<Class<?>> componentTypeList) {
        if (componentTypeList.isEmpty()){
            return;
        }
        this.componentTypeList = componentTypeList;
        this.paramType = true;
        for (Class<?> aClass : componentTypeList) {
            importList.add(DynamicUtils.getImport(aClass));
        }
    }


    /**
     * 访问范围
     */
    private int modifiers;
}
