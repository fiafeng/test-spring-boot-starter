package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Data
public abstract class BaseObjectMysqlMapper {

    public Class<?> type;

    private ConnectionPoolServiceImpl connectionPoolService;

    public IMysqlTableProperties properties;

    public String getTableName() {
        return getProperties().getTableName();
    }

    public String getIdName() {
        return getProperties().getIdName();
    }

    public String getTableColName() {
        return getProperties().getTableColName();
    }

    public String getAllCols() {
        String cols = "";
        for (Field field : getType().getDeclaredFields()) {
            cols += StringUtils.camelToUnderline(field.getName()) + ",";
        }
        return cols.substring(0, cols.length() - 2);
    }


    /**
     * 使用jdbcTemplate新增一条数据
     *
     * @param object 新增对象
     * @param <T>    对象类型
     * @param flag   true时，使用数据库自增主键。false时，使用数据内的id值
     */
    public <T> boolean insertObject(T object, Boolean flag) {
        return getConnectionPoolService().insertObject(object, getTableName(), getIdName(), flag);
    }

    public <T> boolean insertObjectList(List<T> objectList) {
        return insertObjectList(objectList, true);
    }

    public <T> boolean insertObjectList(List<T> objectList, Boolean flag) {
        return getConnectionPoolService().insertObjectList(objectList, getIdName(), getTableName(), flag);
    }


    public <T> boolean insertObject(T object) {
        return insertObject(object, true);
    }


    public boolean deletedObjectById(Long objectId) {
        return getConnectionPoolService().deletedObjectById(objectId, getIdName(), getTableName());
    }


    public boolean deletedObjectByIdList(List<Long> objectIdList) {
        return getConnectionPoolService().deletedObjectByIdList(objectIdList, getIdName(), getTableName());
    }

    public <T> boolean updateObject(T object) {
        return getConnectionPoolService().updateObject(object, getIdName(), getTableName());
    }


    public <T> boolean updateObjectList(List<T> objecList) {
        return getConnectionPoolService().updateObjectList(objecList, getIdName(), getTableName());
    }


    public <T> List<T> selectObjectListAll() {
        return getConnectionPoolService().selectObjectListAll(type, getTableName());
    }

    public <T> T selectObjectByObjectName(String objectName, String tableColName) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), objectName, tableColName);
    }


    public <T> List<T> selectObjectListByObjectIdList(List<Long> objectIdList) {
        return getConnectionPoolService().selectObjectListByObjectIdList(getTableName(), getIdName(), type, objectIdList);
    }


    public <T> T selectObjectByObjectId(Long objectId) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), getIdName(), objectId);
    }


    public <T, W> List<T> selectObjectListByKeyAndValue(String colName, W value) {
        return getConnectionPoolService().selectObjectListByColName(getTableName(), getType(), colName, value);
    }

    public <T, W> T selectObjectByKeyAndValue(String colName, W value) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), colName, value);
    }

    public <T, D, W> T selectObjectByName1Name2AndValue1Value2(String colName1, String colName2, D value1, W value2) {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put(colName1, value1);
        objectHashMap.put(colName2, value2);
        return getConnectionPoolService().selectObjectByColMap(getTableName(), getType(), objectHashMap);
    }

    public <T> T selectObjectByHashMap(HashMap<String, Object> hashMap) {
        return getConnectionPoolService().selectObjectByColMap(getTableName(), getType(), hashMap);
    }


    public <T> List<T> selectObjectListByHashMap(HashMap<String, Object> hashMap) {
        return getConnectionPoolService().selectObjectListByColMap(getTableName(), getType(), hashMap);
    }
}