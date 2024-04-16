package com.fiafeng.mapping.properties;

import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mysql-table.mapping")
@Data
public class FiafengMysqlMappingProperties implements IMysqlTableProperties {

    /**
     * 主键id的字段名称
     */
    public String idName = "id";

    /**
     * 表名
     */
    public String tableName = "base_mapping";

}
