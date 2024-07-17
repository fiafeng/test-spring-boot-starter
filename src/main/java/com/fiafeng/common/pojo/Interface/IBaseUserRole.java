package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseUserRole extends IBasePojo {


    Long getRoleId();

    void setRoleId(Long roleId);

    Long getUserId();

    void setUserId(Long userId);


}

