package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DynamicConstructorMethod implements Serializable {


    List<DynamicArg> argList;

    List<DynamicAnnotation> annotationList;


    /**
     * 修饰符号
     */
    private int modifiers;
}
