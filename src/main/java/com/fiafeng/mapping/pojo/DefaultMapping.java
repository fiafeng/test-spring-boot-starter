package com.fiafeng.mapping.pojo;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;

@Data
@Accessors(chain = true)
@BeanDefinitionOrderAnnotation(value = ModelConstant.defaultOrder)
@PojoAnnotation
public class DefaultMapping implements IBaseMapping {

    private Long id;

    private String url;

    private HashSet<String> roleHashSet = new HashSet<>();

    private HashSet<String> permissionHashSet = new HashSet<>();
}
