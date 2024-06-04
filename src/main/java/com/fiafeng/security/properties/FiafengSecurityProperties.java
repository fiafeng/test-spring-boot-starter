package com.fiafeng.security.properties;

import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Data
@ConfigurationProperties("fiafeng.security")
public class FiafengSecurityProperties implements IEnableProperties {

    public Boolean enable = true;

    /**
     * security框架允许匿名通过白名单
     */
    public List<String> permitAllList = Arrays.asList("/login/**", "/register", "/captchaImage");



    public void setPermitAllList(List<String> permitAllList) {
        HashSet<String> hashSet = new HashSet<>(permitAllList);
        hashSet.addAll(this.permitAllList);
        this.permitAllList = new ArrayList<>(hashSet);
    }
}
