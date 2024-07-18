package com.fiafeng.test;

import com.fiafeng.comment.service.Impl.mybatis.CommentMybatisServiceImpl;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.demo.TestSpringBootStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SpringBootTest(classes = {TestSpringBootStarterApplication.class})
//@SpringBootTest
class TestSpringBootStarterApplicationTests {


    @Autowired
    IUserMapper mapper;

    @Autowired
    CommentMybatisServiceImpl commentService;

    public static String commentObjectType = "视频";
    public static String commentObjectId = "4";


    public static void main(String[] args) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -2);
        System.out.println(calendar.get(Calendar.DATE));
    }

    @Test
    void contextLoads() {
        IBaseUser baseUser = mapper.selectUserByUserId(1L);
        System.out.println();
    }

}
