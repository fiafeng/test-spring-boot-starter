package com.fiafeng.mapping.pojo.Interface;


import com.fiafeng.common.pojo.Interface.base.IBasePojo;

import java.util.HashSet;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseMapping extends IBasePojo {


    String getUrl();

    void setUrl(String url);

    HashSet<String> getRoleHashSet();

    void setRoleHashSet(HashSet<String> roleHashSet);

    HashSet<String> getPermissionHashSet();

    void setPermissionHashSet(HashSet<String> permissionHashSet);
}
