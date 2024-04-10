package com.fiafeng.blog.properties;

import com.fiafeng.common.properties.IProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.blog")
@Data
public class FiafengBlogProperties implements IProperties {

    /**
     * 是否开启博客功能
     */
    Boolean enable = false;

}
