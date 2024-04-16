package com.fiafeng.common.filter;

import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


/**
 * 用户更新过滤器
 */
@Component
public class RefreshUserFilter  extends OncePerRequestFilter {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Autowired
    IUserRoleService userRoleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            // 从请求中获取用户信息
            IBaseUserInfo loginUser = tokenService.getLoginUser();
            // 从缓存服务中获取需要更新的用户列表
            HashSet<Long> updateUserInfoList = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
            // 判断当前用户列表是否在列表内
            if (updateUserInfoList != null && updateUserInfoList.contains(loginUser.getUser().getId())) {
                updateUserInfoList.remove(loginUser.getUser().getId());
                // 刷新权限和角色列表
                loginUser.setPermissionList(userRoleService.queryUserPermissionNameListByUserId(loginUser.getUser().getId()));
                loginUser.setRoleList(userRoleService.queryUserRoleNameListByUserId(loginUser.getUser().getId()));
                tokenService.refreshToken(loginUser);
                long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
                cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, updateUserInfoList, expire, TimeUnit.MILLISECONDS);
            }
        } catch (ServiceException ignored) {
        }


        chain.doFilter(request, response);
    }
}
