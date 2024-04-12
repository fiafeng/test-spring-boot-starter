package com.fiafeng.security.init;

import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.security.service.IUserDetails;

public class SecurityApplicationProcessor extends ApplicationProcessor {

    static {
        ObjectClassUtils.addRemoveBeanDefinitionByClass(IUserDetails.class);
    }

}
