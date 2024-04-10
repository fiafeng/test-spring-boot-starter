package com.fiafeng.common.service.Impl;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.service.ICacheService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Fiafeng
 * @create 2023/12/06
 * @description
 */

@BeanDefinitionOrderAnnotation()
public class DefaultCacheServiceImpl implements ICacheService {

    // <key,[obj,data]>   对象数组第二个元素存储时间，如果没有设置缓存时间，则是Object对象
    public ConcurrentHashMap<String, Object[]> cacheHashMap = new ConcurrentHashMap<>();


    @Override
    public <T> void setCacheObject(String key, T value) {
        cacheHashMap.put(key, new Object[]{value, new Object()});
    }

    @Override
    public <T> T getCacheObject(String key) {
        // 不存在key
        if (!cacheHashMap.containsKey(key)) {
            return null;
        }

        // 判断过期时间
        if (cacheHashMap.get(key)[1] instanceof LocalDateTime) {
            LocalDateTime date = (LocalDateTime) cacheHashMap.get(key)[1];
            LocalDateTime nowTime = LocalDateTime.now();
            // 已经过期了
            if (nowTime.isAfter(date)) {
                deleteObject(key);
            }
        }

        return (T) cacheHashMap.get(key)[0];
    }

    @Override
    public <T> void setCacheObject(String key, T value, Long timeout, TimeUnit timeUnit) {

        LocalDateTime nowTime = LocalDateTime.now();
        switch (timeUnit) {
            case NANOSECONDS:
                nowTime = nowTime.plusNanos(timeout);
                break;
            case SECONDS:
                nowTime = nowTime.plusSeconds(timeout);
                break;
            case MINUTES:
                nowTime = nowTime.plusMinutes(timeout);
                break;
            case HOURS:
                nowTime = nowTime.plusHours(timeout);
                break;
            case DAYS:
                nowTime = nowTime.plusDays(timeout);
                break;
            default:
                nowTime = nowTime.plusSeconds(1);
        }

        cacheHashMap.put(key, new Object[]{value, nowTime});
    }

    @Override
    public boolean deleteObject(String key) {
        return cacheHashMap.remove(key) != null;
    }

    @Override
    public Boolean hasKey(String key) {
        return cacheHashMap.containsKey(key);
    }

    @Override
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (!cacheHashMap.containsKey(key)) {
            return false;
        }
        setCacheObject(key, getCacheObject(key),timeout, unit);

        return true;
    }

    public long getExpire(final String key) {
        // 不存在key
        if (!cacheHashMap.containsKey(key)) {
            return -1;
        }

        // 判断过期时间
        if (cacheHashMap.get(key)[1] instanceof LocalDateTime) {
            LocalDateTime date = (LocalDateTime) cacheHashMap.get(key)[1];
            LocalDateTime nowTime = LocalDateTime.now();
            // 已经过期了
            if (nowTime.isAfter(date)) {
                deleteObject(key);
                return -2;
            }
        }
        LocalDateTime localDateTime = (LocalDateTime) cacheHashMap.get(key)[1];
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date.getTime();
    }


}
