package com.fiafeng.rbac.config;


import com.fiafeng.rbac.init.RBACApplicationInit;
import com.fiafeng.rbac.mapper.*;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import com.fiafeng.rbac.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@EnableConfigurationProperties({
        FiafengRbacProperties.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.rbac.controller")
})
@ConditionalOnWebApplication
public class RbacConfig {

    @Bean
    DefaultPermissionsServiceImpl defaultPermissionsService(){
        return new DefaultPermissionsServiceImpl();
    }

    @Bean
    DefaultRolePermissionServiceImpl defaultRolePermissionService(){
        return new DefaultRolePermissionServiceImpl();
    }

    @Bean
    DefaultRoleServiceImpl defaultRoleService(){
        return new DefaultRoleServiceImpl();
    }

    @Bean
    DefaultUserRoleServiceImpl defaultUserRoleService(){
        return new DefaultUserRoleServiceImpl();
    }

    @Bean
    DefaultUserServiceImpl defaultUserService(){
        return new DefaultUserServiceImpl();
    }


    @Bean
    DefaultPermissionMapper defaultPermissionMapper(){
        return new DefaultPermissionMapper();
    }

    @Bean
    DefaultRoleMapper defaultRoleMapper(){
        return new DefaultRoleMapper();
    }

    @Bean
    DefaultRolePermissionMapper defaultRolePermissionMapper(){
        return new DefaultRolePermissionMapper();
    }

    @Bean
    DefaultUserMapper defaultUserMapper(){
        return new DefaultUserMapper();
    }

    @Bean
    DefaultUserRoleMapper defaultUserRoleMapper(){
        return new DefaultUserRoleMapper();
    }

    @Bean
    RBACApplicationInit rbacApplicationInit(){
        return new RBACApplicationInit();
    }


}
