package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBaseUser;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

public interface IUserService {


    boolean isExistUserId(Long userId);

    <T extends IBaseUser> boolean insertUser(T user);

    <T extends IBaseUser> boolean updateUser(T user);

    boolean deletedUser(Long userId);

    <T extends IBaseUser> List<T> selectUserListAll();

    <T extends IBaseUser> T selectUserByUserName(String username);

    <T extends IBaseUser> T selectUserByUserId(Long userId);
}
