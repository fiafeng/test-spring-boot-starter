package com.fiafeng.common.mapper;

import java.util.List;

import com.fiafeng.common.pojo.Interface.IBaseUser;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IUserMapper {



    boolean insertUser(IBaseUser user);

     boolean updateUser(IBaseUser user);

    boolean deletedUser(Long userId);

    <T extends IBaseUser> List<T> selectUserListAll();

    <T extends IBaseUser> T selectUserByUserName(String username);

    <T extends IBaseUser> T selectUserByUserId(Long userId);

}
