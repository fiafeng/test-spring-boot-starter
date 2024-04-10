package com.fiafeng.common.filter;

import com.alibaba.fastjson2.JSON;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.utils.ServletUtils;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */

@BeanDefinitionOrderAnnotation(1)
public class DefaultJwtAuthenticationTokenFilter extends OncePerRequestFilter implements IJwtAuthenticationTokenFilter {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/login") || requestURI.startsWith("/captchaImage") || requestURI.startsWith("/register")){
            chain.doFilter(request, response);
            return;
        }

        IBaseUserInfo loginUser;
        try {
            // 从请求中获取用户信息
            loginUser = tokenService.getLoginUser();
        } catch (ServiceException e) {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(e.getCode(), e.getMessage())));
            return;
        }

        tokenService.verifyToken(loginUser);
        // 判断当前用户是否是需要更新缓存的用户
        HashSet<Long> updateUserInfoList = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        if (updateUserInfoList != null && updateUserInfoList.contains(loginUser.getUser().getId())) {
            updateUserInfoList.remove(loginUser.getUser().getId());
            tokenService.refreshToken(loginUser);
            long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
            cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, updateUserInfoList, expire, TimeUnit.MILLISECONDS);
        }

        chain.doFilter(request, response);
    }
}