package com.fiafeng.security.mapper;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.mysql.mapper.BaseMysqlMapper;
import com.fiafeng.mysql.properties.FiafengMysqlUserProperties;
import com.fiafeng.common.mapper.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@BeanDefinitionOrderAnnotation(4)
public class DefaultSecurityMysqlUserMapper extends BaseMysqlMapper implements IUserMapper {


    @Autowired
    FiafengMysqlUserProperties userProperties;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Value("${fiafeng.mysqlTable.user.table-name:base_user}")
    public void setTableName(String tableName) {
        this.tableName = userProperties.getTableName();
    }

    @Override
    @Value("${fiafeng.mysqlTable.user.id-name:id}")
    public void setIdName(String idName) {
        super.setIdName(idName);
    }

    @Override
    @Value("${fiafeng.mysqlTable.user.table-col-name:username}")
    public void setTableColName(String tableColName) {
        super.setTableColName(tableColName);
    }


    @Override
    public boolean insertUser(IBaseUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return insertObject(user);
    }

    @Override
    public boolean updateUser(IBaseUser user) {
        if (!user.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return updateObject(user);
    }

    @Override
    public boolean deletedUser(Long userId) {
        return deletedObjectById(userId);
    }

    @Override
    public <T extends IBaseUser> List<T> selectUserListAll() {
        List<IBaseUser> objectList = selectObjectListAll();
        for (IBaseUser baseUser : objectList) {
            baseUser.setPassword(bCryptPasswordEncoder.encode(baseUser.getPassword()));
        }
        return (List<T>) objectList;
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserName(String userName) {
        IBaseUser baseUser = selectObjectByObjectName(userName);
        baseUser.setPassword(bCryptPasswordEncoder.encode(baseUser.getPassword()));
        return (T) baseUser;
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserId(Long userId) {

        IBaseUser baseUser = selectObjectByObjectId(userId);
        baseUser.setPassword(bCryptPasswordEncoder.encode(baseUser.getPassword()));
        return (T) baseUser;
    }
}
