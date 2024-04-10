package com.fiafeng.blog.pojo;


import com.alibaba.fastjson2.annotation.JSONField;
import com.fiafeng.blog.annotation.BaseBlogAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import lombok.Data;

import java.sql.Date;

@BaseBlogAnnotation
@Data
@BeanDefinitionOrderAnnotation
public class DefaultBlog {

    private Long id;
    private Long userId;

    private String title;

    private String content;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

}
