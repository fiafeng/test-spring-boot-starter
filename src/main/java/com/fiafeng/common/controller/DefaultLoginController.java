package com.fiafeng.common.controller;


import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.captcha.annotation.UseCaptchaAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.service.IUserService;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.common.service.ILoginService;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.properties.FiafengTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@BeanDefinitionOrderAnnotation(1)
public class DefaultLoginController implements ILoginController {

    @Autowired
    ILoginService loginService;

    @Autowired
    FiafengTokenProperties tokenProperties;

    @Autowired
    IUserService userService;

    @Autowired
    ITokenService tokenService;

    @GetMapping("/login/{username}/{password}")
    @UseCaptchaAnnotation
    public AjaxResult login(@PathVariable String username,
                            @PathVariable String password) {
        String token = loginService.login(username, password);
        AjaxResult ajax = AjaxResult.success();
        ajax.put(tokenProperties.getToken(), token);
        return ajax;
    }


    @GetMapping("/register")
    public AjaxResult register(JSONObject jsonObject){
        IBaseUser bean = SpringUtils.getBean(IBaseUser.class);
        IBaseUser iBaseUser = jsonObject.toJavaObject(bean.getClass());
        userService.insertUser(iBaseUser);
        return AjaxResult.success("注册成功");
    }

    @PostMapping("/login")
    @UseCaptchaAnnotation
    public AjaxResult login(@RequestBody JSONObject jsonObject) {
        IBaseUser bean = SpringUtils.getBean(IBaseUser.class);
        IBaseUser iBaseUser = jsonObject.toJavaObject(bean.getClass());
        String token = loginService.login(iBaseUser.getUsername(), iBaseUser.getPassword());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(tokenProperties.getToken(), token);
        return AjaxResult.success(hashMap);
    }

    @GetMapping("/getInfo")
    public AjaxResult getUserInfo(){
        IBaseUserInfo loginUser = tokenService.getLoginUser();
        return AjaxResult.success(loginUser);
    }

    @PostMapping("/logout")
    public AjaxResult logout() {
        loginService.logout();
        return AjaxResult.success("注销成功");
    }
}
