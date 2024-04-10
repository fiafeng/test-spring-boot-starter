package com.fiafeng.common.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.constant.CacheConstants;
import com.fiafeng.common.constant.Constants;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Interface.IBaseUserInfo;
import com.fiafeng.common.service.ITokenService;
import com.fiafeng.common.utils.HttpServletUtils;
import com.fiafeng.common.utils.IdUtils;
import com.fiafeng.common.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@BeanDefinitionOrderAnnotation(0)
public class DefaultTokenServiceImpl implements ITokenService {


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


    /**
     * 获取请求token
     *
     * @param request 请求
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.strNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
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
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }

    @Override
    public <T extends IBaseUserInfo> T getLoginUser() {
        HttpServletRequest httpServletRequest = HttpServletUtils.getHttpServletRequest();
        // 获取请求携带的令牌
        String token = getToken(httpServletRequest);
        if (StringUtils.strNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.Token_LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                JSONObject jsonObject = cacheService.getCacheObject(userKey);
                IBaseUserInfo defaultSecurityLoginUserInfo = JSONObject.parseObject(jsonObject.toJSONString(), IBaseUserInfo.class);
                if (defaultSecurityLoginUserInfo == null) {
                    throw new ServiceException("token不存在", 403);
                }

                return (T) defaultSecurityLoginUserInfo;
            } catch (Exception e) {
                log.error("获取用户信息异常'{}'", e.getMessage());
                throw new ServiceException("token校验失败", 403);
            }
        }
        return null;
    }


    /**
     * 创建令牌
     *
     * @param claims 令牌的Map信息
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 创建令牌
     *
     * @param userInfo 用户信息
     * @return 令牌
     */
    @Override
    public String createToken(IBaseUserInfo userInfo) {

        String uuid = IdUtils.fastUUID();
        userInfo.setUuid(uuid);
        refreshToken(userInfo);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.Token_LOGIN_USER_KEY, uuid);
        return createToken(claims);
    }

    @Override
    public void verifyToken(IBaseUserInfo userInfo) {
        long expireTime = userInfo.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {

            refreshToken(userInfo);
        }
    }

    @Override
    public void refreshToken(IBaseUserInfo userInfo) {
        userInfo.setLoginTime(System.currentTimeMillis());
        userInfo.setExpireTime(userInfo.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        ObjectMapper objectMapper = new ObjectMapper();
        String userKey = getTokenKey(userInfo.getUuid());
        try {
            String jsonString = objectMapper.writeValueAsString(userInfo);
            JSONObject jsonObject = JSONObject.parse(jsonString);
            cacheService.setCacheObject(userKey, jsonObject, expireTime, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean removeToken(IBaseUserInfo userInfo) {
        return cacheService.deleteObject(getTokenKey(userInfo.getUuid()));
    }
}
