package com.fiafeng.redis.properties;

import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.redis")
@Data
public class FiafengRedisProperties implements IEnableProperties {

    public Boolean enable = true;
}
