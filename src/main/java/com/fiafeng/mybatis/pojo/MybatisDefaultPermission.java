package com.fiafeng.mybatis.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import lombok.Data;
import lombok.experimental.Accessors;

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
@PojoAnnotation
@BeanDefinitionOrderAnnotation(1)
public class MybatisDefaultPermission implements IBasePermission {

    @TableId(type = IdType.AUTO)
    public Long id;

    public String name;

    public static List<IBasePermission> toBasePermissionByHashMap(HashMap<Long, String> map) {
        List<IBasePermission> basePermissionList = new ArrayList<>();
        for (Long permissionId : map.keySet()) {
            basePermissionList.add(
                    new MybatisDefaultPermission()
                            .setId(permissionId)
                            .setName(map.get(permissionId)));
        }
        return basePermissionList;
    }


}
