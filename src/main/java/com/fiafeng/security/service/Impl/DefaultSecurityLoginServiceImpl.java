package com.fiafeng.security.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.FiafengMessageUtils;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.service.ILoginService;
import com.fiafeng.security.service.IUserDetails;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.SecurityUtils;
import com.fiafeng.common.utils.spring.AuthenticationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

@Slf4j
@BeanDefinitionOrderAnnotation(3)
public class DefaultSecurityLoginServiceImpl implements ILoginService {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Resource
    private AuthenticationManager authenticationManager;


    public String login(String username, String password) {

        // 用户验证
        Authentication authentication;
        // 获取用户的权限列表
        List<String> roleList = new ArrayList<>();

        // 通过用户的权限列表生成授权列表
        List<GrantedAuthority> authoritieList = roleList.stream().
                map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password, authoritieList);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                // 运行到这里，有两种情况，第一：数据库里面的密码没有加密，导致输入正确密码也匹配不上。
                // 第二 用户或者密码不正确
                throw new ServiceException(FiafengMessageUtils.message("user.password.not.match"));
            } else {
                log.error(e.getMessage());
                throw e;
            }
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        IUserDetails loginUser = (IUserDetails) authentication.getPrincipal();

        return tokenService.createToken(loginUser);
    }

    @Override
    public boolean logout() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken)
                        SecurityUtils.getAuthentication();
        IUserDetails loginUser = (IUserDetails) authentication.getPrincipal();
        tokenService.removeToken(loginUser);
        return true;
    }


}
