package com.fiafeng.common.filter;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;

import javax.servlet.Filter;


@BeanDefinitionOrderAnnotation()
public interface IJwtAuthenticationTokenFilter extends Filter {
}
