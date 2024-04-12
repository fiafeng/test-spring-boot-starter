package com.fiafeng.common.config;

import com.fiafeng.common.init.CommonApplicationInit;
import com.fiafeng.common.init.CommonApplicationProcess;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

public class CommonConfig {

    @Bean
    CommonApplicationInit commonApplicationInit(){
        return new CommonApplicationInit();
    }


    @Bean
    CommonApplicationProcess commonApplicationProcess(){
        return  new CommonApplicationProcess();
    }
}
