package com.fiafeng.common.config;

import com.fiafeng.common.init.CommonApplicationInit;
import org.springframework.context.annotation.Bean;

public class CommonConfig {

    @Bean
    CommonApplicationInit commonApplicationInit(){
        return new CommonApplicationInit();
    }
}
