package com.fiafeng.flowable.flowable.dao;


import com.fiafeng.flowable.flowable.domain.FlowProcDefDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 流程定义查询
 *
 * @author Tony
 * @email
 * @date 2022/1/29 5:44 下午
 **/
@Mapper
public interface FlowDeployMapper {

    /**
     * 流程定义列表
     * @param name
     * @return
     */
    List<FlowProcDefDto> selectDeployList(String name);
}
