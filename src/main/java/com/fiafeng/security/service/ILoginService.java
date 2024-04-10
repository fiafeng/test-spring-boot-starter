package com.fiafeng.security.service;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface ILoginService {

    String login(String username,String password);

    boolean logout();
}
