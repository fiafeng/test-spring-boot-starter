package com.fiafeng.mybatis.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fiafeng.common.mapper.Interface.IUserMapper;
import com.fiafeng.common.pojo.DefaultUser;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserProperties;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
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
        FiafengMysqlUserProperties mysqlUserProperties = FiafengSpringUtils.getBean(FiafengMysqlUserProperties.class);
        queryWrapper.eq(mysqlUserProperties.getTableColName(), username);
        IBaseUser baseUser = selectOne(queryWrapper);
        return (T) baseUser;
    }

    default <T extends IBaseUser> T selectUserByUserId(Long userId) {
        IBaseUser baseUser = selectById(userId);
        return (T) baseUser;
    }
}
