package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.util.List;

@Data
public class DynamicClassMethod {


    String methodName;

    List<DynamicClass> argList;


    List<DynamicAnnotated> annotatedList;
}
