package com.fiafeng.security.service.Impl;

import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.security.pojo.DefaultSecurityLoginUserInfo;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.IUserRoleService;
import com.fiafeng.common.service.IUserService;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.security.service.IUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */
@Slf4j
public class DefaultUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    IUserService userService;


    @Autowired
    IUserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        IBaseUser user = userService.selectUserByUserName(username);
        if (ObjectUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户名或者密码不存在");
        }
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(IBaseUser user) {
        IUserDetails userDetails = SpringUtils.getBean(IUserDetails.class);
        userDetails.setUser(user)
                .setPermissionList(userRoleService.queryUserPermissionNameListByUserId(user.getId()))
                .setRoleList(new ArrayList<>(userRoleService.queryUserRoleNameListByUserId(user.getId())));
        return userDetails;
    }
}
