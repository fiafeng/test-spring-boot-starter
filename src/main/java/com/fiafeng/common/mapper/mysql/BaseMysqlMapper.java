package com.fiafeng.common.mapper.mysql;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.AutoFiledAnnotation;
import com.fiafeng.common.utils.FiafengMysqlUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.common.Enum.TypeOrmEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
//@Service
public abstract class BaseMysqlMapper {

    @Getter
    public Class<?> type;

    @Setter
    public String tableName;

    @Setter
    public String idName = "id"; // 主键名字

    public ConnectionPoolServiceImpl connectionPoolService;

    /**
     * 数据库内表的名字映射
     */
    @Setter
    public String tableColName = "name";

    @Setter
    public String roleIdName = "roleId";

    @Setter
    public String permissionIdName = "permissionId";

    @Setter
    public String userIdName = "userId";


    public void setType(Class type) {
        this.type = type;
    }


    /**
     * 根据枚举类型返回mysql创建字段
     *
     * @param type   枚举类型
     * @param length 长度
     */
    public static String getTypeName(TypeOrmEnum type, int length) {
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
     * 根据属性进行
     *
     * @param field 属性
     */
    public static String getTypeName(Field field) {
        String typeName = "";
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            typeName = "varchar(" + 64 + ") ";
        } else if (fieldType == (Long.class)) {
            typeName = "int(" + 11 + ") ";
        } else if (fieldType == Date.class) {
            typeName = "date ";
        } else if (fieldType == Timestamp.class) {
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


        }
        return typeName;
    }

    /**
     * 检查mysql表是否存在（没有检查属性），如果不存在，则创建
     */
    public void checkMysqlTableIsExist(String url) {

        if (connectionPoolService == null) {
            connectionPoolService = FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class);
        }

        if (!checkTableExist(url)) {
            createdTable(TypeOrmEnum.intType);
        }
    }

    /**
     * 创建mysql数据表
     *
     * @param primaryType 主键类型
     */
    private void createdTable(TypeOrmEnum primaryType) {
        String primaryName = idName;
        String sql = FiafengMysqlUtils.createdTableSql(primaryName, tableName, primaryType, type);

        connectionPoolService.executeSql(sql);
        log.info("创建表" + tableName + "语句为:\n" + sql);
    }

    private boolean checkTableExist(String url) {
        String databaseName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));

        List<Map<String, Object>> columns = connectionPoolService.queryForList(FiafengMysqlUtils.queryTableExistSql, new Object[]{tableName, databaseName});
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
        T t = object;
        Field[] declaredFields = object.getClass().getDeclaredFields();
        List<Object> objectList = new ArrayList<>();
        StringBuilder insertColsName = new StringBuilder(" ( ");
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (flag && idName.equals(fieldName)) {
                continue;
            }
            fieldName = StringUtils.camelToUnderline(fieldName);
            try {
                declaredField.setAccessible(true);
                Object value = declaredField.get(t);
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

        String sql = "insert into " + tableName + insertColsName + values;

        Object[] objects = objectList.toArray(new Object[objectList.size()]);
        return connectionPoolService.updateSql(sql, objects) == 1;
    }

    public <T> boolean insertObjectList(List<T> objectList) {
        return insertObjectList(objectList, false);
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
                if (flag && idName.equals(fieldName)) {
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
                for (Object o : parameterObjectList) {
                    values.append("?,");
                }

                sql = "insert into " + tableName + insertColsName + values.substring(0, values.length() - 1) + ")";
            }
            list.add(parameterObjectList.toArray(new Object[parameterObjectList.size()]));
        }


        return connectionPoolService.updateBatchByListSql(sql, list) == 1;
    }


    public <T> boolean insertObject(T object) {
        return insertObject(object, true);
    }


    public boolean deletedObjectById(Long ObjectId) {
        return connectionPoolService.updateSql("delete from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(idName) + " = ?", new Object[]{ObjectId}) == 1;
    }


    public boolean deletedObjectByIdList(List<Long> objectIdList) {
        if (objectIdList == null || objectIdList.isEmpty()) {
            return true;
        }
        StringBuilder sql = new StringBuilder("delete from " + StringUtils.camelToUnderline(tableName) + " where  " + StringUtils.camelToUnderline(idName) + " in (");
        for (Long objectId : objectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        String string = sql.toString();
        return connectionPoolService.updateSql(string) == objectIdList.size();
    }

    public <T> boolean updateObject(T Object) {
        T t = Object;
        List<Object> objectList = new ArrayList<>();
        String sql = getSqlUpdate(t, objectList);
        Object[] objects = objectList.toArray(new Object[objectList.size()]);
        return connectionPoolService.updateSql(sql, objects) == 1;
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
            Object[] objects = objectList.toArray(new Object[objectList.size()]);
            list.add(objects);
            if (sql.isEmpty()) {
                sql = updateColsName;
            }
        }
        int result = connectionPoolService.updateBatchByListSql(sql, list);
        return result == objecList.size();
    }


    public <T> List<T> selectObjectListAll() {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName);
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql);
        return ConnectionPoolServiceImpl.getObjectList(maps, type);
    }

    public <T> T selectObjectByObjectName(String objectName) {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(tableColName) + "= ?";
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql, new Object[]{objectName});
        if (maps.size() != 1) {
            return null;
        }
        return ConnectionPoolServiceImpl.getObject(maps.get(0), type);
    }


    public <T> List<T> selectObjectListByObjectIdList(List<Long> ObjectIdList) {
        StringBuilder sql = new StringBuilder("select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(idName) + " in (");
        for (Long objectId : ObjectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql.toString());
        List objectList = ConnectionPoolServiceImpl.getObjectList(maps, type);
        return (List<T>) objectList;
    }


    public <T> T selectObjectByObjectId(Long objectId) {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(idName) + " = ?";
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql, new Object[]{objectId});
        if (maps.size() != 1) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parse(JSON.toJSONString(maps.get(0)));
        return ConnectionPoolServiceImpl.getObject(jsonObject, type);
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
        StringBuilder insertColsName = new StringBuilder("update " + StringUtils.camelToUnderline(tableName) + " set ");
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            declaredField.setAccessible(true);
            if (idName.equals(fieldName)) {
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
            if (idName.equals(fieldName)) {
                try {
                    objectList.add(declaredField.get(t));
                } catch (IllegalAccessException e) {
                }
            }
        }
        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + " where " + StringUtils.camelToUnderline(idName) + "=?;");
        return insertColsName.toString();
    }


    public <T, W> List<T> selectObjectByKeyAndValueList(String key, W value) {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(key) + "=?";
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql, new Object[]{value});
        return ConnectionPoolServiceImpl.getObjectList(maps, type);
    }

    public <T, W> T selectObjectByKeyAndValue(String key, W value) {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(key) + "=?";
        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql, new Object[]{value});
        return (T) ConnectionPoolServiceImpl.getObjectList(maps, type).get(0);
    }

    public <T, D, W> T selectObjectByName1Name2AndValue1Value2(String name1, String name2, D value1, W value2) {
        String sql = "select * from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(name1) + "=? and " +
                StringUtils.camelToUnderline(name2) + "=?";

        List<Map<String, Object>> maps = connectionPoolService.queryForList(sql, new Object[]{value1, value2});
        if (maps.size() != 1) {
            return null;
        }
        return ConnectionPoolServiceImpl.getObject(maps.get(0), type);
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
            StringBuilder sql = new StringBuilder("select " + cols + " from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(keyList.get(0)) + "=? ");
            for (int i = 1; i < keyList.size(); i++) {
                sql.append("and ").append(StringUtils.camelToUnderline(keyList.get(0))).append("=? ");
            }
            Object[] objects = objectList.toArray();
            List<Map<String, Object>> maps = connectionPoolService.queryForList(sql.toString(), objects);
            if (maps.size() != 1) {
                return null;
            }
            return ConnectionPoolServiceImpl.getObject(maps.get(0), type);
        }


    }

}
