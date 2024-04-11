package com.fiafeng.common.config.bean;


import com.fiafeng.rbac.pojo.*;
import com.fiafeng.common.pojo.DefaultUserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;


public class PojoBeanConfig {

    @Bean
    @Scope("prototype")
    public DefaultPermission defaultPermission(){
        DefaultPermission defaultPermission = new DefaultPermission();
        return defaultPermission;
    }

    @Bean
    @Scope("prototype")
    public DefaultRole defaultRole(){
        DefaultRole defaultRole = new DefaultRole();
        return defaultRole;
    }

    @Bean
    @Scope("prototype")
    public DefaultRolePermission defaultRolePermission(){
        DefaultRolePermission defaultRolePermission = new DefaultRolePermission();
        return defaultRolePermission;
    }

    @Bean
    @Scope("prototype")
    public DefaultUser defaultUser(){
        DefaultUser defaultUser = new DefaultUser();
        return defaultUser;
    }

    @Bean
    @Scope("prototype")
    public DefaultUserRole defaultUserRole(){
        DefaultUserRole defaultUserRole = new DefaultUserRole();
        return defaultUserRole;
    }

    @Bean
    @Scope("prototype")
    DefaultUserInfo defaultUserInfo(){
        DefaultUserInfo defaultUserInfo = new DefaultUserInfo();
        return defaultUserInfo;
    }

}
