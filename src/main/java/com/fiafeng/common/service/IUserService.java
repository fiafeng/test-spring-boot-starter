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

    int insertUser(IBaseUser user);

    int updateUser(IBaseUser user);

    int deletedUser(Long userId);

    List<IBaseUser> selectUserListAll();

    List<IBaseUser> selectUserListAll(IBaseUser baseUser);


    @Nullable
    IBaseUser selectUserByUserName(String username);


    @Nullable
    IBaseUser selectUserByUserId(Long userId);
}
