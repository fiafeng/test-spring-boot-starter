package com.fiafeng.common.pojo.Interface;


import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBasePermission extends IBasePojo {

    Long getId();

    IBasePermission setId(Long id);

    String getName();

    IBasePermission setName(String name);


}
