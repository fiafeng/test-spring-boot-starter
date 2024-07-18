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

    private List<DynamicAnnotation> annotatedList;

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
