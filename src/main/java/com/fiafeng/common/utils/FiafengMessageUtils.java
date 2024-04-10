package com.fiafeng.common.utils;

import com.fiafeng.i18n.properties.FiafengI18nProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 获取i18n资源文件
 *
 * @author ruoyi
 */
public class FiafengMessageUtils {


    static FiafengI18nProperties i18nProperties;

    @Autowired
    public void setI18nProperties(FiafengI18nProperties i18nProperties) {
        FiafengMessageUtils.i18nProperties = i18nProperties;
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) {
        if (i18nProperties.enable) {
            MessageSource messageSource = SpringUtils.getBean(MessageSource.class);
            try {

               return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
            }catch (Exception e){
                return code;
            }
        }
        else {
            return code;
        }
    }
}
