package com.fiafeng.mybatis.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.DefaultUser;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDaoMybatisPlusDemo extends BaseMapper<DefaultUser>, IUserMapper {

    default <T extends IBaseUser> int insertUser(T user) {
        return insert((DefaultUser) user);
    }

    default <T extends IBaseUser> int updateUser(T user) {


        return updateById((DefaultUser) user);
    }

    default int deletedUserByUserId(Long userId) {

        return deleteById(userId);
    }

    default <T extends IBaseUser> List<T> selectUserListAll() {
        QueryWrapper<DefaultUser> queryWrapper = new QueryWrapper<>();
        return (List<T>) selectList(queryWrapper);
    }

    default <T extends IBaseUser> T selectUserByUserName(String username) {
        QueryWrapper<DefaultUser> queryWrapper = new QueryWrapper<>();
        IBaseUser baseUser = selectOne(queryWrapper);
        return (T) baseUser;
    }

    default <T extends IBaseUser> T selectUserByUserId(Long userId) {
        IBaseUser baseUser = selectById(userId);
        return (T) baseUser;
    }
}
