package com.fiafeng.blog.config;

import com.fiafeng.blog.mapper.DefaultMysqlBlogMapper;
import com.fiafeng.blog.pojo.DefaultBlog;
import com.fiafeng.blog.properties.*;
import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@EnableConfigurationProperties({
        FiafengBlogProperties.class,
        FiafengMysqlBlogProperties.class
})
@ComponentScans({
        @ComponentScan("com.fiafeng.blog.controller"),
        @ComponentScan("com.fiafeng.blog.init")
})
@ConditionalEnableProperty(value = "fiafeng.blog.enable")
public class BlogConfig {


    @Bean
    DefaultMysqlBlogMapper defaultMysqlBlogMapper(FiafengMysqlBlogProperties properties){
        DefaultMysqlBlogMapper mapper = new DefaultMysqlBlogMapper();
        mapper.tableName = properties.tableName;
        mapper.userIdName = properties.userIdName;
        return mapper;
    }

    @Bean
    DefaultBlog defaultBlog(){
        return new DefaultBlog();
    }
}

