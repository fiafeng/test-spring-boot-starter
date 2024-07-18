package com.fiafeng.dynamicClass.pojo;


import com.fiafeng.dynamicClass.utils.DynamicUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class DynamicClass implements Serializable {


    private boolean isInterface;

    private boolean isAnnotation;

    private String name;

    private String packageName;

    private HashSet<String> importList = new HashSet<>();

    private Class<?> parentClass;

    private Class<?> type;


    private List<DynamicInterface> implementList = new ArrayList<>();

    private List<DynamicField> fieldList = new ArrayList<>();

    private List<DynamicMethod> methodList = new ArrayList<>();

    private List<DynamicAnnotation> annotationList = new ArrayList<>();

    public void setType(Class<?> type) {
        this.type = type;
        this.packageName = DynamicUtils.getPackageName(type);
        this.importList.add(DynamicUtils.getImport(type));
    }

    public void setImplementList(List<DynamicInterface> implementList) {
        if (implementList != null && !implementList.isEmpty()) {
            this.implementList = implementList;
            for (DynamicInterface dynamicInterface : implementList) {
                this.importList.addAll(dynamicInterface.getImportList());
            }
        }
    }

    public void setAnnotationList(List<DynamicAnnotation> annotationList) {
        if (annotationList != null && !annotationList.isEmpty()) {
            this.annotationList = annotationList;
            for (DynamicAnnotation dynamicAnnotation : annotationList) {
                this.importList.addAll(dynamicAnnotation.getImportList());
            }
        }
    }

    public void setMethodList(List<DynamicMethod> methodList) {
        if (methodList != null && !methodList.isEmpty()) {
            this.methodList = methodList;
            for (DynamicMethod dynamicMethod : methodList) {
                this.importList.addAll(dynamicMethod.getImportList());
            }
        }
    }

    public void setFieldList(List<DynamicField> fieldList) {
        if (fieldList != null && !fieldList.isEmpty()) {
            this.fieldList = fieldList;
            for (DynamicField dynamicField : fieldList) {
                this.importList.addAll(dynamicField.getImportList());
            }
        }
    }

    /**
     * 是不是匿名类
     */
    private boolean isAnonymousClass;


    /**
     * 访问范围
     */
    private int modifiers;


}
