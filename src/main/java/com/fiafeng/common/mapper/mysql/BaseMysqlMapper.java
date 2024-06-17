package com.fiafeng.common.mapper.mysql;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.Enum.TypeOrmEnum;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.utils.FiafengMysqlUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseMysqlMapper {

    @Getter
    @Setter
    public Class<?> type;

    @Getter
    @Setter
    private ConnectionPoolServiceImpl connectionPoolService;

    @Getter
    @Setter
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

    public String getPermissionIdName() {
        return getProperties().getPermissionIdName();
    }

    public String getRoleIdName() {
        return getProperties().getRoleIdName();
    }

    public String getUserIdName() {
        return getProperties().getUserIdName();
    }


    /**
     * 检查mysql表是否存在（没有检查属性），如果不存在，则创建
     */
    public void checkMysqlTableIsExist(String url) {
        checkMysqlTableIsExist(url, TypeOrmEnum.intType);
    }


    public void checkMysqlTableIsExist(String url, TypeOrmEnum typeOrmEnum) {

        if (getConnectionPoolService() == null) {
            setConnectionPoolService(FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class));
        }

        if (!checkTableExist(url)) {
            createdTable(typeOrmEnum);
        }
    }

    /**
     * 创建mysql数据表
     *
     * @param primaryType 主键类型
     */
    private void createdTable(TypeOrmEnum primaryType) {
        String primaryName = getIdName();
        String sql = FiafengMysqlUtils.createdTableSql(primaryName, getTableName(), primaryType, getType());

        getConnectionPoolService().executeSql(sql);
        log.info("创建表" + getTableName() + "语句为:\n" + sql);
    }

    private boolean checkTableExist(String url) {
        String databaseName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));

        List<Map<String, Object>> columns = getConnectionPoolService().queryForList(FiafengMysqlUtils.queryTableExistSql(), new Object[]{getTableName(), databaseName});
        return !columns.isEmpty();
    }


    /**
     * 使用jdbcTemplate新增一条数据
     *
     * @param object 新增对象
     * @param <T>    对象类型
     * @param flag   true时，使用数据库自增主键。false时，使用数据内的id值
     */
    public <T> boolean insertObject(T object, Boolean flag) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        List<Object> objectList = new ArrayList<>();
        StringBuilder insertColsName = new StringBuilder(" ( ");
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (flag && getIdName().equals(fieldName)) {
                continue;
            }
            fieldName = StringUtils.camelToUnderline(fieldName);
            try {
                declaredField.setAccessible(true);
                Object value = declaredField.get(object);
                if (value != null) {
                    insertColsName.append(fieldName).append(",");
                    objectList.add(value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + ") ");

        StringBuilder values = new StringBuilder("VALUES (");
        for (Object o : objectList) {
            values.append("?,");
        }
        values = new StringBuilder(values.substring(0, values.length() - 1) + ")");

        String sql = "insert into " + getTableName() + insertColsName + values;

        Object[] objects = objectList.toArray(new Object[objectList.size()]);
        return getConnectionPoolService().updateSql(sql, objects) == 1;
    }

    public <T> boolean insertObjectList(List<T> objectList) {
        return insertObjectList(objectList, true);
    }

    public <T> boolean insertObjectList(List<T> objectList, Boolean flag) {
        List<Object[]> list = new ArrayList<>();
        String sql = null;
        for (T object : objectList) {
            Field[] declaredFields = object.getClass().getDeclaredFields();
            List<Object> parameterObjectList = new ArrayList<>();
            StringBuilder insertColsName = new StringBuilder(" ( ");
            for (Field declaredField : declaredFields) {
                String fieldName = declaredField.getName();
                fieldName = StringUtils.camelToUnderline(fieldName);
                if (flag && getIdName().equals(fieldName)) {
                    continue;
                }
                try {
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(object);
                    if (value != null) {
                        insertColsName.append(fieldName).append(",");
                        parameterObjectList.add(value);
                    }
                } catch (IllegalAccessException e) {
                }
            }
            if (sql == null) {
                insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + ") ");

                StringBuilder values = new StringBuilder("VALUES (");
                for (Object ignored : parameterObjectList) {
                    values.append("?,");
                }

                sql = "insert into " + getTableName() + insertColsName + values.substring(0, values.length() - 1) + ")";
            }
            list.add(parameterObjectList.toArray(new Object[parameterObjectList.size()]));
        }


        return getConnectionPoolService().updateBatchByListSql(sql, list) == 1;
    }


    public <T> boolean insertObject(T object) {
        return insertObject(object, true);
    }


    public boolean deletedObjectById(Long ObjectId) {
        return getConnectionPoolService().updateSql(
                "delete from " + StringUtils.camelToUnderline(getTableName()) + " where " + StringUtils.camelToUnderline(getIdName()) + " = ?",
                new Object[]{ObjectId}) == 1;
    }


    public boolean deletedObjectByIdList(List<Long> objectIdList) {
        if (objectIdList == null || objectIdList.isEmpty()) {
            return true;
        }
        StringBuilder sql = new StringBuilder("delete from " + StringUtils.camelToUnderline(getTableName()) + " where  " + StringUtils.camelToUnderline(getIdName()) + " in (");
        for (Long objectId : objectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        String string = sql.toString();
        return getConnectionPoolService().updateSql(string) == objectIdList.size();
    }

    public <T> boolean updateObject(T Object) {
        List<Object> objectList = new ArrayList<>();
        String sql = getSqlUpdate(Object, objectList);
        Object[] objects = objectList.toArray(new Object[0]);
        return getConnectionPoolService().updateSql(sql, objects) == 1;
    }


    public <T> boolean updateObjectList(List<T> objecList) {
        if (objecList == null || objecList.isEmpty()) {
            return false;
        }
        List<Object[]> list = new ArrayList<>();
        String sql = "";
        for (T t : objecList) {
            List<Object> objectList = new ArrayList<>();
            String updateColsName = getSqlUpdate(t, objectList);
            Object[] objects = objectList.toArray(new Object[0]);
            list.add(objects);
            if (sql.isEmpty()) {
                sql = updateColsName;
            }
        }
        int result = getConnectionPoolService().updateBatchByListSql(sql, list);
        return result == objecList.size();
    }


    public <T> List<T> selectObjectListAll() {
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName());
        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql);
        return ConnectionPoolServiceImpl.getObjectList(maps, type);
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
            } catch (IllegalAccessException e) {
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
        String sql = "select * from " + StringUtils.camelToUnderline(getTableName()) + " where  1 = 1";
        List<Object> objectList = new ArrayList<>();
        for (String key : hashMap.keySet()) {
            if (hashMap.get(key) != null) {
                objectList.add(hashMap.get(key));
            }
            sql += " and " + StringUtils.camelToUnderline(key) + "=? ";
        }

        List<Map<String, Object>> maps = getConnectionPoolService().queryForList(sql, objectList.toArray());
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
