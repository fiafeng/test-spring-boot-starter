package com.fiafeng.comment.config;


import com.fiafeng.comment.properties.FiafengCommentProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@ComponentScans({
        @ComponentScan("com.fiafeng.comment")
})
@EnableConfigurationProperties({
        FiafengCommentProperties.class
})
@MapperScans({
        @MapperScan("com.fiafeng.comment.dao")
})
@ConditionalOnClass({SqlSessionFactoryBean.class})
public class CommentConfig {
}
