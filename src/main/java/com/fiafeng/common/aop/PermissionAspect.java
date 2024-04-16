package com.fiafeng.common.aop;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.Enum.LogicEnum;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.SecurityUtils;
import com.fiafeng.common.annotation.HasPermissionAnnotation;
import com.fiafeng.common.annotation.HasRoleAnnotation;
import com.fiafeng.common.properties.FiafengRbacProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/11
 * @description
 */
@Aspect
@Slf4j
@Component
@ConditionalOnClass(Aspect.class)
@ConditionalOnProperty( prefix = "fiafeng.rbac" ,value = "permission-aop-enable", havingValue = "true")
public class PermissionAspect {

    @Autowired
    ICacheService cacheService;

    @Autowired
    ITokenService tokenService;

    @Autowired
    FiafengRbacProperties rbacProperties;

    private static final ThreadLocal<Boolean> localVariable = new ThreadLocal<>();

    @Before(value = "@annotation(com.fiafeng.common.annotation.HasPermissionAnnotation)")
    public void PermissionCheck(JoinPoint joinPoint) {
        IBaseUserInfo loginUserInfo = SecurityUtils.getLoginUserInfo();

        String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUserInfo.getUuid();
        JSONObject jsonObject = cacheService.getCacheObject(userKey);
        IBaseUserInfo cacheObj = JSONObject.parseObject(jsonObject.toJSONString(), IBaseUserInfo.class);

        List<String> permissionList = cacheObj.getPermissionList();
        if (permissionList.contains(rbacProperties.permissionAdminName)) {
            localVariable.set(true);
            return;
        }

        // 获取注解所在的目标方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 获取方法上注解
        HasPermissionAnnotation permissionAnnotation = method.getAnnotation(HasPermissionAnnotation.class);
        String[] needPermissionArray = permissionAnnotation.value();
        for (String string : needPermissionArray) {
            if (string.contains(":*")) {
                String reg = string.substring(0, string.indexOf(":*"));
                if (string.startsWith(reg)) {

                    localVariable.set(true);
                    return;
                }
            }
            if (permissionList.contains(string)) {
                localVariable.set(true);
                return;
            }
        }

        HasRoleAnnotation roleAnnotation = method.getAnnotation(HasRoleAnnotation.class);
        if (roleAnnotation != null && roleAnnotation.logic().equals(LogicEnum.or)) {
            if (localVariable.get() == null) {
                localVariable.set(true);
                return;
            }
        }


        throw new ServiceException("抱歉，您没有权限。请联系管理员授权", 401);
    }

    @Before(value = "@annotation(com.fiafeng.common.annotation.HasRoleAnnotation)")
    public void RoleCheck(JoinPoint joinPoint) {
        Authentication authentication = SecurityUtils.getAuthentication();
        IBaseUserInfo loginUserInfo = (IBaseUserInfo) authentication.getPrincipal();
        List<String> roleList = loginUserInfo.getRoleList();
        if (roleList.contains(rbacProperties.roleAdminName)) {
            localVariable.set(true);
            return;
        }

        // 获取注解所在的目标方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 获取方法上注解
        HasRoleAnnotation roleAnnotation = method.getAnnotation(HasRoleAnnotation.class);
        String[] needRoleArray = roleAnnotation.value();
        for (String string : needRoleArray) {
            if (roleList.contains(string)) {
                localVariable.set(true);
                return;
            }
        }

        if (method.getAnnotation(HasPermissionAnnotation.class) != null
                && roleAnnotation.logic().equals(LogicEnum.or)
                && localVariable.get() == null) {
            localVariable.set(true);
            return;
        }

        throw new ServiceException("抱歉，您没有权限。请联系管理员授权", 403);
    }

}
