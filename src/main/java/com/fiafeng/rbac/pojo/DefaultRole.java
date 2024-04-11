package com.fiafeng.rbac.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
@Data
@Accessors(chain = true)
@Scope("prototype")
@BeanDefinitionOrderAnnotation
public class DefaultRole implements IBaseRole {

    private Long id;

    private String name;

    public static List<IBaseRole> toBaseRoleByHashMap(HashMap<Long, String> map) {
        List<IBaseRole> basePermissionList = new ArrayList<>();
        for (Long permissionId : map.keySet()) {
            basePermissionList.add(
                    new DefaultRole()
                            .setId(permissionId)
                            .setName(map.get(permissionId)));
        }
        return basePermissionList;
    }
}
