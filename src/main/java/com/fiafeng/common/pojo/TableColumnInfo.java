package com.fiafeng.common.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fiafeng
 * @create 2023/12/29
 * @description
 */
@Data
@Accessors(chain = true)
@ToString
public class TableColumnInfo {

    String columnName;
    String columnType;
    String columnComment;
    String DataType;
    Boolean isNull;
    BigInteger characterMaximumLength;

    public static TableColumnInfo tableFiledInfoByObj(Map<String,Object> objectHashMap){
        TableColumnInfo tableColumnInfo = new TableColumnInfo();

        tableColumnInfo
                .setColumnName((String) objectHashMap.get("COLUMN_NAME"))
                .setColumnComment((String) objectHashMap.get("COLUMN_COMMENT"))
                .setColumnType((String) objectHashMap.get("COLUMN_TYPE"))
                .setDataType((String) objectHashMap.get("DATA_TYPE"))
                .setIsNull("YES".equals(objectHashMap.get("IS_NULLABLE")))
                .setCharacterMaximumLength((BigInteger) objectHashMap.get("CHARACTER_MAXIMUM_LENGTH"));
        return tableColumnInfo;
    }

    public static List<TableColumnInfo> tableFiledInfoByList(List<Map<String,Object>> columns){
        List<TableColumnInfo> tableColumnInfoList = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            TableColumnInfo tableColumnInfo = TableColumnInfo.tableFiledInfoByObj(column);

            tableColumnInfoList.add(tableColumnInfo);
        }

        return tableColumnInfoList;
    }

    public static HashMap<String,TableColumnInfo> tableFiledInfoByMap(List<Map<String,Object>> columns){
        HashMap<String,TableColumnInfo> hashMap = new HashMap<>();
        for (Map<String, Object> column : columns) {
            TableColumnInfo tableColumnInfo = TableColumnInfo.tableFiledInfoByObj(column);
            hashMap.put(tableColumnInfo.getColumnName(),tableColumnInfo);
        }

        return hashMap;
    }
}
