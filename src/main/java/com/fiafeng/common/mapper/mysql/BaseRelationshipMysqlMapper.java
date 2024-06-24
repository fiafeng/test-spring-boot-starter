package com.fiafeng.common.mapper.mysql;

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
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public abstract class BaseRelationshipMysqlMapper {

    @Setter
    public Class<?> type;

    @Setter
    private ConnectionPoolServiceImpl connectionPoolService;

    @Setter
    public IMysqlTableProperties properties;


    @Setter
    @Getter
    private String relationshipOneName = "relationshipOneName";

    @Setter
    @Getter
    public String relationshipTwoName = "relationshipTwoName";


    public String getIdName() {
        return getProperties().getIdName();
    }


    public String getTableName() {
        return getProperties().getTableName();
    }


    /**
     * 检查mysql表是否存在（没有检查属性），如果不存在，则创建
     */
    public void checkMysqlTableIsExist(String url) {
        checkMysqlTableIsExist(url, TypeOrmEnum.intType);
    }


    public void checkMysqlTableIsExist(String url,TypeOrmEnum typeOrmEnum ) {

        if (getConnectionPoolService() == null) {
            setConnectionPoolService(FiafengSpringUtils.getBean(ConnectionPoolServiceImpl.class));
        }

        if (!checkTableExist(url)) {
            createdTable(typeOrmEnum);
        }
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
        for (Object ignored : objectList) {
            values.append("?,");
        }
        values = new StringBuilder(values.substring(0, values.length() - 1) + ")");

        String sql = "insert into " + getTableName() + insertColsName + values;

        Object[] objects = objectList.toArray(new Object[objectList.size()]);
        return getConnectionPoolService().updateSql(sql, objects) == 1;
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


}
