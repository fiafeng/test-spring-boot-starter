package com.fiafeng.common.config;

import com.fiafeng.common.init.CommonApplicationInit;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

public class CommonConfig {

    @Bean
    @Order()
    CommonApplicationInit commonApplicationInit(){
        return new CommonApplicationInit();
    }
}
