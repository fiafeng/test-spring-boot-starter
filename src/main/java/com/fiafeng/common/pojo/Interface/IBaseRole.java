package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseRole extends IBasePojo {
    Long getId();

    IBaseRole setId(Long id);

    String getName();

    IBaseRole setName(String name);
}
