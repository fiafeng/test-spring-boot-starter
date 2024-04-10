package com.fiafeng.mapping.pojo;


import com.fiafeng.common.annotation.BaseMappingAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseMapping;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;

@Data
@Accessors(chain = true)
@BaseMappingAnnotation
@BeanDefinitionOrderAnnotation
@Scope("prototype")
public class DefaultMapping implements IBaseMapping {

    private Long id;

    private String url;

    private HashSet<String> roleHashSet = new HashSet<>();

    private HashSet<String> permissionHashSet = new HashSet<>();
}
