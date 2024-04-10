package com.fiafeng.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */
public class SecurityUtils {


    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }




    public static <T extends UserDetails> T getLoginUserInfo(){
        return (T) getAuthentication().getPrincipal();
    }
}
