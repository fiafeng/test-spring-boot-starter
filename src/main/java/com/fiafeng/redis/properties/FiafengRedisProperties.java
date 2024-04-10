package com.fiafeng.redis.properties;

import com.fiafeng.common.properties.IProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.redis")
@Data
public class FiafengRedisProperties implements IProperties {

    public Boolean enable = true;
}
