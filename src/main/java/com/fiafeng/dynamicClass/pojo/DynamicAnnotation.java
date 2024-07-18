package com.fiafeng.dynamicClass.pojo;


import com.fiafeng.dynamicClass.utils.DynamicUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;

@Data
/*
  动态注解类
 */
public class DynamicAnnotation implements Serializable {

    /**
     * 注解名
     */
    private String name;

    /**
     * 实体类所在的包名
     */
    private String packageName;

    /**
     * 对应的实际类型
     */
    private Class<? extends Annotation> type;

    public void setType(Class<? extends Annotation> type) {
        this.type = type;
        this.packageName = DynamicUtils.getPackageName(type);
        this.importList.add(DynamicUtils.getImport(type));
    }

    private List<DynamicAnnotationMethod> valueList;

    public void setValueList(List<DynamicAnnotationMethod> valueList) {
        if (valueList != null && !valueList.isEmpty()) {
            this.valueList = valueList;
            for (DynamicAnnotationMethod dynamicAnnotationMethod : valueList) {
                this.importList.add(DynamicUtils.getImport(dynamicAnnotationMethod.getReturnType()));
            }
        }
    }

    private HashSet<String> importList = new HashSet<>();

    /**
     * 访问范围
     */
    private int modifiers;

}
