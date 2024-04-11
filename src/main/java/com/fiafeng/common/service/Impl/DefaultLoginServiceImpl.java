package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.service.IUserRoleService;
import com.fiafeng.common.service.IUserService;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.service.ILoginService;
import com.fiafeng.common.utils.FiafengMessageUtils;
import com.fiafeng.common.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j

@BeanDefinitionOrderAnnotation(1)
public class DefaultLoginServiceImpl implements ILoginService {

    @Autowired
    ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Autowired
    IUserService userService;

    @Autowired
    IUserRoleService userRoleService;

    @Override
    public String login(String username, String password) {
        IBaseUser user = userService.selectUserByUserName(username);
        if (ObjectUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("用户名或者密码不存在");
        }
        if (!user.getPassword().equals(password)){
            throw new ServiceException(FiafengMessageUtils.message("user.password.not.match"));
        }
        IBaseUserInfo iBaseUserInfo = SpringUtils.getBean(IBaseUserInfo.class);
        iBaseUserInfo.setUser(user);
        iBaseUserInfo.setPermissionList(userRoleService.queryUserPermissionNameListByUserId(user.getId()));
        iBaseUserInfo.setRoleList(userRoleService.queryUserRoleNameListByUserId(user.getId()));
        return tokenService.createToken(iBaseUserInfo);
    }

    @Override
    public boolean logout() {
        IBaseUserInfo loginUser = tokenService.getLoginUser();
        tokenService.refreshToken(loginUser);
        return false;
    }

}
