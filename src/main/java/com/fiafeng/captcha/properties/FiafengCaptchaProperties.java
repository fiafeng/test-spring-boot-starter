package com.fiafeng.captcha.properties;


import com.fiafeng.captcha.Enum.CaptchaProducerType;
import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fiafeng.captcha")
@Data
public class FiafengCaptchaProperties implements IEnableProperties {

    /**
     * 是否开启验证码
     */
    public Boolean enable = true;

    /**
     * 是否是否开启验证码登录
     */
    public boolean captchaLogin = false;

    /**
     * 验证码的类型
     */
    public CaptchaProducerType captchaProducerType = CaptchaProducerType.Math;

    /**
     * 验证码发送给前端时，图片的属性名
     */
    public String captchaName = "captcha";

    /**
     * 验证码有效时间(单位分钟) Expiration
     */
    public Long captchaExpiration = 5L;

    /**
     * 验证码发送给前端时，uuid的属性名
     */
    public String uuid = "uuid";

    /**
     * 使用验证码登陆时，前端发送到后端时，验证码值的名词
     */
    public String loginCaptchaName = "captcha";

    /**
     * 使用验证码登陆时，前端发送到后端时，验证码值的名词
     */
    public String loginUuid = "uuid";

    /**
     * 验证码过期时，返回的code值
     */
    public Integer expireCode = 700;

    /**
     * 验证码不正确时，返回的code值
     */
    public Integer incorrectCode = 701;


}
