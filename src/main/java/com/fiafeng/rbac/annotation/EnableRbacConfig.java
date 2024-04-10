package com.fiafeng.rbac.annotation;


import com.fiafeng.i18n.config.I18nConfig;
import com.fiafeng.rbac.config.RbacConfig;
import com.fiafeng.common.config.DefaultConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        RbacConfig.class,
        DefaultConfig.class,
        I18nConfig.class
})
public @interface EnableRbacConfig {
}
