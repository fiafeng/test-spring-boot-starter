package com.fiafeng.captcha.service;

public interface ICaptchaService {

    /**
     * 获取Base64字符串类型验证码
     */
    String getCaptchaByBase64(String uuid);

    /**
     * 检查是否存在规定参数，并且校验其中的验证码是否正确
     */
    void checkCaptchaByHashMap(String value ,String uuid);

    /**
     * 获取字节数组类型验证码
     */
    byte[] getCaptchaByByteArray(String uuid);
}
