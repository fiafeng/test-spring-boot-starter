package com.fiafeng.mapping.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.mapping.pojo.DefaultMapping;
import com.fiafeng.mapping.pojo.vo.RequestMappingBean;
import com.fiafeng.mapping.properties.FiafengMappingProperties;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.FiafengStaticBean;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Slf4j
public class FiafengPermissionInterceptor implements HandlerInterceptor {

    @Autowired
    ICacheService cacheService;

    @Autowired
    ITokenService tokenService;

    @Autowired
    FiafengMappingProperties mappingProperties;

    @Autowired
    RequestMappingBean requestMappingBean;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!mappingProperties.permissionInterceptorEnable) {
            return true;
        }
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/login") || "/error".equals(requestURI)) {
            return true;
        }

        requestURI = FiafengStaticBean.searchTree.valueExistTree(requestURI);
        if (requestURI == null) {

//            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error("地址不存在")));
            throw new ServiceException("地址不存在", 404);
//            return false;
        }

        IBaseUserInfo loginUserInfo = tokenService.getLoginUser();

        String userKey = CacheConstants.LOGIN_TOKEN_KEY + loginUserInfo.getUuid();
        JSONObject jsonObject = cacheService.getCacheObject(userKey);
        IBaseUserInfo cacheObj = JSONObject.parseObject(jsonObject.toJSONString(), IBaseUserInfo.class);

        List<String> permissionList = cacheObj.getPermissionList();
        List<String> roleList = cacheObj.getRoleList();

        DefaultMapping mappingBaseVO = requestMappingBean.getBaseMappingList().get(requestMappingBean.getUrlHashMap().get(requestURI));

        if (mappingBaseVO.getPermissionHashSet().isEmpty() && mappingBaseVO.getRoleHashSet().isEmpty()) {
            return true;
        }

        if (!mappingBaseVO.getPermissionHashSet().isEmpty()) {
            for (String permission : permissionList) {
                if (mappingBaseVO.getPermissionHashSet().contains(permission)) {
                    return true;
                }
            }
        }
        if (!mappingBaseVO.getRoleHashSet().isEmpty()) {
            for (String role : roleList) {
                if (mappingBaseVO.getRoleHashSet().contains(role)) {
                    return true;
                }
            }
        }
        throw new ServiceException("您没有权限查询", 403);
//        return false;
    }
}
