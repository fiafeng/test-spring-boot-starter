package com.fiafeng.security.init;

import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.security.service.IUserDetails;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class SecurityApplicationProcessor{

    static {
        ObjectClassUtils.addClass(IUserDetails.class);
    }

}
