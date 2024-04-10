package com.fiafeng.common.service;

import com.fiafeng.common.pojo.Interface.IBaseUserInfo;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */

public interface ITokenService {


    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    <T extends IBaseUserInfo> T getLoginUser();

    /**
     * 创建令牌
     *
     * @return 令牌
     */
    String createToken(IBaseUserInfo userInfo);

    void verifyToken(IBaseUserInfo userInfo);

    void refreshToken(IBaseUserInfo userInfo);

   boolean removeToken(IBaseUserInfo userInfo);
}
