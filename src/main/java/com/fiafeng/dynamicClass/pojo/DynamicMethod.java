package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class DynamicMethod implements Serializable {

    private String name;

    private List<DynamicAnnotation> annotationList = new ArrayList<>();

    private List<DynamicArg> argList = new ArrayList<>();

    private HashSet<String> importList = new HashSet<>();

    private List<DynamicException> exceptionList = new ArrayList<>();


    public void setAnnotationList(List<DynamicAnnotation> annotationList) {
        if (annotationList != null && !annotationList.isEmpty()) {
            this.annotationList = annotationList;
            for (DynamicAnnotation dynamicAnnotation : annotationList) {
                this.importList.addAll(dynamicAnnotation.getImportList());
            }

        }
    }

    public void setArgList(List<DynamicArg> argList) {
        if (argList != null && !argList.isEmpty()) {
            this.argList = argList;
            for (DynamicArg dynamicArg : argList) {
                importList.addAll(dynamicArg.getImportList());
            }
        }
    }

    public void setReturnArg(DynamicArg returnArg) {
        this.returnArg = returnArg;
        if (returnArg.getImportList() != null && !returnArg.getImportList().isEmpty()) {
            importList.addAll(returnArg.getImportList());
        }
    }

    public DynamicArg returnArg;

    private DynamicContent content;

    /**
     * 修饰符
     */
    private int modifiers;

}
