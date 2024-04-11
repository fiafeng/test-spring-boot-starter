package com.fiafeng.captcha.annotation;


import com.fiafeng.captcha.config.CaptchaConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        CaptchaConfig.class,
})
@Inherited
@ConditionalOnWebApplication
public @interface EnableFiafengCaptchaConfig {
}
