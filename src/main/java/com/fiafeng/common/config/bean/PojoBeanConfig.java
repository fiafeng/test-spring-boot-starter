package com.fiafeng.common.config.bean;


import com.fiafeng.rbac.pojo.*;
import com.fiafeng.common.pojo.DefaultUserInfo;
import org.springframework.context.annotation.Bean;


public class PojoBeanConfig {

    @Bean
    public DefaultPermission defaultPermission(){
        DefaultPermission defaultPermission = new DefaultPermission();
        return defaultPermission;
    }

    @Bean
    public DefaultRole defaultRole(){
        DefaultRole defaultRole = new DefaultRole();
        return defaultRole;
    }

    @Bean
    public DefaultRolePermission defaultRolePermission(){
        DefaultRolePermission defaultRolePermission = new DefaultRolePermission();
        return defaultRolePermission;
    }

    @Bean
    public DefaultUser defaultUser(){
        DefaultUser defaultUser = new DefaultUser();
        return defaultUser;
    }

    @Bean
    public DefaultUserRole defaultUserRole(){
        DefaultUserRole defaultUserRole = new DefaultUserRole();
        return defaultUserRole;
    }

    @Bean
    DefaultUserInfo defaultUserInfo(){
        DefaultUserInfo defaultUserInfo = new DefaultUserInfo();
        return defaultUserInfo;
    }

}
