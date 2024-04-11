package com.fiafeng.rbac.properties;


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
     * 是否启用aop  HasPermission HasRole注解
     */
    public Boolean permissionAop = false;

}
