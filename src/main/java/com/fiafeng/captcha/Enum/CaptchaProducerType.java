package com.fiafeng.captcha.Enum;

public enum CaptchaProducerType {

    /**
     * 数学计算
     */
    Math("math"),
    /**
     * 字符验证
     */
    Char("char")
    ;

    public final String type;

    CaptchaProducerType(String type){
        this.type = type;
    }
}
