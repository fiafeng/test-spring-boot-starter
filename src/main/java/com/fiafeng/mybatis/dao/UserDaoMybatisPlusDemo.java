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

    default int insertUser(IBaseUser user) {
        DefaultUser defaultUser = (DefaultUser) user;
        return insert(defaultUser);
    }

    default int updateUser(IBaseUser user) {
        return updateById((DefaultUser) user);
    }

    default int deletedUserByUserId(Long userId) {

        return deleteById(userId);
    }

    default List<IBaseUser> selectUserListAll() {
        QueryWrapper<DefaultUser> queryWrapper = new QueryWrapper<>();
        return (List) selectList(queryWrapper);
    }

    default IBaseUser selectUserByUserName(String username) {
        QueryWrapper<DefaultUser> queryWrapper = new QueryWrapper<>();
        FiafengMysqlUserProperties mysqlUserProperties = FiafengSpringUtils.getBean(FiafengMysqlUserProperties.class);
        queryWrapper.eq(mysqlUserProperties.getTableColName(), username);
        return selectOne(queryWrapper);
    }

    default IBaseUser selectUserByUserId(Long userId) {
        return selectById(userId);
    }
}
