package com.fiafeng.common.pojo;


import com.fiafeng.common.annotation.BaseUserInfoAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@BaseUserInfoAnnotation
@BeanDefinitionOrderAnnotation
public class DefaultUserInfo implements IBaseUserInfo {


    /**
     * 用户唯一标识
     */
    private String uuid;


    /**
     * 登录时间
     */
    private Long loginTime;


    /**
     * 过期时间
     */
    private Long expireTime;


    /**
     * 用户信息
     */
    private IBaseUser user;

    /**
     * 用户权限列表
     */
    private List<String> permissionList;

    /**
     * 用户角色列表
     */
    private List<String> roleList;

}
