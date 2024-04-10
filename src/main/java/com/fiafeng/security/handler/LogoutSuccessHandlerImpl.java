package com.fiafeng.security.handler;

import com.alibaba.fastjson2.JSON;
import com.fiafeng.common.pojo.AjaxResult;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private ITokenService tokenService;

    /**
     * 退出处理
     *
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        IBaseUserInfo loginUserInfo =tokenService.getLoginUser();
        if (ObjectUtils.isNotNull(loginUserInfo)) {
//            String userName = loginUserInfo.getUsername();
            // 删除用户缓存记录
            tokenService.removeToken(loginUserInfo);
            // 记录用户退出日志
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));

            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
        }
    }
}
