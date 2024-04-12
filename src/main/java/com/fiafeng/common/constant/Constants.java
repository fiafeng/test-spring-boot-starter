package com.fiafeng.common.constant;

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";


    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";



    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "com.fiafeng"};

}
