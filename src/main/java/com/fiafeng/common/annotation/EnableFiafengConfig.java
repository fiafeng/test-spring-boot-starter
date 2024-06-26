package com.fiafeng.common.annotation;


import com.fiafeng.blog.annotation.EnableFiafengBlogConfig;
import com.fiafeng.captcha.config.CaptchaConfig;
import com.fiafeng.comment.config.CommentConfig;
import com.fiafeng.common.config.CommonConfig;
import com.fiafeng.dynamicClass.config.DynamicClassConfig;
import com.fiafeng.flowable.config.FlowableConfig;
import com.fiafeng.mapping.config.MappingAutoConfig;
import com.fiafeng.mybatis.config.MyBatisConfig;
import com.fiafeng.redis.config.RedisConfig;
import com.fiafeng.validation.config.ValidationConfig;
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
        CommonConfig.class,
        CommentConfig.class,
        ValidationConfig.class,
        DynamicClassConfig.class,
        FlowableConfig.class
})
@Inherited
@ConditionalOnWebApplication
@EnableFiafengBlogConfig
public @interface EnableFiafengConfig {
}
