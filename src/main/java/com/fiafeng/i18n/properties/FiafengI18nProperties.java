package com.fiafeng.i18n.properties;


import com.fiafeng.common.properties.IProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties("fiafeng.i18n")
@Data
public class FiafengI18nProperties implements IProperties {

    /**
     * 是否开启国际化配置
     */
    public Boolean enable = true;

    /**
     * 本地国际化资源文件路径
     */
    public String messagePath = "i18n/message";

    /**
     * 前端在headers里面传递标志语言的属性名
     */
    public String language = "language";

    /**
     * 系统默认语言
     */
    public Locale locale = Locale.SIMPLIFIED_CHINESE;
}
