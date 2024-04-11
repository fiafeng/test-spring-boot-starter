package com.fiafeng.blog.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.blog.mysql")
@Data
public class FiafengMysqlBlogProperties {


    /**
     * 博客表的表名
     */
    public String tableName = "base_blog";

    /**
     * 博客关系表
     */
    public String baseBlogUserTableName = "base_blog_user";

    /**
     * 博客表中，和用户表关联的id名称
     */
    public String userIdName = "userId";

    /**
     * 博客表关系
     */
    public String blogIdName = "blogId";
}
