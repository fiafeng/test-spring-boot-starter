package com.fiafeng.mapping.pojo;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;

@Data
@Accessors(chain = true)
@BeanDefinitionOrderAnnotation
@PojoAnnotation
public class DefaultMapping implements IBaseMapping {

    private Long id;

    private String url;

    private HashSet<String> roleHashSet = new HashSet<>();

    private HashSet<String> permissionHashSet = new HashSet<>();
}
