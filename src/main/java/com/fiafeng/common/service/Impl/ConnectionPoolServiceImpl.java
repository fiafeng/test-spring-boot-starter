package com.fiafeng.common.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.config.bean.DefaultDataSource;
import com.fiafeng.common.properties.mysql.FiafengMysqlProperties;
import com.fiafeng.common.utils.FiafengMysqlUtils;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ConnectionPoolServiceImpl {

    DataSource dataSource;

    public static volatile List<Connection> connectionPool = new ArrayList<>();

    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private static final ThreadLocal<Integer> threadLocalInt = new ThreadLocal<>();
    private static final ThreadLocal<Statement> threadLocalStatement = new ThreadLocal<>();

    private static final CopyOnWriteArrayList<Boolean> connectionUser = new CopyOnWriteArrayList<>();

    FiafengMysqlProperties mysqlProperties;

    public ConnectionPoolServiceImpl() {

    }

    public ConnectionPoolServiceImpl(FiafengMysqlProperties mysqlProperties) {
        this.mysqlProperties = mysqlProperties;
    }


    private int maxSize = 10;

    private static boolean flag = false;

    private static volatile AtomicInteger userCount = new AtomicInteger();

    public ConnectionPoolServiceImpl(DataSource dataSource, FiafengMysqlProperties mysqlProperties) {
        this.dataSource = dataSource;
        if (dataSource instanceof DefaultDataSource) {
            flag = true;
        }

        this.mysqlProperties = mysqlProperties;
    }

    public boolean checkTableExist(String tableName) {
        return checkTableExist(ObjectClassUtils.url, tableName);
    }

    public boolean checkTableExist(String url, String tableName) {
        String databaseName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));

        List<Map<String, Object>> columns = queryForList(FiafengMysqlUtils.queryTableExistSql(), new Object[]{tableName, databaseName});
        boolean flag = !columns.isEmpty();
        return flag;
    }

    public void checkMysqlTableIsExist(String tableName, Class<?> typeClass){
        checkMysqlTableIsExist(tableName, ObjectClassUtils.url, typeClass);
    }

    public void checkMysqlTableIsExist(String tableName,String url, Class<?> typeClass) {
        if (!checkTableExist(url)) {
            String createdTableSql = FiafengMysqlUtils.createdTableSql(tableName, typeClass);
            executeSql(createdTableSql);
        }
    }


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
            if (sql != null) {

                statement = connection.prepareStatement(sql);
            }

        } catch (Exception e) {

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


    public List<Map<String, Object>> queryForList(String sql) {
        return queryForList(sql, null);
    }

    public List<Map<String, Object>> queryForList(String sql, Object[] objects) {
        PreparedStatement statement = null;
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            statement = getStatement(sql);
            setObject(objects, statement);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> hashMap = new HashMap<>();
                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    try {
                        Object object = resultSet.getObject(j);
                        String colName = metaData.getColumnName(j);
                        hashMap.put(colName, object);
                    } catch (Exception e) {
                    }
                }
                if (!hashMap.isEmpty()) {
                    list.add(hashMap);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {

            }
            try {
                statement.close();
            } catch (SQLException e) {
            }
            ConnectionPoolServiceImpl.close();
        }
        return list;
    }

    public void executeSql(String sql) {
        executeSql(sql, null);
    }

    public void executeSql(String sql, Object[] objects) {
        PreparedStatement statement = null;
        try {
            statement = getStatement(sql);
            setObject(objects, statement);
            statement.execute();
        } catch (Exception ignored) {
        } finally {
            ConnectionPoolServiceImpl.close();
        }
    }


    public int updateSql(String sql, Object[] objects) {
        PreparedStatement statement = null;
        int result = 0;
        try {

            statement = getStatement(sql);
            setObject(objects, statement);
            result = statement.executeUpdate();
            log.info("执行sql为[" + sql + "]参数为" + Arrays.toString(objects));

        } catch (Exception ignored) {
        } finally {
            ConnectionPoolServiceImpl.close();
        }

        return result;
    }


    public int updateSql(String sql) {
        return updateSql(sql, null);
    }


    public int updateBatchByListSql(String sql, List<Object[]> objectList) {
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
                    setObject(objects, statement);
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
            ConnectionPoolServiceImpl.close();
        }

        return result;
    }

    public int updateBatchByListSql(List<String> sqlList) {
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
            ConnectionPoolServiceImpl.close();
        }

        return result;
    }


    public static void setObject(Object[] objects, PreparedStatement statement) {

        String string = "";
        try {
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    Object object = objects[i];
                    if (object instanceof String) {
                        statement.setString(i + 1, (String) object);
                    } else if (object instanceof Long) {
                        statement.setLong(i + 1, (Long) object);
                    } else if (object instanceof HashSet) {
                        string = "";
                        HashSet<Object> hashSet = (HashSet<Object>) object;
                        for (Object o : hashSet) {
                            string += o.toString() + "，";
                        }
                        if (string.length() > 1) {
                            string = string.substring(0, string.length() - 1);
                            statement.setString(i + 1, string);
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

    public static <T> T getObject(Map<String, Object> map, Class<?> type) {
        JSONObject jsonObject = JSONObject.parse(JSON.toJSONString(map));
        return ConnectionPoolServiceImpl.getObject(jsonObject, type);
    }


    /**
     * 根据传递的jdbcTemplate查询结果list，构建泛型List结果集进行返回
     *
     * @param maps jdbcTemplate查询结果list
     * @param <T>  泛型
     * @return 构建泛型List结果集进行返回
     */
    public static <T> List<T> getObjectList(List<Map<String, Object>> maps, Class<?> type) {
        List list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            try {
                Object object = getObject(map, type);
                if (object != null)
                    list.add(object);
            } catch (Exception e) {

            }
        }
        return list;
    }


    public static <T> T getObject(JSONObject jsonObject, Class<?> type) {
        Object object = null;

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
        return (T) object;
    }

}