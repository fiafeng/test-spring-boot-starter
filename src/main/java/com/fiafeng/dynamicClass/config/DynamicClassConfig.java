package com.fiafeng.dynamicClass.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;

@ComponentScans({
        @ComponentScan("com.fiafeng.dynamicClass")
})
@EnableConfigurationProperties({
})
@Import({
})
public class DynamicClassConfig {


}
