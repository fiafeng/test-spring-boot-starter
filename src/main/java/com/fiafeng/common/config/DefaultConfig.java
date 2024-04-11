package com.fiafeng.common.config;

import com.fiafeng.common.config.bean.PojoBeanConfig;
import com.fiafeng.common.config.bean.WebBeanConfig;
import com.fiafeng.i18n.properties.FiafengI18NProperties;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

//表示这个类为配置类
@ComponentScans({
        @ComponentScan("com.fiafeng.test.controller"),
        @ComponentScan("com.fiafeng.test.handler")
})
@Import({
        PojoBeanConfig.class,
        WebBeanConfig.class
})
@EnableConfigurationProperties({

})
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication
public class DefaultConfig {

}