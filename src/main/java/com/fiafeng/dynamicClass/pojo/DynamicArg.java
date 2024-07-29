package com.fiafeng.dynamicClass.pojo;


import com.fiafeng.dynamicClass.utils.DynamicUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
public class DynamicArg implements Serializable {

   private String name;


    private Class<?> type;

    public void setType(Class<?> type) {
        this.type = type;
        importList.add(DynamicUtils.getImport(type));
    }

    List<DynamicAnnotation> annotationList;

    public void setAnnotationList(List<DynamicAnnotation> annotationList) {
        this.annotationList = annotationList;
        if (annotationList != null && !annotationList.isEmpty()){
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

    private boolean paramType = false;


    private List<Class<?>> componentTypeList;

    private HashSet<String> importList = new HashSet<>();

    public DynamicArg(String name) {
        this.name = name;
    }

    public static DynamicArg getReturn(){
       return new DynamicArg("return");
    }

}
