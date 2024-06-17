package com.fiafeng.common.controller;


import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.captcha.annotation.UseCaptchaAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.service.IUserService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.common.service.ILoginService;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.pojo.Vo.IBaseUserInfo;
import com.fiafeng.common.properties.FiafengTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@BeanDefinitionOrderAnnotation()
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
    public AjaxResult register(JSONObject jsonObject) {
        IBaseUser bean = FiafengSpringUtils.getBean(IBaseUser.class);
        IBaseUser iBaseUser = jsonObject.toJavaObject(bean.getClass());
        userService.insertUser(iBaseUser);
        return AjaxResult.success("注册成功");
    }

    @PostMapping("/login")
    @UseCaptchaAnnotation
    public AjaxResult login(@RequestBody JSONObject jsonObject) {
        IBaseUser bean = FiafengSpringUtils.getBean(IBaseUser.class);
        IBaseUser iBaseUser = jsonObject.toJavaObject(bean.getClass());
        String token = loginService.login(iBaseUser.getUsername(), iBaseUser.getPassword());
        AjaxResult ajax = AjaxResult.success();
        ajax.put(tokenProperties.getToken(), token);
        return ajax;
    }

    @GetMapping("/getInfo")
    public AjaxResult getUserInfo() {
        IBaseUserInfo loginUser = tokenService.getLoginUser();
        loginUser.getUser().setPassword(null);
//        loginUser.getUser().setId(null);
        return AjaxResult.success(loginUser.getUser());
    }

    @PostMapping("/logout")
    public AjaxResult logout() {
        loginService.logout();
        return AjaxResult.success("注销成功");
    }
}
