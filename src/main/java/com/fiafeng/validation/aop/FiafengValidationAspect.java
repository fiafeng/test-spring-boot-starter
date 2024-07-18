package com.fiafeng.validation.aop;


import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.validation.annotation.IntegerValidationAnnotation;
import com.fiafeng.validation.annotation.LongValidationAnnotation;
import com.fiafeng.validation.annotation.StringValidationAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Slf4j
@Component
@ConditionalOnClass(Aspect.class)
public class FiafengValidationAspect {

    @Before(value = "@annotation(com.fiafeng.validation.annotation.ValidationAnnotation)")
    public void validation(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            Field[] fields = arg.getClass().getDeclaredFields();
            for (Field field : fields) {

                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                Object objectValue = null;
                try {
                    objectValue = field.get(arg);
                } catch (Exception e) {
                    continue;
                }
                if (fieldType == String.class && field.isAnnotationPresent(StringValidationAnnotation.class)) {
                    //存在某个注解就进行对应的处理
                    StringValidationAnnotation annotation = field.getAnnotation(StringValidationAnnotation.class);
                    String regex = annotation.regex();
                    int max = annotation.max();
                    int min = annotation.min();
                    if (StringUtils.strNotEmpty(annotation.fieldName())) {
                        fieldName = annotation.fieldName();
                    }

                    boolean allowNull = annotation.allowNull();
                    //设置属性可见性
                    field.setAccessible(true);
                    String value = null;
                    try {
                        value = (String) objectValue;
                    } catch (Exception e) {
                        try {
                            value = objectValue.toString();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                    //先判断空
                    //如果可以为空 值也为空就不进行处理
                    if (allowNull && ObjectUtils.isNull(value)) {
                        continue;
                    }
                    if (!allowNull && StringUtils.strIsEmpty(value)) {
                        throw new ServiceException(fieldName + "：不能为空！");
                    }
                    //如果正则不匹配就直接返回
                    if (StringUtils.strNotEmpty(regex)) {
                        if (!value.matches(regex)) {
                            throw new ServiceException(fieldName + "：格式不正确！");
                        }
                    }
                    if (min != -1 && value.length() < min) {
                        throw new ServiceException(fieldName + ":字符串长度小于" + min);
                    }

                    if (max != -1 && value.length() > max) {
                        throw new ServiceException(fieldName + ":字符串长度大于" + max);
                    }
                }
                if (fieldType == Integer.class && field.isAnnotationPresent(IntegerValidationAnnotation.class)) {
                    //存在某个注解就进行对应的处理
                    IntegerValidationAnnotation annotation = field.getAnnotation(IntegerValidationAnnotation.class);
                    int max = annotation.max();
                    int min = annotation.min();
                    if (StringUtils.strNotEmpty(annotation.fieldName())) {
                        fieldName = annotation.fieldName();
                    }
                    boolean allowNull = annotation.allowNull();
                    //设置属性可见性
                    field.setAccessible(true);
                    Integer value = -1;
                    try {
                        value = (Integer) objectValue;
                    } catch (Exception e) {
                        try {
                            value = Integer.valueOf(objectValue.toString());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    //先判断空
                    //如果可以为空 值也为空就不进行处理
                    if (allowNull && ObjectUtils.isNull(value)) {
                        continue;
                    }

                    if (min != -1 && value < min) {
                        throw new ServiceException(fieldName + ":最小值为" + min);
                    }

                    if (max != -1 && value > max) {
                        throw new ServiceException(fieldName + ":最大值为：" + max);
                    }
                }

                if (fieldType == Long.class && field.isAnnotationPresent(LongValidationAnnotation.class)) {
                    LongValidationAnnotation annotation = field.getAnnotation(LongValidationAnnotation.class);
                    long max = annotation.max();
                    long min = annotation.min();

                    if (StringUtils.strNotEmpty(annotation.fieldName())) {
                        fieldName = annotation.fieldName();
                    }
                    boolean allowNull = annotation.allowNull();
                    //设置属性可见性
                    field.setAccessible(true);
                    Long value;
                    try {
                        value = (Long) objectValue;
                    } catch (Exception e) {
                        try {
                            value = Long.valueOf(objectValue.toString());
                        } catch (Exception ex) {
                            value = -1L;
                        }
                    }

                    //先判断空
                    //如果可以为空 值也为空就不进行处理
                    if (!allowNull && (value == null || value == -1)) {
                        throw new ServiceException(fieldName + "不允许为空");
                    }

                    if (!allowNull && min != -1 && value < min) {
                        throw new ServiceException(fieldName + ":最小值为" + min);
                    }

                    if (!allowNull && max != -1 && value > max) {
                        throw new ServiceException(fieldName + ":最大值为：" + max);
                    }
                }

            }
        }

    }
}
