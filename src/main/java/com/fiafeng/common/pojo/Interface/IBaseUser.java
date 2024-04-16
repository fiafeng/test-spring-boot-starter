package com.fiafeng.common.pojo.Interface;

import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

@PojoAnnotation
public interface IBaseUser extends IBasePojo {

    String getUsername();

    String getPassword();

    Long getId();

    IBaseUser setUsername(String username);

    IBaseUser setPassword(String password);

    IBaseUser setId(Long id);


}
