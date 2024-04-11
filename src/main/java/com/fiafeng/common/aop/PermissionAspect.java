package com.fiafeng.common.aop;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.SecurityUtils;
import com.fiafeng.rbac.annotation.HasPermission;
import com.fiafeng.rbac.annotation.HasRole;
import com.fiafeng.security.service.IUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/11
 * @description
 */
@Aspect
@Slf4j
public class PermissionAspect {

    @Autowired
    ICacheService cacheService;

    @Autowired
    ITokenService tokenService;


    @Before(value = "@annotation(com.fiafeng.rbac.annotation.HasPermission)")
    public void PermissionCheck(JoinPoint joinPoint) {
        IUserDetails loginUserInfo = SecurityUtils.getLoginUserInfo();

        String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUserInfo.getUuid();
        JSONObject jsonObject = cacheService.getCacheObject(userKey);
        IUserDetails cacheObj = JSONObject.parseObject(jsonObject.toJSONString(), IUserDetails.class);

        List<String> permissionList = cacheObj.getPermissionList();
        if (permissionList.contains("admin")) {
            return;
        }

        // 获取注解所在的目标方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 获取方法上注解
        HasPermission annotation = method.getAnnotation(HasPermission.class);
        String[] needPermissionArray = annotation.value();
        for (String string : needPermissionArray) {
            if (string.contains(":*")) {
                String reg = string.substring(0, string.indexOf(":*"));
                if (reg.equals(string.substring(0, string.indexOf(":")))) {
                    return;
                }
            }
            if (permissionList.contains(string)) {
                return;
            }
        }
        throw new ServiceException("抱歉，您没有权限。请联系管理员授权", 401);
    }

    @Before(value = "@annotation(com.fiafeng.rbac.annotation.HasRole)")
    public void RoleCheck(JoinPoint joinPoint) {
        Authentication authentication = SecurityUtils.getAuthentication();
        IUserDetails loginUserInfo = (IUserDetails) authentication.getPrincipal();
        List<String> roleList = loginUserInfo.getRoleList();
        if (roleList.contains("admin")) {
            return;
        }

        // 获取注解所在的目标方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 获取方法上注解
        HasRole annotation = method.getAnnotation(HasRole.class);
        String[] needRoleArray = annotation.value();
        for (String string : needRoleArray) {
            if (roleList.contains(string)) {
                return;
            }
        }
        throw new ServiceException("抱歉，您没有权限。请联系管理员授权", 403);
    }

}
