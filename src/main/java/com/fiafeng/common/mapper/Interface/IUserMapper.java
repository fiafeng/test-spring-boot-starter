package com.fiafeng.common.mapper.Interface;

import java.util.List;

import com.fiafeng.common.pojo.Interface.IBaseUser;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IUserMapper extends IMapper {



    <T extends IBaseUser> int insertUser(T user);

    <T extends IBaseUser> int updateUser(T user);

    <T extends IBaseUser> int deletedUserByUserId(Long userId);

    <T extends IBaseUser> List<T> selectUserListAll();

    <T extends IBaseUser> T selectUserByUserName(String username);

    <T extends IBaseUser> T selectUserByUserId(Long userId);

}
