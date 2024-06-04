package com.fiafeng.common.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@Data
@Accessors(chain = true)
@PojoAnnotation
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
public class DefaultUserRole implements IBaseUserRole {

    private Long id;

    private Long roleId;

    private Long userId;
}
