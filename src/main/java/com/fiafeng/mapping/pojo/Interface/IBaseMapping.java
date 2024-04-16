package com.fiafeng.mapping.pojo.Interface;


import com.fiafeng.common.pojo.Interface.base.IBasePojo;

import java.util.HashSet;

/**
 * @author Fiafeng
 * @create 2023/12/08
 * @description
 */
public interface IBaseMapping extends IBasePojo {

    Long getId();

    IBaseMapping setId(Long id);

    String getUrl();

    IBaseMapping setUrl(String url);

    HashSet<String> getRoleHashSet();

    IBaseMapping setRoleHashSet(HashSet<String> roleHashSet);

    HashSet<String> getPermissionHashSet();

    IBaseMapping setPermissionHashSet(HashSet<String> permissionHashSet);
}
