package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class BaseObjectMysqlMapper extends BaseMysqlMapper {

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
        StringBuilder cols = new StringBuilder();
        for (Field field : getType().getDeclaredFields()) {
            cols.append(StringUtils.camelToUnderline(field.getName())).append(",");
        }
        return cols.substring(0, cols.length() - 2);
    }

    public void createdMysqlTable() {
        getConnectionPoolService().createdMysqlTable(getTableName(), getType());
    }


    /**
     * 使用jdbcTemplate新增一条数据
     *
     * @param object 新增对象
     * @param flag   true时，使用数据库自增主键。false时，使用数据内的id值
     */
    public int insertObject(Object object, Boolean flag) {
        return getConnectionPoolService().insertObject(object, getIdName(), getTableName(), flag);
    }

    public int insertObjectList(List objectList) {
        return insertObjectList(objectList, true);
    }

    public int insertObjectList(List objectList, Boolean flag) {
        return getConnectionPoolService().insertObjectList(objectList, getIdName(), getTableName(), flag);
    }


    public int insertObject(Object object) {
        return insertObject(object, false);
    }


    public int deletedObjectById(Long objectId) {
        return getConnectionPoolService().deletedObjectById(objectId, getIdName(), getTableName());
    }


    public int deletedObjectByIdList(List objectIdList) {
        return getConnectionPoolService().deletedObjectByIdList(objectIdList, getIdName(), getTableName());
    }

    public int updateObject(Object object) {
        return getConnectionPoolService().updateObjectById(object, getIdName(), getTableName());
    }


    public int updateObjectList(List objecList) {
        return getConnectionPoolService().updateObjectList(objecList, getIdName(), getTableName());
    }


    public List selectObjectListAll() {
        return getConnectionPoolService().selectObjectListAll(type, getTableName());
    }

    public Object selectObjectByObjectName(String colName, Object valueObject) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), colName, valueObject);
    }


    public List selectObjectListByObjectIdList(List<Long> objectIdList) {
        return getConnectionPoolService().selectObjectListByObjectIdList(getTableName(), getIdName(), type, objectIdList);
    }


    public Object selectObjectByObjectId(Long objectId) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), getIdName(), objectId);
    }


    public List selectObjectListByColValue(String colName, Object value) {
        return getConnectionPoolService().selectObjectListByColName(getTableName(), getType(), colName, value);
    }

    public Object selectObjectByColValue(String colName, Object value) {
        return getConnectionPoolService().selectObjectByColName(getTableName(), getType(), colName, value);
    }

    public Object selectObjectByName1Name2AndValue1Value2(String colName1, String colName2, Object value1, Object value2) {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put(colName1, value1);
        objectHashMap.put(colName2, value2);
        return getConnectionPoolService().selectObjectByColMap(getTableName(), getType(), objectHashMap);
    }

    public Object selectObjectByHashMap(HashMap<String, Object> hashMap) {
        return getConnectionPoolService().selectObjectByColMap(getTableName(), getType(), hashMap);
    }


    public Object selectObjectListByHashMap(HashMap<String, Object> hashMap) {
        return getConnectionPoolService().selectObjectListByColMap(getTableName(), getType(), hashMap);
    }
}
