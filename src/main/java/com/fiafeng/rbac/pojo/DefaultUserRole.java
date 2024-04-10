package com.fiafeng.rbac.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.BaseUserRoleAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@Data
@Accessors(chain = true)
@BaseUserRoleAnnotation
@Scope("prototype")
@BeanDefinitionOrderAnnotation
public class DefaultUserRole implements IBaseUserRole {

    private Long id;

    private Long roleId;

    private Long userId;
}
