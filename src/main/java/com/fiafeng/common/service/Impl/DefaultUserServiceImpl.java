package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */
@Service
@BeanDefinitionOrderAnnotation()
public class DefaultUserServiceImpl implements IUserService {


    @Autowired
    IUserMapper userMapper;

    @Override
    @Nullable
    public  IBaseUser selectUserByUserName(String username) {
        return userMapper.selectUserByUserName(username);
    }

    @Override
    @Nullable
    public  IBaseUser selectUserByUserId(Long userId) throws ServiceException{
        return userMapper.selectUserByUserId(userId);
    }

    @Override
    public int isExistUserId(@NonNull Long userId) {
        return userMapper.selectUserByUserId(userId) == null ? 0 : 1;
    }

    @Override
    public  int insertUser(IBaseUser user) {

        IBaseUser defaultUser = userMapper.selectUserByUserName(user.getUsername());
        if (defaultUser != null){
            throw new ServiceException("新增用户时，用户名重复了");
        }
        user.setPassword(user.getPassword());

        return userMapper.insertUser(user);
    }

    @Override
    public  int updateUser(IBaseUser user) {
        if (selectUserByUserId(user.getId()) == null){
            throw new ServiceException("更新用户信息时,根据Id没有找到对应的用户信息");
        }

        return userMapper.updateUser(user);
    }

    @Override
    public int deletedUser(Long userId) {
        return userMapper.deletedUserByUserId(userId);
    }

    @Override
    public  List<IBaseUser> selectUserListAll() {
        return userMapper.selectUserListAll();
    }

    @Override
    public List<IBaseUser> selectUserListAll(IBaseUser baseUser) {
        return null;
    }
}
