package com.fiafeng.test;

import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.service.Impl.ConnectionPoolServiceImpl;
import com.fiafeng.demo.TestSpringBootStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest(classes = {TestSpringBootStarterApplication.class})
//@SpringBootTest
class TestSpringBootStarterApplicationTests {


    @Autowired
    IUserMapper mapper;


    @Autowired
    ConnectionPoolServiceImpl connectionPoolService;


    public static void main(String[] args) {
    }

    @Test
    void contextLoads() throws SQLException {

    }

}
