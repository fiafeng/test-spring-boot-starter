package com.fiafeng.captcha.aop;


import com.fiafeng.captcha.properties.FiafengCaptchaProperties;
import com.fiafeng.captcha.service.impl.CaptchaServiceImpl;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.utils.HttpServletUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
public class CaptchaLoginAspect {

    @Autowired
    CaptchaServiceImpl captchaService;

    @Autowired
    FiafengCaptchaProperties captchaProperties;

    // 前置通知
    @Before("@annotation(com.fiafeng.captcha.annotation.UseCaptchaAnnotation)")
    public void beforeAdvice() {
        HttpServletRequest request = HttpServletUtils.getHttpServletRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.containsKey(captchaProperties.loginUuid) || !parameterMap.containsKey(captchaProperties.loginCaptchaName)) {
            throw new ServiceException("当前接口开启了验证码，请先获取验证码，然后传递验证码和对应的参数!！");
        }
        String[] uuidArray = parameterMap.get(captchaProperties.loginUuid);
        String[] valueArray = parameterMap.get(captchaProperties.loginCaptchaName);
        String uuid;
        String value;
        if (uuidArray == null || uuidArray.length != 1) {
            throw new ServiceException(captchaProperties.loginUuid + "有多个参数,请检查传递的参数");
        }
        if (valueArray == null || valueArray.length != 1) {
            throw new ServiceException(captchaProperties.loginCaptchaName + "有多个参数,请检查传递的参数");
        }
        uuid = uuidArray[0];
        value = valueArray[0];
        // 检查验证码
        captchaService.checkCaptchaByHashMap(value, uuid);
    }
}
