package com.fiafeng.mapping.pojo;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import lombok.Data;

import java.util.HashSet;

@Data
@BeanDefinitionOrderAnnotation()
@PojoAnnotation
public class DefaultMapping implements IBaseMapping {

    private Long id;

    private String url;

    private HashSet<String> roleHashSet = new HashSet<>();

    private HashSet<String> permissionHashSet = new HashSet<>();
}
