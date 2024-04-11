package com.fiafeng.common.service;

import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/06
 * @description
 */

public interface ICacheService {

    <T> void setCacheObject(final String key, final T value);

    <T> T getCacheObject(final String key);

    <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit);

    boolean deleteObject(final String key);

    Boolean hasKey(String key);

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    boolean expire(final String key, final long timeout);

    boolean expire(final String key, final long timeout, final TimeUnit unit);

    long getExpire(final String key);
}
