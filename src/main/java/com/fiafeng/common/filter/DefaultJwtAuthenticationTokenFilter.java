package com.fiafeng.common.filter;

import com.alibaba.fastjson2.JSON;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.mvc.ServletUtils;
import com.fiafeng.security.properties.FiafengSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

@Component
@BeanDefinitionOrderAnnotation()
public class DefaultJwtAuthenticationTokenFilter extends OncePerRequestFilter implements IJwtAuthenticationTokenFilter {

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

        IBaseUserInfo loginUser;
        try {
            // 从请求中获取用户信息
            loginUser = tokenService.getLoginUser();
            tokenService.verifyToken(loginUser);
        } catch (ServiceException e) {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(e.getCode(), e.getMessage())));
            return;
        } catch (Exception e){
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error("遇到意外的错误=》" +e.getMessage())));
            return;
        }

        chain.doFilter(request, response);
    }
}
