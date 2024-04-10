package com.fiafeng.common.constant;


/**
 * 缓存常量
 *
 * @author Fiafeng
 * @create 2023/12/04
 * @description
 */
public class CacheConstants {

    /**
     * 登录token缓存key
     */
    public static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 需要更新用户信息的key
     */
    public static String UPDATE_USER_INFO = "update_user_info";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";


    /**
     * 用户的角色信息缓存key
     */

    public static final String USER_ROLE_INFO_KEY = "user_role_info:";

    /**
     * 用户的权限信息缓存
     */
    public static final String USER_PERMISSION_INFO_KEY = "user_permission_info:";
}
