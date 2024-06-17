package com.fiafeng.blog.config;

import com.fiafeng.blog.init.BlogApplicationInit;
import com.fiafeng.blog.mapper.DefaultMysqlBlogMapper;
import com.fiafeng.blog.pojo.DefaultBlog;
import com.fiafeng.blog.properties.FiafengBlogProperties;
import com.fiafeng.blog.properties.FiafengMysqlBlogProperties;
import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
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
    @ConditionalOnClassList(name = {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"})
    @ConditionalEnableProperty("fiafeng.mysql.enable")
    DefaultMysqlBlogMapper defaultMysqlBlogMapper(FiafengMysqlBlogProperties properties){
        DefaultMysqlBlogMapper mapper = new DefaultMysqlBlogMapper();
        mapper.setProperties(properties);
        return mapper;
    }

    @Bean
    DefaultBlog defaultBlog(){
        return new DefaultBlog();
    }

    @Bean
    BlogApplicationInit blogApplicationInit(){
        return new BlogApplicationInit();
    }}

