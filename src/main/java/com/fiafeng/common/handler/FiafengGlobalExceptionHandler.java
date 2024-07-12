package com.fiafeng.common.handler;

import com.fiafeng.common.utils.spring.FiafengMessageUtils;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Fiafeng
 * @create 2023/12/05
 * @description 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class FiafengGlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e) {
        String message = e.getMessage();
        log.error(message, e);
        Integer code = e.getCode();
        message =  FiafengMessageUtils.message(e.getMessage());
        return ObjectUtils.isNotNull(code) ? AjaxResult.error(code, message) : AjaxResult.error(message);
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult handleOtherException(Exception e) {
        String message = e.getMessage();
        log.error(message, e);
        int code = 500;
        message =  FiafengMessageUtils.message(e.getMessage());
        return AjaxResult.error(code, message);
    }
}
