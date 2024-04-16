package com.fiafeng.mapping.properties;

import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("fiafeng.mapping")
@Data
@Component
public class FiafengMappingProperties implements IEnableProperties {

    /**
     * 是否开启mapping功能
     */
    public Boolean enable = false;


    /**
     * 是否需要添加基于mapping的拦截器
     */
    public Boolean permissionInterceptorEnable = true;


    /**
     * 拦截器白名单,默认使用${fiafeng.security.permitAllList}的配置
     */
    public List<String> interceptor = new ArrayList<>();
}
