package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

@Data
public class DynamicException {

    private String name;
    Class<?> type;

}
