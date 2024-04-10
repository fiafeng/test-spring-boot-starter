package com.fiafeng.mysql.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mysql-table.role-permission")
@Data
public class FiafengMysqlRolePermissionProperties {

    /**
     * 主键id的字段名称
     */
    public String idName = "id";

    /**
     * 表名
     */
    public String tableName = "base_role_permission";

    /**
     * 关系表中权限id的名字
     */
    public String permissionIdName = "permissionId";

    /**
     * 关系表中角色id的名字
     */
    public String roleIdName = "roleId";

}
