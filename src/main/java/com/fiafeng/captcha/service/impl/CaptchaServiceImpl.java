package com.fiafeng.captcha.service.impl;

import com.fiafeng.captcha.properties.FiafengCaptchaProperties;
import com.fiafeng.captcha.service.ICaptchaService;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.utils.Base64;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class CaptchaServiceImpl implements ICaptchaService {

    @Autowired
    FiafengCaptchaProperties captchaProperties;

    @Resource(name = "captchaProducerChar")
    private Producer captchaProducerChar;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    ICacheService cacheService;


    /**
     * 生成验证码图片的Base64形式，并且根据传递的uuid把对应的正确结果保存到缓存中
     * @param uuid uuid
     * @return 验证码图片的Base64形式
     */
    public String getCaptchaByBase64(String uuid) {
        return Base64.encode(getCaptchaByByteArray(uuid));
    }

    /**
     * 检查是否存在规定参数，并且校验其中的验证码是否正确
     */
    public void checkCaptchaByHashMap(String captchaValue ,String uuid) {
        if (captchaValue == null || captchaValue.isEmpty() || uuid == null || uuid.isEmpty()) {
            throw new ServiceException("参数传递错误！");
        }
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        String cacheValue = cacheService.getCacheObject(verifyKey);
        if (cacheValue == null) {
            throw new ServiceException("验证码不存在,或者已经过期", captchaProperties.expireCode);
        }
        if (!cacheValue.equals(captchaValue)){
            throw new ServiceException("验证码不正确", captchaProperties.incorrectCode);
        }
    }

    /**
     * 生成验证码图片的字节数组形式，并且根据传递的uuid把对应的正确结果保存到缓存中
     * @param uuid uuid
     * @return 验证码图片的字节数组形式
     */
    public byte[] getCaptchaByByteArray(String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;
        switch (captchaProperties.captchaProducerType) {
            case Char:
                capStr = code = captchaProducerChar.createText();
                image = captchaProducerChar.createImage(capStr);
                break;
            case Math:
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                image = captchaProducerMath.createImage(capStr);
                break;
        }

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }

        byte[] byteArray = os.toByteArray();
        // 保存到缓存中
        cacheService.setCacheObject(verifyKey, code, captchaProperties.captchaExpiration, TimeUnit.MINUTES);
        return byteArray;
    }
}
