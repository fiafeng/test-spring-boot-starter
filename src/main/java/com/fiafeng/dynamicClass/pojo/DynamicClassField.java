package com.fiafeng.dynamicClass.pojo;


import lombok.Data;

import java.util.List;

@Data
public class DynamicClassField {

    String filedName;

    Class<?> type;

    List<DynamicAnnotated> annotatedList;
}
