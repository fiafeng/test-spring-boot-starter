package com.fiafeng.security.service;

import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ITokenService;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
public interface ITokenSecurityService extends ITokenService{

    <T extends IUserDetails> T getSecurityLoginUser();
    String createSecurityToken(IUserDetails userDetails);
    void verifySecurityToken(IUserDetails userDetails);

    boolean removeSecurityToken(IUserDetails userDetails);

    void refreshSecurityToken(IUserDetails userDetails);


    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    default <T extends IBaseUserInfo> T getLoginUser() {
        return getSecurityLoginUser();
    }

    /**
     * 创建令牌
     *
     * @return 令牌
     */
    default String createToken(IBaseUserInfo userInfo) {
        return createSecurityToken((IUserDetails) userInfo);
    }

    default void verifyToken(IBaseUserInfo userInfo) {
        verifySecurityToken((IUserDetails) userInfo);
    }

    default void refreshToken(IBaseUserInfo userInfo) {
        refreshSecurityToken((IUserDetails) userInfo);
    }

    default boolean removeToken(IBaseUserInfo userInfo) {
        return removeSecurityToken((IUserDetails) userInfo);
    }

}
