package com.fiafeng.rbac.pojo;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Fiafeng
 * @create 2023/12/07
 * @description
 */
@Data
@Accessors(chain = true)
@Scope("prototype")
@BeanDefinitionOrderAnnotation
public class DefaultPermission implements IBasePermission {

    public Long id;

    public String name;

    public static List<IBasePermission> toBasePermissionByHashMap(HashMap<Long, String> map) {
        List<IBasePermission> basePermissionList = new ArrayList<>();
        for (Long permissionId : map.keySet()) {
            basePermissionList.add(
                    new DefaultPermission()
                            .setId(permissionId)
                            .setName(map.get(permissionId)));
        }
        return basePermissionList;
    }


}
