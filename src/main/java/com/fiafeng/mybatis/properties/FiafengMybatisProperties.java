package com.fiafeng.mybatis.properties;


import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.mybatis")
@Data
public class FiafengMybatisProperties implements IEnableProperties {

    /**
     * 是否开启mybatis支持
     */
    Boolean enable = true;


    /**
     * 是否开启逻辑删除
     */
    Boolean tombstone = true;


    /**
     * 开启逻辑删除时，逻辑字段
     */
    String tombstoneFieldName = "deleted";
}
