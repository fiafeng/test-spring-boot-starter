package com.fiafeng.blog.annotation;


import com.fiafeng.blog.config.BlogConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        BlogConfig.class,
})
@Inherited
@ConditionalOnWebApplication
public @interface EnableFiafengBlogConfig {
}
