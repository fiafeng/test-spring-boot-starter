package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.io.Serializable;

@Data
public class DynamicAnnotationMethod implements Serializable {

    private String name;

    private Class<?> returnType;

    private Object value;





}
