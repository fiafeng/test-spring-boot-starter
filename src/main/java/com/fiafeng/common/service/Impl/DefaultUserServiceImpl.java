package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
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
    public <T extends IBaseUser> T selectUserByUserName(String username) {
        IBaseUser user = userMapper.selectUserByUserName(username);
        if (user == null){
            throw new ServiceException("用户不存在");
        }

        return (T) user;
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserId(Long userId) {
        IBaseUser user = userMapper.selectUserByUserId(userId);
        if (user == null){
            throw new ServiceException("没有找到该用户");
        }
        return (T) user;
    }

    @Override
    public boolean isExistUserId(@NonNull Long userId) {
        return userMapper.selectUserByUserId(userId) == null;
    }

    @Override
    public <T extends IBaseUser> boolean insertUser(T user) {

        IBaseUser defaultUser = userMapper.selectUserByUserName(user.getUsername());
        if (defaultUser != null){
            throw new ServiceException("新增用户时，用户名重复了");
        }
        user.setPassword(user.getPassword());

        return userMapper.insertUser(user);
    }

    @Override
    public <T extends IBaseUser> boolean updateUser(T user) {
        if (selectUserByUserId(user.getId()) == null){
            throw new ServiceException("没有找到用户");
        }

        return userMapper.updateUser(user);
    }

    @Override
    public boolean deletedUser(Long userId) {
        return userMapper.deletedUserByUserId(userId);
    }

    @Override
    public  List<IBaseUser> selectUserListAll() {
        return userMapper.selectUserListAll();
    }
}
