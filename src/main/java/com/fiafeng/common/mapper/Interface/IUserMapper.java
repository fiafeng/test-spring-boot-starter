package com.fiafeng.common.mapper.Interface;

import com.fiafeng.common.pojo.Interface.IBaseUser;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface IUserMapper extends IMapper {



     int insertUser(IBaseUser user);

     int updateUser(IBaseUser user);

     int deletedUserByUserId(Long userId);

     List<IBaseUser> selectUserListAll();

//     List<IBaseUser> selectUserListAll(IBaseUser baseUser);

     IBaseUser selectUserByUserName(String username);

     IBaseUser selectUserByUserId(Long userId);

}
