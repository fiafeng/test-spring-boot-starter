package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.util.List;

@Data
public class DynamicClass {


    boolean isInterface;
    boolean isAnnotation;

    String className;

    String packageName;

    List<String> importList;




    List<DynamicClassField> fieldList;





}
