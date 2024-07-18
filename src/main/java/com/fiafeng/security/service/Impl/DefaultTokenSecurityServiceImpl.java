package com.fiafeng.security.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import com.fiafeng.common.properties.FiafengTokenProperties;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.utils.IdUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.utils.mvc.HttpServletUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.security.service.ITokenSecurityService;
import com.fiafeng.security.service.IUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description token服务类
 */
@Slf4j
@BeanDefinitionOrderAnnotation(value = ModelConstant.secondOrdered)
@Component
public class DefaultTokenSecurityServiceImpl implements ITokenSecurityService {


    @Value("${fiafeng.token.secret:abcdefghijklmnopqrstuvwxyz}")
    private String secret;

    // 令牌有效期（默认60分钟）
    @Value("${fiafeng.token.expireTime:60}")
    private Long expireTime;

    // 令牌自定义标识
    @Value("${fiafeng.token.header:Authorization}")
    private String header;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    public ICacheService cacheService;

    @Autowired
    FiafengTokenProperties tokenProperties;


    /**
     * 获取请求token
     *
     * @param request 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(tokenProperties.header);
        if (StringUtils.strNotEmpty(token) && token.startsWith(tokenProperties.token_prefix)) {
            token = token.replace(tokenProperties.token_prefix, "");
        }
        return token;
    }


    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(tokenProperties.secret)
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * 创建令牌
     *
     * @param claims 令牌的Map信息
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, tokenProperties.secret).compact();
    }


    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }


    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    @Override
    public <T extends IUserDetails> T getSecurityLoginUser() {
        HttpServletRequest httpServletRequest = HttpServletUtils.getHttpServletRequest();
        // 获取请求携带的令牌
        String token = getToken(httpServletRequest);
        if (StringUtils.strNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(CacheConstants.TOKEN_LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
//                IBaseUserInfo defaultSecurityLoginUserInfo = cacheService.getCacheObject(userKey);
                JSONObject jsonObject = cacheService.getCacheObject(userKey);
//
//                IUserDetails defaultSecurityLoginUserInfo = jsonObject.toJavaObject(FiafengSpringUtils.getBean(IUserDetails.class).getClass());
                IUserDetails defaultSecurityLoginUserInfo = JSONObject.parseObject(jsonObject.toJSONString(), FiafengSpringUtils.getBean(IUserDetails.class).getClass());

                defaultSecurityLoginUserInfo.setUser(jsonObject.getJSONObject("user").toJavaObject(FiafengSpringUtils.getBean(IBaseUser.class).getClass()));

                return (T) defaultSecurityLoginUserInfo;
            } catch (Exception e) {
                log.error("获取用户信息异常'{}'", e.getMessage());
                throw new ServiceException("token校验失败", 403);
            }
        }
        return null;
    }

    @Override
    public String createSecurityToken(IUserDetails userDetails) {
        String uuid = cacheService.getCacheObject(CacheConstants.USERNAME_UUID + userDetails.getUser().getUsername());
        if (uuid == null){
            uuid = IdUtils.fastUUID();
        }
        userDetails.setUuid(uuid);
        refreshSecurityToken(userDetails);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CacheConstants.TOKEN_LOGIN_USER_KEY, uuid);
        return createToken(claims);
    }

    @Override
    public void verifySecurityToken(IUserDetails userDetails) {
        long expireTime = userDetails.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshSecurityToken(userDetails);
        }
    }

    @Override
    public boolean removeSecurityToken(IUserDetails userDetails) {
        return cacheService.deleteObject(getTokenKey(userDetails.getUuid()));
    }

    @Override
    public void refreshSecurityToken(IUserDetails userDetails) {
        userDetails.setLoginTime(System.currentTimeMillis());
        userDetails.setExpireTime(userDetails.getLoginTime() + expireTime * MILLIS_MINUTE);
        String uuidKey = CacheConstants.USERNAME_UUID + userDetails.getUser().getUsername();
        // 根据uuid将loginUser缓存
        ObjectMapper objectMapper = new ObjectMapper();
        String userKey = getTokenKey(userDetails.getUuid());
        try {
            String jsonString = objectMapper.writeValueAsString(userDetails);
            JSONObject jsonObject = JSONObject.parse(jsonString);
            cacheService.setCacheObject(userKey, jsonObject, tokenProperties.expireTime, TimeUnit.MINUTES);
            cacheService.setCacheObject(uuidKey, userDetails.getUuid(), tokenProperties.expireTime, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
