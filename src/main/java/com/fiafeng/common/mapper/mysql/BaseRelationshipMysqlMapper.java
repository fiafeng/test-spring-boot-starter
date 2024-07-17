package com.fiafeng.common.mapper.mysql;

import com.fiafeng.common.Enum.TypeOrmEnum;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Slf4j
public abstract class BaseRelationshipMysqlMapper extends BaseMysqlMapper {

    @Setter
    public Class<?> type;

    @Setter
    private ConnectionPoolServiceImpl connectionPoolService;

    @Setter
    public IMysqlTableProperties properties;


    public abstract String getOneName();

    public abstract String getTwoName();


    public String getIdName() {
        return getProperties().getIdName();
    }


    public String getTableName() {
        return getProperties().getTableName();
    }


    /**
     * 检查mysql表是否存在（没有检查属性），如果不存在，则创建
     */
    public void checkMysqlTableIsExist() {
        checkMysqlTableIsExist(TypeOrmEnum.intType);
    }

    /**
     * 检查mysql表是否存在（没有检查属性），如果不存在，则创建
     */
    public void checkMysqlTableIsExist(TypeOrmEnum typeOrmEnum) {
        getConnectionPoolService().createdMysqlTable(getIdName(), getTableName(), typeOrmEnum, getType());
    }

    public int insertObjectByValue(Object value1, Object value2) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getOneName(), value1);
        hashMap.put(getTwoName(), value2);
        return getConnectionPoolService().insertObjectByMap(getTableName(), hashMap);
    }

    public int insertObjectByObject(String OneName, String twoName, Object value1, Object value2) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(OneName, value1);
        hashMap.put(twoName, value2);
        return getConnectionPoolService().insertObjectByMap(getTableName(), hashMap);
    }


    public int deletedObjectByTwoValue(Object value1, Object value2) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getOneName(), value1);
        hashMap.put(getTwoName(), value2);
        return getConnectionPoolService().deletedObjectByMap(getTableName(), hashMap);
    }

    public int deletedObjectById(Object id) {
        return getConnectionPoolService().deletedObjectById(id, getTableName(), getIdName());
    }

    public List selectObjectListByNameValue(String colName, Object value) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(colName, value);
        return getConnectionPoolService().selectObjectListByColMap(getTableName(), getType(), hashMap);
    }

    public List selectObjectListByOneNameValue(Object value) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getOneName(), value);
        return getConnectionPoolService().selectObjectListByColMap(getTableName(), getType(), hashMap);
    }

    public List selectObjectListByTwoNameValue(Object value) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getTwoName(), value);
        return getConnectionPoolService().selectObjectListByColMap(getTableName(), getType(), hashMap);
    }

    public Object selectObjectByTwoValue(Object value1, Object value2) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getOneName(), value1);
        hashMap.put(getTwoName(), value2);
        return getConnectionPoolService().selectObjectByColMap(getTableName(), getType(), hashMap);
    }


}
