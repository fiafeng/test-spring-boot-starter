package com.fiafeng.common.pojo.Vo;

import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;

import java.util.List;

public interface IBaseUserInfo extends IBasePojo {

    String getUuid();

    Long getLoginTime();

    Long getExpireTime();

    IBaseUser getUser();

    List<String> getPermissionList();

    List<String> getRoleList();


    void setUuid(String uuid);

    void setLoginTime(Long loginTime);

    void setExpireTime(Long expireTime);

    void setUser(IBaseUser user);

    void setPermissionList(List<String> permissionList);

    void setRoleList(List<String> roleList);
}
