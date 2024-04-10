package com.fiafeng.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mysql-table.role")
@Data
public class FiafengMysqlRoleProperties {

    /**
     * 主键id的字段名称
     */
    public String idName = "id";

    /**
     * 表名
     */
    public String tableName = "base_role";

    /**
     * 名称字段的名字
     */
    public String tableColName = "name";


}
