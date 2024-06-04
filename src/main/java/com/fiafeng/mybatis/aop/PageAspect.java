package com.fiafeng.mybatis.aop;

import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.Interceptor.PageHelperInterceptor;
import com.fiafeng.mybatis.annotation.PageAnnotation;
import com.fiafeng.mybatis.properties.FiafengMybatisPageProperties;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Fiafeng
 * @create 2023/12/11
 * @description
 */
@Aspect
@Slf4j
@Component
@ConditionalOnClass(value = {Aspect.class, PageHelper.class})
public class PageAspect {

    @Autowired
    FiafengMybatisPageProperties pageProperties;


    @Before(value = "@annotation(com.fiafeng.mybatis.annotation.PageAnnotation)")
    public void pageHelperSet(JoinPoint joinPoint) {

        PageHelperInterceptor pageHelperInterceptor;
        try {

            pageHelperInterceptor = FiafengSpringUtils.getBean(PageHelperInterceptor.class);
        } catch (Exception e) {
            return;
        }


        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PageAnnotation pageAnnotation = method.getAnnotation(PageAnnotation.class);
        int page = pageAnnotation.page() == -1 ? pageProperties.getPage() : pageAnnotation.page();
        int pageSize = pageAnnotation.pageSize() == -1 ? pageProperties.getPageSize() : pageAnnotation.pageSize();
        String suffix = pageAnnotation.suffix();
        HttpServletRequest request = pageHelperInterceptor.getRequestThreadLocal().get();
        if (request == null) {
            return;
        }

        Map<String, String[]> parameterMap = request.getParameterMap();
        String pageName = pageProperties.getPageName();
        String pageSizeName = pageProperties.getPageSizeName();
        if (StringUtils.strNotEmpty(suffix)) {
            pageName += suffix;
            pageSizeName += suffix;
        }
        if (parameterMap.containsKey(pageSizeName)) {
            pageSize = Integer.parseInt(request.getParameter(pageSizeName));
        }
        if (parameterMap.containsKey(pageName)) {
            page = Integer.parseInt(request.getParameter(pageName));
        }

        if (pageSize != 0) {
            PageHelper.startPage(page, pageSize);
        } else {
            PageHelper.clearPage();
        }
    }

}
