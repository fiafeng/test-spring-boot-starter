package com.fiafeng.common.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */

@Data
@BeanDefinitionOrderAnnotation()
@PojoAnnotation
public class DefaultUser implements IBaseUser, Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

}
