package com.fiafeng.common.annotation;


import com.fiafeng.captcha.config.CaptchaConfig;
import com.fiafeng.common.config.CommonConfig;
import com.fiafeng.i18n.config.I18nConfig;
import com.fiafeng.mapping.config.MappingConfig;
import com.fiafeng.mybatis.config.MyBatisConfig;
import com.fiafeng.mysql.config.MysqlMapperConfig;
import com.fiafeng.rbac.config.RbacConfig;
import com.fiafeng.redis.config.RedisConfig;
import com.fiafeng.security.config.SecurityConfig;
import com.fiafeng.common.config.DefaultConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        RbacConfig.class,
        DefaultConfig.class,
        MappingConfig.class,
        MysqlMapperConfig.class,
        MyBatisConfig.class,
        SecurityConfig.class,
        I18nConfig.class,
        RedisConfig.class,
        CaptchaConfig.class,
        CommonConfig.class
})
@ConditionalOnWebApplication
public @interface EnableFiafengConfig {
}
