package com.fiafeng.rbac.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.BaseRolePermissionAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseRolePermission;
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
@BaseRolePermissionAnnotation
@Scope("prototype")
@BeanDefinitionOrderAnnotation
public class DefaultRolePermission implements IBaseRolePermission {

    private Long id;

    private Long roleId;

    private Long permissionId;
}
