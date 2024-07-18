package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
public class DynamicInterface {

    private String name;

    private String packageName;


    private Class<?> type;

    private HashSet<String> importList = new HashSet<>();


    private List<DynamicMethod> methodList = new ArrayList<>();


    private List<DynamicAnnotation> annotatedList = new ArrayList<>();

    private List<DynamicInterface> interfaceList = new ArrayList<>();

    /**
     * 访问范围
     */
    private int modifiers;
}
