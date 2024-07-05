package com.fiafeng.blog.properties;

import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.blog.mysql")
@Data
public class FiafengMysqlBlogProperties implements IMysqlTableProperties {


    /**
     * 博客表的表名
     */
    public String tableName = "base_blog";
    /**
     * 博客表朱建民
     */
    public String idName = "id";

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
