package com.fiafeng.common.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.Enum.TypeOrmEnum;
import com.fiafeng.common.config.bean.DefaultDataSource;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.properties.mysql.FiafengMysqlProperties;
import com.fiafeng.common.utils.FiafengMysqlUtils;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@NoArgsConstructor
public class ConnectionPoolServiceImpl {

    DataSource dataSource;

    @Value("${spring.datasource.url}")
    @Getter
    public String url;

    public static volatile List<Connection> connectionPool = new ArrayList<>();

    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private static final ThreadLocal<Integer> threadLocalInt = new ThreadLocal<>();
    private static final ThreadLocal<Statement> threadLocalStatement = new ThreadLocal<>();

    private static final CopyOnWriteArrayList<Boolean> connectionUser = new CopyOnWriteArrayList<>();

    FiafengMysqlProperties mysqlProperties;


    public ConnectionPoolServiceImpl(FiafengMysqlProperties mysqlProperties) {
        this.mysqlProperties = mysqlProperties;
    }


    @Setter
    @Getter
    private int maxSize = 10;

    private static boolean flag = false;

    private static final AtomicInteger userCount = new AtomicInteger();

    public ConnectionPoolServiceImpl(DataSource dataSource, FiafengMysqlProperties mysqlProperties) {
        this.dataSource = dataSource;
        if (dataSource instanceof DefaultDataSource) {
            flag = true;
        }

        this.mysqlProperties = mysqlProperties;
    }

    /**
     * 检查对应数据库里面是否存在指定表名的表
     *
     * @param tableName 表名
     */
    public boolean checkTableExist(String tableName) {
        try {
            String schema = dataSource.getConnection().getSchema();
            List<Map<String, Object>> columns = queryForList(FiafengMysqlUtils.queryTableExistSql(), new Object[]{tableName, schema});
            return columns.isEmpty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据表名，和对应的实体类检查表是否存在，如不存在则创建
     *
     * @param tableName 表名
     * @param typeClass 实体类
     */
    public void createdMysqlTable(String tableName, Class<?> typeClass) {
        if (checkTableExist(tableName)) {
            String createdTableSql = FiafengMysqlUtils.createdTableSql(tableName, typeClass);
            executeSql(createdTableSql);
        }
    }

    /**
     * 根据表名，和对应的实体类检查表是否存在，如不存在则创建
     *
     * @param tableName 表名
     * @param typeClass 实体类
     */
    public void createdMysqlTable(String primaryName, String tableName, TypeOrmEnum primaryType, Class<?> typeClass) {
        if (checkTableExist(tableName)) {
            String createdTableSql = FiafengMysqlUtils.createdTableSql(primaryName, tableName, primaryType, typeClass);
            executeSql(createdTableSql);
        }
    }


    /*
    获取连接
     */

    public Connection getConnection() {
        // 如果没有连接池，使用自定义的连接池
        if (flag) {
            Connection connection = null;
            int userCountAndIncrement = userCount.get();
            // 检查链接池里面是否还有空闲的线程
            if (userCountAndIncrement < connectionPool.size()) {
                // 检查当前连接池里面哪一个是空闲从连接，并且返回当前链接
                for (int i = 0; i < connectionUser.size() && connection == null; i++) {
                    if (!connectionUser.get(i)) {
                        synchronized (this) {
                            if (connectionUser.get(i)) {
                                connection = connectionPool.get(i);
                                connectionUser.set(i, true);
                                threadLocalConnection.set(connection);
                                threadLocalInt.set(i);
                                userCount.getAndIncrement();
                            }
                        }
                    }
                }
            }
            // 当前连接池没有空闲的线程，检查线程池的数量有没有达到设定的数量
            else if (connectionPool.size() < maxSize) {
                synchronized (this) {
                    int currentSize = connectionPool.size();
                    // 再次检查线程池的数量有没有达到设定的数量
                    if (currentSize < maxSize) {
                        // 线程池没有达到设定的最大值
                        connection = getDefaultConnection();
                        threadLocalInt.set(currentSize);
                        userCount.getAndIncrement();
                        connectionUser.add(currentSize, true);
                    } else {
                        // 线程池已经满了
                        connection = getDefaultConnection();
                    }
                }
            } else {
                // 当前线程池里面没有找到空闲的线程，创建一个链接直接返回
                connection = getDefaultConnection();
            }
            return connection;
        } else {
            // 找到了系统配置的线程池。直接获取链接然后返回即可
            return getDefaultConnection();
        }

    }

    private Connection getDefaultConnection() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            threadLocalConnection.set(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public <T extends Statement> T getStatement(@NonNull String sql) {
        PreparedStatement statement = null;
        Connection connection = getConnection();
        try {
            statement = connection.prepareStatement(sql);

        } catch (Exception ignore) {

        }
        return (T) statement;
    }


    public static void close() {

        closeStatement();
        // 没有使用自定义的连接池
        if (!flag) {
            // 使用了连接池的。直接进行释放即可
            try {
                threadLocalConnection.get().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 使用了自定义的连接池
        Connection connection = threadLocalConnection.get();
        Integer integer = threadLocalInt.get();
        try {
            if (!connection.getAutoCommit())
                connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (integer == null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {

            connectionUser.set(integer, false);
            userCount.getAndDecrement();
        }
    }

    private static void closeStatement() {

        Statement statement = threadLocalStatement.get();
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }


    // 实际执行增删改查的方法

    /**
     * 执行查询sql，返回结果对象集合List
     *
     * @param sql sql
     * @return 返回结果对象集合List
     */
    public List<Map<String, Object>> queryForList(String sql) {
        return queryForList(sql, (Object[]) null);
    }

    /**
     * 执行查询sql，返回结果对象集合List
     *
     * @param sql     sql
     * @param objects 执行sql时需要的参数
     * @return 结果对象集合
     */
    public List<Map<String, Object>> queryForList(String sql, Object[] objects) {
        PreparedStatement statement = null;
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            statement = getStatement(sql);
            setParamObject(objects, statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> hashMap = new HashMap<>();
                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    try {
                        Object object = resultSet.getObject(j);
                        String colName = metaData.getColumnName(j);
                        hashMap.put(colName, object);
                    } catch (Exception ignore) {
                    }
                }
                if (!hashMap.isEmpty()) {
                    list.add(hashMap);
                }

            }
        } catch (Exception ignore) {
        } finally {
            try {
                resultSet.close();
            } catch (Exception ignore) {

            }
            try {
                statement.close();
            } catch (SQLException ignore) {
            }
            close();
        }
        return list;
    }

    /**
     * 执行查询sql，返回结果对象集合List
     *
     * @param sql     sql
     * @param objects 执行sql时需要的参数
     * @return 返回结果对象集合List
     */
    public List<Map<String, Object>> queryForList(String sql, List<Object> objects) {
        return queryForList(sql, objects.toArray());
    }


    /**
     * 执行查询sql，返回结果对象Map
     *
     * @param sql sql
     * @return 结果对象Map
     */
    @Nullable
    public Map<String, Object> queryFoObject(String sql) {
        List<Map<String, Object>> mapList = queryForList(sql);
        if (mapList.isEmpty()) {
            return null;
        }

        if (mapList.size() != 1) {
            throw new ServiceException("预期查询结果为1个，但是查询结果出现了" + mapList.size() + "个");
        }

        return mapList.get(0);
    }

    /**
     * 执行查询sql，返回结果对象Map
     *
     * @param sql     sql
     * @param objects 执行sql时需要的参数
     * @return 返回结果对象Map
     */
    @Nullable
    public Map<String, Object> queryFoObject(String sql, Object[] objects) {
        List<Map<String, Object>> mapList = queryForList(sql, objects);
        if (mapList.isEmpty()) {
            return null;
        }

        if (mapList.size() != 1) {
            throw new ServiceException("预期查询结果为1个，但是查询结果出现了" + mapList.size() + "个");
        }

        return mapList.get(0);
    }

    /**
     * 执行查询sql，返回结果对象Map
     *
     * @param sql     sql
     * @param objects 执行sql时需要的参数
     * @return 返回结果对象Map
     */
    @Nullable
    public Map<String, Object> queryFoObject(String sql, List<Object> objects) {
        return queryFoObject(sql, objects.toArray());
    }

    /**
     * 执行sql
     *
     * @param sql sql
     */
    public void executeSql(String sql) {
        executeSql(sql, (Object[]) null);
    }

    /**
     * 执行 @param sql
     *
     * @param objects 执行sql时需要的参数
     * @param sql     sql
     */
    public void executeSql(String sql, Object[] objects) {
        PreparedStatement statement = null;
        try {
            statement = getStatement(sql);
            setParamObject(objects, statement);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            close();
        }
    }

    /**
     * 执行 @param sql
     *
     * @param objects 执行sql时需要的参数
     * @param sql     sql
     */
    public void executeSql(String sql, List<Object> objects) {
        executeSql(sql, objects.toArray());
    }

    /**
     * 批量执行同一条sql，每次参数参数不一样
     *
     * @param sql        sql
     * @param objectList sql参数集合
     * @return 执行的sql影响的总行数
     */
    public int batchExecuteSql(String sql, List<Object[]> objectList) {
        int result = 0;
        if (objectList == null || objectList.isEmpty()) {
            return result;
        }

        Connection connection = getConnection();
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(sql);
            for (Object[] objects : objectList) {
                if (objects != null && objects.length != 0) {
                    setParamObject(objects, statement);
                    statement.addBatch();
                }
            }
            try {
                int[] ints = statement.executeBatch();
                for (int i : ints) {
                    result += i;
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
            }
            statement.clearBatch();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException(e);


        } finally {
            close();
        }

        return result;
    }


    /**
     * 批量执行多条sql
     *
     * @param sqlList sql
     * @return 执行的sql影响的总行数
     */
    public int batchExecuteSqlList(List<String> sqlList) {
        int result = 0;
        if (sqlList == null || sqlList.isEmpty()) {
            return result;
        }

        Connection connection = getConnection();
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            for (String sql : sqlList) {
                statement.addBatch(sql);
            }

            try {
                for (int i : statement.executeBatch()) {
                    result += i;
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
            }
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }

        return result;
    }

    private void setParamObject(Object[] objects, PreparedStatement statement) {

        StringBuilder string = new StringBuilder();
        try {
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    Object object = objects[i];
                    if (object instanceof String) {
                        statement.setString(i + 1, (String) object);
                    } else if (object instanceof Long) {
                        statement.setLong(i + 1, (Long) object);
                    } else if (object instanceof HashSet) {
                        string = new StringBuilder();
                        HashSet<Object> hashSet = (HashSet<Object>) object;
                        for (Object o : hashSet) {
                            string.append(o.toString()).append("，");
                        }
                        if (string.length() > 1) {
                            string = new StringBuilder(string.substring(0, string.length() - 1));
                            statement.setString(i + 1, string.toString());
                        } else {
                            statement.setObject(i + 1, null);
                        }
                    } else {
                        statement.setObject(i + 1, object);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateSql(String sql, Object[] objects) {
        PreparedStatement statement = null;
        int result = 0;
        try {

            statement = getStatement(sql);
            setParamObject(objects, statement);
            result = statement.executeUpdate();
            log.info("执行sql为[" + sql + "]参数为" + Arrays.toString(objects));
            statement.clearParameters();

        } catch (Exception ignored) {
        } finally {
            close();
        }

        return result;
    }

    public int updateSql(String sql, List<Object> objectList) {
        return updateSql(sql, objectList.toArray());
    }

    public int updateSql(String sql) {
        return updateSql(sql, (Object[]) null);
    }


    // 新增通用方法


    /**
     * 新增一条数据
     *
     * @param object 新增对象    对象类型
     * @param flag   true时，使用数据库自增主键。false时，使用数据内的id值
     */
    public <T> int insertObject(T object, String idName, String tableName, Boolean flag) {
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
                Object value = declaredField.get(object);
                if (value != null) {
                    insertColsName.append(fieldName).append(",");
                    objectList.add(value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + ") ");

        StringBuilder values = new StringBuilder(" VALUES (");
        for (Object ignored : objectList) {
            values.append("?,");
        }
        values = new StringBuilder(values.substring(0, values.length() - 1) + ") ");

        String sql = "insert into " + tableName + insertColsName + values;

        Object[] objects = objectList.toArray(new Object[0]);
        return updateSql(sql, objects);
    }

    public int insertObjectByMap(String tableName, Map<String, Object> objectMap) {
        StringBuilder insertColsName = new StringBuilder(" ( ");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> objectList = new ArrayList<>();
        for (String colName : objectMap.keySet()) {
            insertColsName.append(StringUtils.camelToUnderline(colName)).append(",");
            values.append("?,");
            objectList.add(objectMap.get(colName));
        }

        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + ") ");
        String sql = "insert into " + tableName + insertColsName + values;
        return updateSql(sql, objectList);
    }

    /**
     * 新增多条数据
     *
     * @param objectList 新增对象列表
     * @param flag       true时，使用数据库自增主键。false时，使用数据内的id值
     */
    public int insertObjectList(List objectList, String idName, String tableName, Boolean flag) {
        List<Object[]> list = new ArrayList<>();
        String sql = null;
        for (Object object : objectList) {
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
                } catch (IllegalAccessException ignore) {
                }
            }
            if (sql == null) {
                insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + ") ");

                StringBuilder values = new StringBuilder("VALUES (");
                for (Object ignored : parameterObjectList) {
                    values.append("?,");
                }

                sql = "insert into " + tableName + insertColsName + values.substring(0, values.length() - 1) + ")";
            }
            list.add(parameterObjectList.toArray(new Object[0]));
        }


        return batchExecuteSql(sql, list);
    }


    // 删除通用方法


    /**
     * 根据id删除数据
     *
     * @param objectId  用户id
     * @param idName    主键id名称    id名称
     * @param tableName 表名 表名
     * @return 删除的数量
     */
    public int deletedObjectById(Object objectId, String idName, String tableName) {
        return updateSql("delete from " + StringUtils.camelToUnderline(tableName) + " where " + StringUtils.camelToUnderline(idName) + " = ?",
                new Object[]{objectId});
    }

    /**
     * @param objectIdList 主键id列表
     * @param idName       主键id名称
     * @param tableName    表名
     * @return 删除的数量
     */
    public int deletedObjectByIdList(List objectIdList, String idName, String tableName) {
        if (objectIdList == null || objectIdList.isEmpty()) {
            throw new ServiceException("objectIdList参数不允许为空");
        }
        StringBuilder sql = new StringBuilder("delete from " + StringUtils.camelToUnderline(tableName) + " where  " + StringUtils.camelToUnderline(idName) + " in (");
        for (Object objectId : objectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        String string = sql.toString();
        return updateSql(string);
    }

    /**
     * @param tableName 表名
     * @param objectMap 删除条件集合
     * @return 删除数量
     */
    public int deletedObjectByMap(String tableName, Map<String, Object> objectMap) {
        if (ObjectUtils.isNull(objectMap) || objectMap.isEmpty()) {
            throw new ServiceException("objectMap参数不允许为空");
        }
        StringBuilder sql = new StringBuilder("delete from " + StringUtils.camelToUnderline(tableName) + " where 1=1 ");
        List<Object> objectList = new ArrayList<>();
        for (String colName : objectMap.keySet()) {
            sql.append(" and ").append(colName).append(" = ? ");
            objectList.add(objectMap.get(colName));
        }
        return updateSql(String.valueOf(sql), objectList);
    }


    //修改通用方法


    /**
     * 根据主键更新对应的数据
     *
     * @param Object    更新的对象
     * @param idName    主键id名称
     * @param tableName 表名
     * @return 删除数量
     */
    public int updateObjectById(Object Object, String idName, String tableName) {
        List<Object> objectList = new ArrayList<>();
        String sql = getSqlUpdate(Object, objectList, idName, tableName);
        Object[] objects = objectList.toArray(new Object[0]);
        return updateSql(sql, objects);
    }

    /**
     * @param objectLists 更新列表
     * @param idName      主键id名称
     * @param tableName   表名
     * @return 更新数量
     */
    public int updateObjectList(List objectLists, String idName, String tableName) {
        if (objectLists == null || objectLists.isEmpty()) {
            throw new ServiceException("参数为空");
        }
        List<Object[]> list = new ArrayList<>();
        String sql = "";
        for (Object t : objectLists) {
            List<Object> objectList = new ArrayList<>();
            String updateColsName = getSqlUpdate(t, objectList, idName, tableName);
            Object[] objects = objectList.toArray(new Object[0]);
            list.add(objects);
            if (sql.isEmpty()) {
                sql = updateColsName;
            }
        }
        return batchExecuteSql(sql, list);
    }


    // 查询方法


    /**
     * @param type      返回类型
     * @param tableName 表名
     * @return 查询结果
     */
    public List<Object> selectObjectListAll(Class<?> type, String tableName) {
        String sql = getBaseSelectSql(tableName);
        List<Map<String, Object>> maps = queryForList(sql);
        return getObjectList(maps, type);
    }


    /**
     * @param tableName   表名
     * @param type        返回的类型
     * @param colName     查询字段名
     * @param valueObject 条件内的值
     * @return 查询结果
     */
    public Object selectObjectByColName(String tableName, String colNames, Class<?> type, String colName, Object valueObject) {
        String sql = getBaseSelectSql(tableName, colNames) + " and " + StringUtils.camelToUnderline(colName) + "= ?";
        Map<String, Object> objectMap = queryFoObject(sql, new Object[]{valueObject});
        return getObject(objectMap, type);
    }

    /**
     * @param tableName   表名
     * @param type        返回的类型
     * @param colName     条件字段名
     * @param valueObject 条件的值
     * @return 返回值
     */
    public Object selectObjectByColName(String tableName, Class<?> type, String colName, Object valueObject) {
        return selectObjectByColName(tableName, null, type, colName, valueObject);
    }


    /**
     * @param tableName   表名
     * @param type        返回类型
     * @param colName     条件字段名
     * @param valueObject 条件的值
     * @return 查询结果
     */
    public List selectObjectListByColName(String tableName, String colNames, Class<?> type, String colName, Object valueObject) {
        String sql = getBaseSelectSql(tableName, colNames) + " and " + StringUtils.camelToUnderline(colName) + "= ?";
        List<Map<String, Object>> maps = queryForList(sql, new Object[]{valueObject});
        return getObjectList(maps, type);
    }

    /**
     * @param tableName   表名
     * @param type        返回类型
     * @param colName     条件字段名
     * @param valueObject 条件的值
     * @return 查询的结果集合
     */
    public List selectObjectListByColName(String tableName, Class<?> type, String colName, Object valueObject) {
        return selectObjectListByColName(tableName, null, type, colName, valueObject);
    }

    /**
     * @param tableName    表名
     * @param idName       主键id名称
     * @param type         返回值的类型
     * @param ObjectIdList 主键id列表
     * @return 结果集合
     */
    public List selectObjectListByObjectIdList(String tableName, String idName, String colNames, Class<?> type, List<Long> ObjectIdList) {
        StringBuilder sql = new StringBuilder(getBaseSelectSql(tableName, colNames) + " and " + StringUtils.camelToUnderline(idName) + " in (");
        for (Long objectId : ObjectIdList) {
            sql.append(objectId).append(",");
        }
        sql = new StringBuilder(sql.substring(0, sql.length() - 1) + ")");
        List<Map<String, Object>> maps = queryForList(sql.toString());
        return getObjectList(maps, type);
    }

    public List selectObjectListByObjectIdList(String tableName, String idName, Class<?> type, List<Long> ObjectIdList) {
        return selectObjectListByObjectIdList(tableName, idName, null, type, ObjectIdList);
    }

    /**
     * @param tableName 表名
     * @param type      返回值的类型
     * @param paramMap  条件集合
     * @return 查询结果
     */
    public Object selectObjectByColMap(String tableName, String colNames, Class<?> type, Map<String, Object> paramMap) {
        StringBuilder sql = new StringBuilder(getBaseSelectSql(tableName, colNames));
        List<Object> objectList = new ArrayList<>();
        for (String colName : paramMap.keySet()) {
            sql.append(" and ").append(StringUtils.camelToUnderline(colName)).append("= ?");
            objectList.add(paramMap.get(colName));
        }
        Map<String, Object> objectMap = queryFoObject(sql.toString(), objectList.toArray());
        return getObject(objectMap, type);
    }

    public Object selectObjectByColMap(String tableName, Class<?> type, Map<String, Object> paramMap) {
        return selectObjectByColMap(tableName, null, type, paramMap);
    }


    /**
     * @param tableName 表名
     * @param type      返回类型
     * @param paramMap  参数集合
     * @return 查询结果集合
     */
    public <T> List<T> selectObjectListByColMap(String tableName, Class<?> type, Map<String, Object> paramMap) {
        return selectObjectListByColMap(tableName, null, type, paramMap);
    }


    /**
     * @param tableName 表名
     * @param colNames  查询字段名
     * @param type      返回类型
     * @param paramMap  参数集合
     * @return 查询结果集合
     */
    public <T> List<T> selectObjectListByColMap(String tableName, String colNames, Class<?> type, Map<String, Object> paramMap) {
        List<Object> objectList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(getBaseSelectSql(tableName, colNames));
        for (String colName : paramMap.keySet()) {
            sql.append(StringUtils.camelToUnderline(colName)).append("= ?");
            objectList.add(paramMap.get(colName));
        }
        List<Map<String, Object>> mapList = queryForList(sql.toString(), objectList.toArray());
        return getObjectList(mapList, type);
    }

    public Long getAutoIncrementValue(String tableName) {
        String schema = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));

        return getAutoIncrementValue(tableName, schema);
    }

    public Long getAutoIncrementValue(String tableName, String schemaName) {
        Object[] objects = {schemaName, tableName};
        String sql = "SELECT AUTO_INCREMENT\n" +
                "FROM information_schema.TABLES\n" +
                "WHERE TABLE_NAME = ?\n";
        objects = new Object[]{tableName};
        if (schemaName != null) {
            sql += "  and TABLE_SCHEMA = ?";
            objects = new Object[]{tableName, schemaName};
        }
        Map<String, Object> map = queryFoObject(sql, objects);
        if (map != null) {
           return ((BigInteger)map.getOrDefault("AUTO_INCREMENT", 1L)).longValue();
        }
        return 1L;
    }


    // 生成sql和将查询结果Map转换成为实际实体类的方法


    /**
     * @param tableName 表名
     * @return 基础查询sql
     */
    public String getBaseSelectSql(String tableName) {
        return "select * from " + StringUtils.camelToUnderline(tableName) + " where 1 = 1 ";
    }


    /**
     * @param tableName 表名
     * @param colNames  查询字段名
     * @return 基础查询sql
     */
    public String getBaseSelectSql(String tableName, String colNames) {
        if (StringUtils.strIsEmpty(colNames)) {
            colNames = "*";
        }

        return "select " + colNames + " from " + StringUtils.camelToUnderline(tableName) + " where 1 = 1 ";
    }


    /**
     * 获取更新语句的sql,同时根据objectList的引用更新需要修改的值
     *
     * @param t          泛型类
     * @param objectList 传递的Object对象list        具体的类
     */
    private <T> String getSqlUpdate(T t, List<Object> objectList, String idName, String tableName) {
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
            } catch (IllegalAccessException ignore) {
            }
        }
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            if (idName.equals(fieldName)) {
                try {
                    objectList.add(declaredField.get(t));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        insertColsName = new StringBuilder(insertColsName.substring(0, insertColsName.length() - 1) + " where " + StringUtils.camelToUnderline(idName) + "=?;");
        return insertColsName.toString();
    }


    /**
     * @param map  对象属性和值的集合
     * @param type 返回类型
     * @return 查询结果
     */
    public static Object getObject(Map<String, Object> map, Class<?> type) {
        if (map == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parse(JSON.toJSONString(map));
        return getObject(jsonObject, type);
    }


    /**
     * 根据传递的jdbcTemplate查询结果list，构建泛型List结果集进行返回
     *
     * @param maps jdbcTemplate查询结果list  泛型
     * @return 构建泛型List结果集进行返回
     */
    public static List getObjectList(List<Map<String, Object>> maps, Class<?> type) {
        List list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            try {
                Object object = getObject(map, type);
                if (object != null)
                    list.add(object);
            } catch (Exception ignore) {

            }
        }
        return list;
    }


    /**
     * @param jsonObject 对象属性
     * @param type       返回类型
     * @return 查询结果
     */
    public static Object getObject(JSONObject jsonObject, Class<?> type) {
        Object object;

        try {
            object = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(type.getName() + "类所包含的没有无参构造的方法，请添加之后在运行");
        }

        Field[] typeFields = type.getDeclaredFields();
        for (Field field : typeFields) {
            String underline = StringUtils.camelToUnderline(field.getName());
            if (jsonObject.containsKey(field.getName()) || jsonObject.containsKey(underline)) {
                field.setAccessible(true);
                try {
                    Class<?> fieldType = field.getType();
                    if (fieldType == Long.class) {
                        Long id = Long.parseLong(jsonObject.get(underline) + "");
                        field.set(object, id);
                    } else if (fieldType == String.class) {
                        field.set(object, jsonObject.get(underline) + "");
                    } else if (fieldType == Date.class) {
                        Date date = Date.valueOf((String) jsonObject.get(underline));
                        field.set(object, date);
                    } else if (fieldType == Time.class) {
                        Time date = Time.valueOf((String) jsonObject.get(underline));
                        field.set(object, date);
                    } else if (fieldType == Timestamp.class) {
                        Timestamp date = Timestamp.valueOf((String) jsonObject.get(underline));
                        field.set(object, date);
                    } else if (fieldType == Float.class) {
                        Float data = Float.valueOf((String) jsonObject.get(underline));
                        field.set(object, data);
                    } else if (fieldType == HashSet.class) {
                        String objectString = jsonObject.getString(underline);
                        HashSet<String> hashSet = new HashSet<>(Arrays.asList(objectString.split("，")));
                        field.set(object, hashSet);

                    } else {
                        Object date = jsonObject.get(underline);
                        field.set(object, date);
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("在转换类型为：" + type.getName() + "，属性名为：" + field.getName() + "时出现转换出现异常错误，错误消息为：" + e.getMessage());
                }

            }
        }
        return object;
    }

}