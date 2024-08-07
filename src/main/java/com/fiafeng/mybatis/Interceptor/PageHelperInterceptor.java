package com.fiafeng.mybatis.Interceptor;

import com.github.pagehelper.PageHelper;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Getter
@ConditionalOnClass(PageHelper.class)
//@Component
public class PageHelperInterceptor implements HandlerInterceptor {


    ThreadLocal<HttpServletRequest>  requestThreadLocal= new ThreadLocal<>();


    /*** 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之前）*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestThreadLocal.set(request);
        return true;
    }

    /*** 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /*** 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）*/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}
