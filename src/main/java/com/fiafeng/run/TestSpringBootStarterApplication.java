package com.fiafeng.run;

import com.fiafeng.rbac.annotation.EnableRbacConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRbacConfig
@SpringBootApplication
public class TestSpringBootStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringBootStarterApplication.class, args);
    }

}
