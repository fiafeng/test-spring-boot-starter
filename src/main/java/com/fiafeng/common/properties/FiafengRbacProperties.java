package com.fiafeng.common.properties;


import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fiafeng.rbac")
public class FiafengRbacProperties implements IEnableProperties {

    /**
     * 是否开启默认rbac权限控制
     */
    public Boolean enable = true;

    /**
     * 一个用户是否允许拥有多个角色
     */
    public Boolean allowHasRoles = false;


    /**
     * 是否启用aop  HasPermissionAnnotation HasRole注解
     */
    public Boolean permissionAopEnable = false;


    /**
     * 管理员角色的角色名
     */
    public String roleAdminName = "admin";

    /**
     * 管理员权限的权限名
     */
    public String permissionAdminName = "admin";


    /**
     * 默认用户的用户名
     */
    public String defaultUserName = "admin";

    /**
     * 默认用户的密码
     */
    public String defaultUserPassword =  "123456";

}
