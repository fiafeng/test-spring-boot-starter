package com.fiafeng.run;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.DefaultUser;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.mybatis.pojo.MybatisUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<MybatisUser>, IUserMapper {

    default <T extends IBaseUser> boolean insertUser(T user){
        return insert((MybatisUser) user) == 1;
    }

    default <T extends IBaseUser> boolean updateUser(T user){


        return updateById((MybatisUser) user) == 1;
    }

    default boolean deletedUser(Long userId){

        return false;
    }

    default  <T extends IBaseUser> List<T> selectUserListAll(){
        QueryWrapper<MybatisUser> queryWrapper = new QueryWrapper<>();
        List<MybatisUser> iBaseUsers = selectList(queryWrapper);

        return (List<T>) iBaseUsers;
    }

    default  <T extends IBaseUser> T selectUserByUserName(String username){
        QueryWrapper<MybatisUser> queryWrapper = new QueryWrapper<>();
        IBaseUser baseUser = selectOne(queryWrapper);
        return (T) baseUser;
    }

    default  <T extends IBaseUser> T selectUserByUserId(Long userId){
        IBaseUser baseUser = selectById(userId);
        return (T) baseUser;
    }
}
