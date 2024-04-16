package com.fiafeng.common.annotation;


import com.fiafeng.blog.annotation.EnableFiafengBlogConfig;
import com.fiafeng.captcha.config.CaptchaConfig;
import com.fiafeng.common.config.CommonConfig;
import com.fiafeng.mapping.config.MappingAutoConfig;
import com.fiafeng.mybatis.config.MyBatisConfig;
import com.fiafeng.redis.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        MappingAutoConfig.class,
        MyBatisConfig.class,
        RedisConfig.class,
        CaptchaConfig.class,
        CommonConfig.class
})
@Inherited
@ConditionalOnWebApplication
@EnableFiafengBlogConfig
public @interface EnableFiafengConfig {
}
