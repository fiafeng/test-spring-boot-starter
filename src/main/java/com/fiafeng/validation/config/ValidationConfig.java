package com.fiafeng.validation.config;

import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.validation.properties.FiafengValidationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;

@ComponentScans({
        @ComponentScan("com.fiafeng.validation")
})
@EnableConfigurationProperties({
        FiafengValidationProperties.class
})
@Import({
})
@ConditionalEnableProperty("fiafeng.validation.enable")
public class ValidationConfig {
}
