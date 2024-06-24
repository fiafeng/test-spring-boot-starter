package com.fiafeng.common.utils;

import com.fiafeng.common.Enum.TypeOrmEnum;
import com.fiafeng.common.annotation.AutoFiledAnnotation;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;

public class FiafengMysqlUtils {

    /**
     * 返回查询表是否存在与指定数据库的sql
     */
    public static String queryTableExistSql() {
        return "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? " +
                " and TABLE_SCHEMA = ?";
    }

    /**
     * 根据枚举类型返回mysql创建字段
     *
     * @param type   枚举类型
     * @param length 长度
     */
    public static String getColsTypeName(TypeOrmEnum type, int length) {
        String typeName;
        switch (type) {
            case intType:
                typeName = "int(" + ((length == 64) ? 11 : length) + ") ";
                break;
            case dateType:
                typeName = "date ";
                break;
            case floatType:
                typeName = "float ";
                break;
            default:
                typeName = "varchar(" + ((length == 11) ? 64 : length) + ") ";
                break;
        }
        return typeName;
    }

    /**
     * 根据属性获取对应的mysql数据库类型
     *
     * @param field 属性
     */
    public static String getFieldTypeName(Field field) {
        String typeName = "";
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            typeName = "varchar(" + 64 + ") ";
        } else if (fieldType == (Long.class)) {
            typeName = "int(" + 11 + ") ";
        } else if (fieldType == Date.class) {
            typeName = "date ";
        } else if (fieldType == Timestamp.class || fieldType == java.util.Date.class) {
            typeName = "datetime ";
        } else if (fieldType == Time.class) {
            typeName = "date ";
        } else if (fieldType == Byte.class) {
            typeName = "tinyint ";
        } else if (fieldType == Short.class) {
            typeName = "smallint ";
        } else if (fieldType == (Integer.class)) {
            typeName = "int(" + 11 + ") ";
        } else if (fieldType == Float.class) {
            typeName = "float ";
        } else if (fieldType == Double.class) {
            typeName = "double ";
        } else if (fieldType == HashSet.class) {
            typeName = "varchar(" + 256 + ") ";
        } else if (fieldType == Boolean.class) {
            typeName = "tinyint(1) ";
        }
        return typeName;
    }


    public static String createdTableSql(String tableName, Class<?> type) {
        return createdTableSql("id", tableName, TypeOrmEnum.intType, type);
    }

    public static String createdTableSql(String primaryName, String tableName, TypeOrmEnum primaryType, Class<?> type) {
        StringBuilder sql = new StringBuilder(
                "Create table  IF NOT EXISTS " + tableName + "(\n"
                        + "    " + primaryName + " ");
        if (primaryType == TypeOrmEnum.intType) {
            sql.append(primaryType.mysqlType
                    + " AUTO_INCREMENT ");
        }

        sql.append(" PRIMARY KEY, "
                        + "\n");

        for (Field field : type.getDeclaredFields()) {
            String filedName = field.getName();
            if (primaryName.equals(filedName)) {
                continue;
            }

            AutoFiledAnnotation fieldAnnotation = field.getDeclaredAnnotation(AutoFiledAnnotation.class);
            if (fieldAnnotation != null) {
                filedName = fieldAnnotation.name().isEmpty() ? filedName : fieldAnnotation.name();

                // TODO1 检查属性名字是否满足mysql 命名规范
                String comment = fieldAnnotation.comment();
                int length = fieldAnnotation.length();
                TypeOrmEnum typed = fieldAnnotation.type();
                String defaultValue = fieldAnnotation.defaultValue();

                if (defaultValue != null && !defaultValue.isEmpty()){

                }

                String typeName = getColsTypeName(typed, length);
                sql.append("    ")
                        .append(filedName)
                        .append(" ")
                        .append(typeName)
                        .append(fieldAnnotation.isNull() ? " null " : " not null ")
                        .append(defaultValue != null && !defaultValue.isEmpty() ? " default " + defaultValue : " ")
                        .append(comment.isEmpty() ? "" : " comment '" + comment + "' ")
                        .append(" , \n");
            } else {
                filedName = StringUtils.camelToUnderline(field.getName());
                String typeName = getFieldTypeName(field);
                sql.append("    ")
                        .append(filedName)
                        .append(" ")
                        .append(typeName)
                        .append(" null ")
                        .append(" , \n");

            }
        }
        return sql.substring(0, sql.lastIndexOf(", \n")) + "\n    )";
    }
}
