package com.fiafeng;

import com.fiafeng.common.annotation.EnableFiafengConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFiafengConfig
@SpringBootApplication
public class TestSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringBootStarterApplication.class, args);
    }

}
