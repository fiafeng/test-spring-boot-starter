package com.fiafeng.rbac.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IUserMapper;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.rbac.properties.FiafengRbacProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@BeanDefinitionOrderAnnotation()
public class DefaultUserMapper implements IUserMapper {

    ConcurrentHashMap<Long, IBaseUser> userMap;

    AtomicLong atomicLong = new AtomicLong(2);

    @Autowired
    FiafengRbacProperties rbacProperties;


    private ConcurrentHashMap<Long, IBaseUser> getUserMap() {
        if (userMap == null){
            userMap = new ConcurrentHashMap<>();

            IBaseUser defaultUser = SpringUtils.getBean(IBaseUser.class);
            defaultUser.setId(1L);
            defaultUser.setUsername(rbacProperties.defaultUserName);
            defaultUser.setPassword(rbacProperties.defaultUserPassword);
            userMap.put(defaultUser.getId(), defaultUser);
        }
        return userMap;
    }


    @Override
    public boolean insertUser(IBaseUser user) {
        if (user == null) {
            return false;
        }
        getUserMap().put(atomicLong.getAndIncrement(), user);
        return true;
    }

    @Override
    public  boolean updateUser(IBaseUser user) {
        if (user.getId() == null) {
            return false;
        }
        if (!getUserMap().containsKey(user.getId())) {
            return false;
        } else {
            getUserMap().put(user.getId(), user);
        }

        return true;
    }

    @Override
    public boolean deletedUser(Long userId) {
        return getUserMap().remove(userId) != null;
    }

    @Override
    public <T extends IBaseUser> List<T> selectUserListAll() {
        List<IBaseUser> baseUserList = new ArrayList<>();
        for (Map.Entry<Long, IBaseUser> entry : getUserMap().entrySet()) {
            baseUserList.add(JSONObject.from(entry.getValue()).toJavaObject(IBaseUser.class));
        }
        return (List<T>) baseUserList;
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            return null;
        }
        for (Map.Entry<Long, IBaseUser> entry : getUserMap().entrySet()) {
            if (userName.equals(entry.getValue().getUsername())){
                return (T) JSONObject.from(entry.getValue()).toJavaObject(IBaseUser.class);
            }
        }
        return null;
    }

    @Override
    public <T extends IBaseUser> T selectUserByUserId(Long userId) {
        if (getUserMap().containsKey(userId)) {
            return (T) JSONObject.from(getUserMap().get(userId)).toJavaObject(IBaseUser.class);
        }

        return null;
    }
}
