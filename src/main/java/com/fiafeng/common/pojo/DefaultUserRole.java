package com.fiafeng.common.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import lombok.Data;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@Data
@PojoAnnotation
@BeanDefinitionOrderAnnotation()
public class DefaultUserRole implements IBaseUserRole {

    private Long id;

    private Long roleId;

    private Long userId;
}
