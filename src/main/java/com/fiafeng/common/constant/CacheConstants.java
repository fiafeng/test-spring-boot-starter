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
     * jwt token里面，获取用户信息的key值
     */
    public static final String TOKEN_LOGIN_USER_KEY = "login_user_key";


    /**
     * 用户名和uuid的映射
     */
    public static final String USERNAME_UUID = "userNameUUID:";


    /**
     * 角色拥有的权限缓存前缀
     */
    public static final String ROLE_PERMISSION_PREFIX = "role_permission_prefix:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

}
