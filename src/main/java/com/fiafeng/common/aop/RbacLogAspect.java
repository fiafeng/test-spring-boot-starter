package com.fiafeng.common.aop;


import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@ConditionalOnClass(Aspect.class)
@ConditionalOnProperty( prefix = "fiafeng.rbac" ,value = "enable-log", havingValue = "true")
public class RbacLogAspect {

    @Before(value = "@annotation(com.fiafeng.common.annotation.LogRbacAnnotation)")
    public void rbacLog(JoinPoint joinPoint) {
        IBaseUserInfo loginUserInfo = SecurityUtils.getLoginUserInfo();


    }

}
