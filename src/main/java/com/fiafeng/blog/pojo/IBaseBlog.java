package com.fiafeng.blog.pojo;

import java.sql.Date;

public interface IBaseBlog {

    Long getId();

    void setId(Long id);

    Long getUserId();

    void setUserId(Long userId);

    String getTitle();

    void setTitle(String title);

    String getContent();

    void setContent(String content);

    Date getCreateDate();

    void setCreateDate(Date createDate);

}
