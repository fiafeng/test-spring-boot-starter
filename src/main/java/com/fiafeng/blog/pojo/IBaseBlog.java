package com.fiafeng.blog.pojo;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

import java.sql.Date;

public interface IBaseBlog extends IBasePojo {


    Long getUserId();

    void setUserId(Long userId);

    String getTitle();

    void setTitle(String title);

    String getContent();

    void setContent(String content);

    Date getCreateDate();

    void setCreateDate(Date createDate);

}
