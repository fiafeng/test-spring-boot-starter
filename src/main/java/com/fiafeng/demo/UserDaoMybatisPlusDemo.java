package com.fiafeng.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.mybatis.pojo.MybatisDefaultUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDaoMybatisPlusDemo extends BaseMapper<MybatisDefaultUser>, IUserMapper {

    default <T extends IBaseUser> boolean insertUser(T user) {
        return insert((MybatisDefaultUser) user) == 1;
    }

    default <T extends IBaseUser> boolean updateUser(T user) {


        return updateById((MybatisDefaultUser) user) == 1;
    }

    default boolean deletedUserByUserId(Long userId) {

        return deleteById(userId) == 1;
    }

    default <T extends IBaseUser> List<T> selectUserListAll() {
        QueryWrapper<MybatisDefaultUser> queryWrapper = new QueryWrapper<>();
        List<MybatisDefaultUser> iBaseUsers = selectList(queryWrapper);

        return (List<T>) iBaseUsers;
    }

    default <T extends IBaseUser> T selectUserByUserName(String username) {
        QueryWrapper<MybatisDefaultUser> queryWrapper = new QueryWrapper<>();
        IBaseUser baseUser = selectOne(queryWrapper);
        return (T) baseUser;
    }

    default <T extends IBaseUser> T selectUserByUserId(Long userId) {
        IBaseUser baseUser = selectById(userId);
        return (T) baseUser;
    }
}
