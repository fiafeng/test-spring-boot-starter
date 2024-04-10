package com.fiafeng.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mysql-table.user-role")
@Data
public class FiafengMysqlUserRoleProperties {

    /**
     * 主键id的字段名称
     */
    public String idName = "id";

    /**
     * 表名
     */
    public String tableName = "base_user_role";

    /**
     * 关系表中角色id的名字
     */
    public String roleIdName = "roleId";

    /**
     * 关系表中用户id的名字
     */
    public String userIdName = "userId";

}
