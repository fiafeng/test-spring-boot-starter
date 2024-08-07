package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.*;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.spring.FiafengMessageUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@BeanDefinitionOrderAnnotation()
@Primary
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
        IBaseUserInfo iBaseUserInfo = FiafengSpringUtils.getBean(IBaseUserInfo.class);
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
