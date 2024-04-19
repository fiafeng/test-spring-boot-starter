package com.fiafeng.test;

import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.demo.TestSpringBootStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {TestSpringBootStarterApplication.class})
//@SpringBootTest
class TestSpringBootStarterApplicationTests {



    @Autowired
    IUserMapper mapper;

    @Test
    void contextLoads() {
        List<IBaseUser> iBaseUsers = mapper.selectUserListAll();
        System.out.println(iBaseUsers);

    }
}
