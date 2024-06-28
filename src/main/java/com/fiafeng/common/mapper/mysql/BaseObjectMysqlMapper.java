package com.fiafeng.common.mapper.mysql;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(tableColName) + "= ?";
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, new Object[]{objectName});
        if (maps.size() != 1) {
            return null;
        }
        return ConnectionPoolServiceImpl.getObject(maps.get(0), getType());
    }


    public <T> List<T> selectObjectListByObjectIdList(List<Long> ObjectIdList) {
        StringBuilder sql = new StringBuilder("select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(getIdName()) + " in (");
        for (Long objectId : ObjectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql.toString());
        return ConnectionPoolServiceImpl.getObjectList(maps, getType());
    }


    public <T> T selectObjectByObjectId(Long objectId) {
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(getIdName()) + " = ?";
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, new Object[]{objectId});
        if (maps.size() != 1) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parse(JSON.toJSONString(maps.get(0)));
        return ConnectionPoolServiceImpl.getObject(jsonObject, getType());
    }


    /**
     * 获取更新语句的sql,同时根据objectList的引用更新需要修改的值
     *
     * @param t          泛型类
     * @param objectList 传递的Object对象list
     * @param <T>        具体的类
     */
    private <T> String getSqlUpdate(T t, List<Object> objectList) {
        Field[] declaredFields = t.getClass().getDeclaredFields();
        StringBuilder insertColsName = new StringBuilder("update " + StringUtils.camelToUnderline(getTableName()) + " set ");
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            declaredField.setAccessible(true);
            if (getIdName().equals(fieldName)) {
                continue;
            }
            fieldName = StringUtils.camelToUnderline(fieldName);
            try {
                Object value = declaredField.get(t);
                if (value != null) {
                    insertColsName.append(fieldName).append("=?,");
                    objectList.add(value);
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (getIdName().equals(fieldName)) {
                try {
                    objectList.add(declaredField.get(t));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + " where " + StringUtils.camelToUnderline(getIdName()) + "=?;");
        return insertColsName.toString();
    }


    public <T, W> List<T> selectObjectByKeyAndValueList(String key, W value) {
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(key) + "=?";
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, new Object[]{value});
        return ConnectionPoolServiceImpl.getObjectList(maps, getType());
    }

    public <T, W> T selectObjectByKeyAndValue(String key, W value) {
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(key) + "=?";
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, new Object[]{value});
        return (T) ConnectionPoolServiceImpl.getObjectList(maps, getType()).get(0);
    }

    public <T, D, W> T selectObjectByName1Name2AndValue1Value2(String name1, String name2, D value1, W value2) {
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(name1) + "=? and " +
                StringUtils.camelToUnderline(name2) + "=?";

        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, new Object[]{value1, value2});
        if (maps.size() != 1) {
            return null;
        }
        return ConnectionPoolServiceImpl.getObject(maps.get(0), getType());
    }

    public <T> T selectObjectByHashMap(HashMap<String, Object> hashMap) {
        StringBuilder sql = new StringBuilder("select * from " + StringUtils.camelToUnderline(getTableName()) + " where  1 = 1");
        List<Object> objectList = new ArrayList<>();
        for (String key : hashMap.keySet()) {
            if (hashMap.get(key) != null) {
                objectList.add(hashMap.get(key));
            }
            sql.append(" and ").append(StringUtils.camelToUnderline(key)).append("=? ");
        }

        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql.toString(), objectList.toArray());
        if (maps.size() != 1) {
            return null;
        }
        return ConnectionPoolServiceImpl.getObject(maps.get(0), getType());
    }


    public <T> T selectObjectByNameListAndValueList(String cols, List<String> keyList, List<Object> objectList) {
        if (keyList == null || keyList.isEmpty() || objectList == null || objectList.isEmpty()) {
            log.error("输入参数为空,keyList=>" + keyList + "。valueList=>" + objectList);
            return null;
        }
        if (keyList.size() != objectList.size()) {
            log.error("输入参数长度不一致,keyList.size=>" + keyList.size() + "。valueList.size=>" + objectList.size());
            return null;
        }
        if (cols == null || cols.isEmpty()) {
            cols = "*";
        }

        if (keyList.size() == 1) {
            return selectObjectByKeyAndValue(keyList.get(0), objectList.get(0));
        } else {
            StringBuilder sql = new StringBuilder("select " + cols + " from " + StringUtils.camelToUnderline(getTableName()) + " where "
                    + StringUtils.camelToUnderline(keyList.get(0)) + "=? ");
            for (int i = 1; i < keyList.size(); i++) {
                sql.append("and ").append(StringUtils.camelToUnderline(keyList.get(0))).append("=? ");
            }
            Object[] objects = objectList.toArray();
            List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql.toString(), objects);
            if (maps.size() != 1) {
                return null;
            }
            return ConnectionPoolServiceImpl.getObject(maps.get(0), getType());
        }


    }

}
