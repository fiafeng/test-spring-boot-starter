package com.fiafeng.common.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import lombok.Data;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
@Data
@PojoAnnotation
@BeanDefinitionOrderAnnotation()
public class DefaultRole implements IBaseRole {

    private Long id;

    private String name;
}
