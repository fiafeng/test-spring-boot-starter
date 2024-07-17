package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class  DynamicMethod implements Serializable {

    private String name;

    private List<DynamicAnnotation> annotatedList = new ArrayList<>();

    private List<DynamicArg> argList = new ArrayList<>();

    private boolean returnIsCollection;

    public DynamicArg returnArg;

    private String content;

    /**
     * 修饰符
     */
    private int modifiers;

}
