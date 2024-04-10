package com.fiafeng.captcha.controller;


import com.fiafeng.captcha.properties.FiafengCaptchaProperties;
import com.fiafeng.captcha.service.impl.CaptchaServiceImpl;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.utils.IdUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnClass(Producer.class)
@BeanDefinitionOrderAnnotation
public class CaptchaProducerController {


    @Autowired
    CaptchaServiceImpl captchaService;

    @Autowired
    FiafengCaptchaProperties captchaProperties;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode() {
        AjaxResult ajaxResult = AjaxResult.success();
        String uuid = IdUtils.simpleUUID();
        String captchaByBase64 = captchaService.getCaptchaByBase64(uuid);
        ajaxResult.put(captchaProperties.uuid, uuid);
        ajaxResult.put(captchaProperties.captchaName, captchaByBase64);
        return ajaxResult;
    }
}
