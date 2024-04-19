package com.fiafeng.demo;

import com.fiafeng.common.annotation.EnableFiafengConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFiafengConfig
public class TestSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringBootStarterApplication.class, args);
    }

}
