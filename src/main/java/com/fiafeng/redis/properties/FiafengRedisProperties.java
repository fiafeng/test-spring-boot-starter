package com.fiafeng.redis.properties;

import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.redis")
@Data
public class FiafengRedisProperties implements IEnableProperties {

    /**
     * 是否开启redis配置
     */
    public Boolean enable = true;


    /**
     * 重新set是否需要更新有效期
     */
    public Boolean expire = true;

}
