package com.fiafeng.common.Enum;

public enum LogicEnum {

    or("或"),
    and("与");
    ;

    final String value;

    String getValue(){
        return this.value;
    }

    LogicEnum(String value){
        this.value = value;
    }
}
