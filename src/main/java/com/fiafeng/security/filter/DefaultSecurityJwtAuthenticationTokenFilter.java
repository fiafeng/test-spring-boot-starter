package com.fiafeng.security.filter;

import com.alibaba.fastjson2.JSON;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.filter.IJwtAuthenticationTokenFilter;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.security.service.IUserDetails;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.SecurityUtils;
import com.fiafeng.common.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

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


@BeanDefinitionOrderAnnotation(3)
public class DefaultSecurityJwtAuthenticationTokenFilter extends OncePerRequestFilter implements IJwtAuthenticationTokenFilter {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        IBaseUserInfo loginUser;
        try {
            // 从请求中获取用户信息
            loginUser = tokenService.getLoginUser();
        } catch (ServiceException e) {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(e.getMessage(), e.getCode())));
            return;
        }
        // 判断当前用户是否是需要更新缓存的用户
        HashSet<Long> updateUserInfoList = cacheService.getCacheObject(CacheConstants.UPDATE_USER_INFO);
        if (updateUserInfoList != null && updateUserInfoList.contains(loginUser.getUser().getId())) {
            tokenService.refreshToken(loginUser);
            updateUserInfoList.remove(loginUser.getUser().getId());
            long expire = cacheService.getExpire(CacheConstants.UPDATE_USER_INFO);
            cacheService.setCacheObject(CacheConstants.UPDATE_USER_INFO, updateUserInfoList, expire, TimeUnit.MILLISECONDS);
        }


        // 从请求中获取用户信息
        IUserDetails userDetails = tokenService.getLoginUser();

        // 校验token
        if (ObjectUtils.isNotNull(userDetails) && ObjectUtils.isNull(SecurityUtils.getAuthentication())) {
            tokenService.verifyToken(userDetails);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        try {

            chain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(e.getMessage())));
                return;
            } else if (e instanceof NestedServletException) {
                Throwable cause = e.getCause();
                if (cause instanceof ServiceException) {
                    ServiceException serviceException = (ServiceException) cause;
                    ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(serviceException.getCode(),serviceException.getMessage())));
                    return;
                }
            }
            throw e;
        }
    }
}
