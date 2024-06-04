package com.fiafeng.blog.pojo;


import com.alibaba.fastjson2.annotation.JSONField;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import lombok.Data;

import java.sql.Date;

@Data
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class DefaultBlog implements IBaseBlog{

    private Long id;

    private Long userId;

    private String title;

    private String content;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 逻辑删除字段
     */
    private Boolean deleted;

}
