package com.fiafeng.common.pojo.Interface;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

public interface IBaseUser {

    String getUsername();

    String getPassword();

    Long getId();

    IBaseUser setUsername(String username);

    IBaseUser setPassword(String password);

    IBaseUser setId(Long id);


}
