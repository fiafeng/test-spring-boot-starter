package com.fiafeng.comment.properties;


import com.fiafeng.common.properties.IEnableProperties;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("fiafeng.comment")
@Data
public class FiafengCommentProperties  implements IEnableProperties {

    @Autowired
    FiafengSecurityProperties securityProperties;

    /**
     * 是否开启mapping功能
     */
    public Boolean enable = false;

    public List<String> whitelist = new ArrayList<>();


    public void setWhitelist(List<String> whitelist) {
        securityProperties.setPermitAllList(whitelist);
        securityProperties.setPermitAllList(this.whitelist);
        this.whitelist = securityProperties.permitAllList;
    }
}
