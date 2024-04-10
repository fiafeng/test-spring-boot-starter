package com.fiafeng.security.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fiafeng.common.annotation.BaseUserInfoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.security.service.IUserDetails;
import com.fiafeng.common.service.IUserRoleService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description 存储用户登录相关信息
 */


@Data
@Accessors(chain = true)
@BaseUserInfoAnnotation
public class DefaultSecurityLoginUserInfo implements Serializable, IUserDetails{

    @Autowired
    IUserRoleService userRoleService;


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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissionList != null && !permissionList.isEmpty())
            return permissionList.stream().
                    map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        else {
            permissionList = userRoleService.queryUserPermissionNameListByUserId(user.getId());
            return permissionList.stream().
                    map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取密码
     *
     */
    @JSONField(serialize = false)
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名
     *
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     *
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     */
    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     *
     */
    @JSONField(serialize = false)
    @Override
    public boolean isEnabled() {
        return true;
    }

}

