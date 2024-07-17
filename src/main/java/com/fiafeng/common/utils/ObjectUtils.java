package com.fiafeng.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */
public class ObjectUtils {

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    public static <T> T getNewObejct(T t){
        return (T) JSONObject.from(t).toJavaObject(FiafengSpringUtils.getBean(t.getClass()).getClass());
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

}
