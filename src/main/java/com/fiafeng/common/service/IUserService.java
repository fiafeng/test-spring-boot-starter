package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBaseUser;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

public interface IUserService {


    int isExistUserId(Long userId);

    <T extends IBaseUser> int insertUser(T user);

    <T extends IBaseUser> int updateUser(T user);

    int deletedUser(Long userId);

    <T extends IBaseUser> List<T> selectUserListAll();


    @Nullable
    <T extends IBaseUser> T selectUserByUserName(String username);


    @Nullable
    <T extends IBaseUser> T selectUserByUserId(Long userId);
}
