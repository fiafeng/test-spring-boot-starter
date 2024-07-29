package com.fiafeng.common.properties.mysql;

import com.fiafeng.common.properties.IFiafengProperties;

public interface IMysqlTableProperties extends IFiafengProperties {

    String getTableName();

   default void setTableName(String tableName){
   }

    default String getIdName() {return "id";}

    default String getTableColName(){
        return "name";
    }

    default String getRoleIdName(){
        return "roleId";
    }

    default String getUserIdName(){
        return "userId";
    }

    default String getPermissionIdName(){
        return "permissionId";
    }
}
