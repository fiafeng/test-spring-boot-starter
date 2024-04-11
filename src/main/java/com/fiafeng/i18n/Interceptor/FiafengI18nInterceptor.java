package com.fiafeng.i18n.Interceptor;

import com.fiafeng.i18n.properties.FiafengI18NProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
public class FiafengI18nInterceptor implements HandlerInterceptor {

    @Autowired
    FiafengI18NProperties i18nProperties;

    /*** 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之前）*/
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (i18nProperties.enable){
            LocaleContextHolder.setLocale(i18nProperties.locale);
            return true;
        }
        Locale locale;
        try {;
            String key = request.getHeader(i18nProperties.language);// 前端传递的language必须是zh-CN格式的，中间的-必须要完整，不能只传递zh或en

            locale = new Locale(key.split("-")[0], key.split("-")[1]);// 这样赋值以后，FiafengMessageUtils.message方法就不用修改了
//            log.info("当前语言={}", key);
            LocaleContextHolder.setLocale(locale);
        }catch (Exception e){
            LocaleContextHolder.setLocale(i18nProperties.locale);
        }

        return true;
    }

    /*** 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /*** 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）*/
    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}