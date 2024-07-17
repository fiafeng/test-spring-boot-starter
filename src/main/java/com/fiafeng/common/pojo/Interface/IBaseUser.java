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

    void setUsername(String username);

    void setPassword(String password);

    void setId(Long id);


}
