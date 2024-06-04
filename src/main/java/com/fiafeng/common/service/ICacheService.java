package com.fiafeng.common.service;

import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/06
 * @description
 */

public interface ICacheService {

    /**
     * 设置key和value的值
     * @param key key
     * @param value value
     * @param <T> value类型
     */
    <T> void setCacheObject(final String key, final T value);

    /**
     * 根据key获取对应的没有过期的value值
     * @param key key
     * @return value对象或者null
     * @param <T> value的类型
     */
    <T> T getCacheObject(final String key);

    /**
     * 设置key和value的值，并且设置过期时间
     * @param key key
     * @param value value值
     * @param timeout 时间
     * @param timeUnit 时间单位
     * @param <T>  value的类型
     */
    <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit);

    /**
     * 根据key删除缓存
     * @param key key
     * @return 删除是否成功
     */
    boolean deleteObject(final String key);

    /**
     * 当前key是否有值
     * @param key key
     * @return 如果存在则返回true，不存在则返回false
     */
    Boolean hasKey(String key);

    /**
     * 设置有效时间
     *
     * @param key key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    boolean expire(final String key, final long timeout);

    /**
     * 设置有效时间
     *
     * @param key key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    boolean expire(final String key, final long timeout, final TimeUnit unit);

    /**
     * 获取key的过期时间
     * @param key key
     * @return 过期时间
     */
    long getExpire(final String key);
}
