package com.fiafeng.captcha.service;

import java.util.HashMap;
import java.util.Map;

public interface ICaptchaService {

    String getCaptchaByBase64(String uuid);

    void checkCaptchaByHashMap(String value ,String uuid);

    byte[] getCaptchaByByteArray(String uuid);
}
