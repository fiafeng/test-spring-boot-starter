package com.fiafeng.captcha.service;

import java.util.HashMap;

public interface ICaptchaService {

    String getCaptchaByBase64(String uuid);

    void checkCaptchaByHashMap(HashMap<String, String> hashMap);

    byte[] getCaptchaByByteArray(String uuid);
}
