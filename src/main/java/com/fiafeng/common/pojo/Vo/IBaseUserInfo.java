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


    IBaseUserInfo setUuid(String uuid);

    IBaseUserInfo setLoginTime(Long loginTime);

    IBaseUserInfo setExpireTime(Long expireTime);

    IBaseUserInfo setUser(IBaseUser user);

    IBaseUserInfo setPermissionList(List<String> permissionList);

    IBaseUserInfo setRoleList(List<String> roleList);
}
