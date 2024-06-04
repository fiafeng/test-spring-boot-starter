package com.fiafeng.mybatis.properties;


import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mybatis.page")
@Data
public class FiafengMybatisPageProperties implements IEnableProperties {

    /**
     * 是否开启mybatis支持
     */
    Boolean enable = false;

    /**
     * 默认页码
     */
    Integer page = 1;

    /**
     * 默认返回数据大小
     */
    Integer pageSize = 100;

    /**
     * 请求参数中，page参数的默认名字
     */
    String pageName = "page";


    /**
     * 请求参数中，pageSize参数的默认名字
     */
    String pageSizeName = "pageSize";
}
