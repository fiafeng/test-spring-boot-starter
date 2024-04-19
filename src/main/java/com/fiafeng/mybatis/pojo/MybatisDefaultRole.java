package com.fiafeng.mybatis.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import lombok.Data;
import lombok.experimental.Accessors;

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
@PojoAnnotation
@BeanDefinitionOrderAnnotation
public class MybatisDefaultRole implements IBaseRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    public static List<IBaseRole> toBaseRoleByHashMap(HashMap<Long, String> map) {
        List<IBaseRole> basePermissionList = new ArrayList<>();
        for (Long permissionId : map.keySet()) {
            basePermissionList.add(
                    new MybatisDefaultRole()
                            .setId(permissionId)
                            .setName(map.get(permissionId)));
        }
        return basePermissionList;
    }
}
