package com.fiafeng.security.properties;

import com.fiafeng.common.properties.IProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;


@Data
@ConfigurationProperties("fiafeng.security")
public class FiafengSecurityProperties implements IProperties {

    public Boolean enable = true;

    /**
     * security框架允许匿名通过白名单
     */
    public List<String> permitAllList = Arrays.asList("/login/**", "/register", "/captchaImage");

}
