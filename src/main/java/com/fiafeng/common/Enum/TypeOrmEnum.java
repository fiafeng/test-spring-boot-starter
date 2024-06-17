package com.fiafeng.common.Enum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/29
 * @description
 */
public enum TypeOrmEnum {

    intType("int", new Class[]{Integer.class}, 11),
    floatType("float", Float.class),
    bigIntType("bigint", Long.class),
    varcharType("varchar", new Class[]{String.class}, 64),
    StringType("char", new Class[]{String.class}, 64),

    dateType("date", Date.class),


    ;

    public String mysqlType;

    public List<Class<?>> javaType;

    public Integer len = -1;

    TypeOrmEnum() {

    }

    TypeOrmEnum(String mysqlType, Class<?> javaType) {
        this.javaType = new ArrayList<>();
        this.javaType.add(javaType);
        this.mysqlType = mysqlType;
    }


    TypeOrmEnum(String mysqlType, Class<?> javaType, Integer len) {
        this.javaType = new ArrayList<>();
        this.javaType.add(javaType);
        this.mysqlType = mysqlType;
        this.len = len;
    }

    TypeOrmEnum(String mysqlType, Class<?>[] javaType, Integer len) {
        this.javaType = new ArrayList<>(Arrays.asList(javaType));
        this.mysqlType = mysqlType;
        this.len = len;
    }
}
