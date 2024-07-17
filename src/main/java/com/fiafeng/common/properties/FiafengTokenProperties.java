package com.fiafeng.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.token")
@Data
public class FiafengTokenProperties {

    /**
     * 安全密钥加密字符串
     */
    public String secret = "abcdefghijklmnopqrstuvwxyz";


    /**
     * token 值里面的前缀值
     */
    public String token_prefix = "Bearer ";


    /**
     * 前后端约定的token在headers里面的字段名称,默认Authorization
     */
    public String header = "Authorization";

    /**
     * 登录成功后，token值在数据中的字段名称，默认token
     */
    public String token = "token";


    /**
     * token过期时间，单位:分钟.默认60分钟
     */
    public Long expireTime = 60L * 24;
}
