package com.fiafeng.common.utils.mvc;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Fiafeng
 * @create 2023/12/26
 * @description
 */
public class HttpServletUtils {

    public static HttpServletRequest getHttpServletRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }


}
