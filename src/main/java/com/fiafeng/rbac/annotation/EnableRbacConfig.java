package com.fiafeng.rbac.annotation;


import com.fiafeng.common.config.CommonConfig;
import com.fiafeng.common.config.bean.PojoBeanConfig;
import com.fiafeng.common.config.bean.WebBeanConfig;
import com.fiafeng.i18n.config.I18nConfig;
import com.fiafeng.mysql.config.MysqlMapperConfig;
import com.fiafeng.rbac.config.RbacConfig;
import com.fiafeng.common.config.DefaultConfig;
import com.fiafeng.security.config.SecurityConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        RbacConfig.class,
        DefaultConfig.class,
        I18nConfig.class,
        DefaultConfig.class,
        PojoBeanConfig.class,
        WebBeanConfig.class,
        SecurityConfig.class,
        MysqlMapperConfig.class,
        CommonConfig.class
})
public @interface EnableRbacConfig {
}
