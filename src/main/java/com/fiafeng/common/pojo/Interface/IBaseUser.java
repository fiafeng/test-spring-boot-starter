package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.annotation.BaseUserAnnotation;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

@BaseUserAnnotation
public interface IBaseUser {

    String getUsername();

    String getPassword();

    Long getId();

    IBaseUser setUsername(String username);

    IBaseUser setPassword(String password);

    IBaseUser setId(Long id);


}
