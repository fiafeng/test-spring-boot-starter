package com.fiafeng.captcha.controller;

import com.fiafeng.captcha.service.impl.CaptchaServiceImpl;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.controller.ILoginController;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.properties.FiafengTokenProperties;
import com.fiafeng.security.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController

@BeanDefinitionOrderAnnotation(2)
public class CaptchaLoginController implements ILoginController {

    @Autowired
    ILoginService loginService;

    @Autowired
    CaptchaServiceImpl captchaService;

    @Autowired
    FiafengTokenProperties tokenProperties;


    @PostMapping("/login")
    public AjaxResult login(@RequestBody HashMap<String, String> hashMap) {
        if (!hashMap.containsKey("username") || !hashMap.containsKey("password")) {
            throw new ServiceException("参数传递错误！");
        }
        captchaService.checkCaptchaByHashMap(hashMap);
        String username = hashMap.get("username");
        String password = hashMap.get("password");

        AjaxResult ajaxResult = AjaxResult.success();
        String token = loginService.login(username, password);
        ajaxResult.put(tokenProperties.getToken(), token);
        return ajaxResult;
    }


    @PostMapping("/logout")
    public AjaxResult logout() {
        loginService.logout();
        return AjaxResult.success("注销成功");
    }
}
