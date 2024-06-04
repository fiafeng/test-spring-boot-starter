package com.fiafeng.security.filter;

import com.alibaba.fastjson2.JSON;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.filter.IJwtAuthenticationTokenFilter;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import com.fiafeng.security.service.IUserDetails;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.SecurityUtils;
import com.fiafeng.common.utils.mvc.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 *
 * @author ruoyi
 */


@BeanDefinitionOrderAnnotation(value = ModelConstant.secondOrdered)
@Component
public class DefaultSecurityJwtAuthenticationTokenFilter extends OncePerRequestFilter implements IJwtAuthenticationTokenFilter {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    ICacheService cacheService;

    @Autowired
    FiafengSecurityProperties fiafengSecurityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/login") || requestURI.startsWith("/captchaImage") || requestURI.startsWith("/register")){
            chain.doFilter(request, response);
            return;
        }
        for (String string : fiafengSecurityProperties.permitAllList) {
            if (requestURI.startsWith(string.replace("/**",""))){
                chain.doFilter(request, response);
                return;
            }
        }


        IUserDetails userDetails;
        try {
            // 从请求中获取用户信息
            // 从请求中获取用户信息
            userDetails = tokenService.getLoginUser();
        } catch (ServiceException e) {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(e.getCode(), e.getMessage())));
            return;
        }


        // 校验token
        if (ObjectUtils.isNotNull(userDetails) && ObjectUtils.isNull(SecurityUtils.getAuthentication())) {
            tokenService.verifyToken(userDetails);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(403, "获取token失败")));
            return;
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
                    ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(serviceException.getCode(), serviceException.getMessage())));
                    return;
                }
            }
            throw e;
        }

    }
}
