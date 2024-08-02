package com.fiafeng.flowable.service;

import java.io.InputStream;

/**
 * 流程追踪图生成类
 *
 * @author liuxz
 */
public interface IFlowableImageService {

    /**
     * 根据流程实例标识，生成流程跟踪图示（高亮）
     *
     * @param procInstId 流程实例标识
     * @return
     * @throws Exception
     */
    InputStream generateImageByProcInstId(String procInstId) throws Exception;

}