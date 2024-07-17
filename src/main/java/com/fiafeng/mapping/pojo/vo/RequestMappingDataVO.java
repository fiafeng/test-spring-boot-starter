package com.fiafeng.mapping.pojo.vo;

import com.fiafeng.mapping.pojo.DefaultMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequestMappingDataVO extends DefaultMapping {

    RequestMappingInfo requestMappingInfo;

    HandlerMethod handlerMethod;


    public RequestMappingDataVO() {

    }

    public RequestMappingDataVO(DefaultMapping defaultMapping) {
        this.setPermissionHashSet(defaultMapping.getPermissionHashSet());
        this.setRoleHashSet(defaultMapping.getRoleHashSet());
        this.setId(defaultMapping.getId());
        this.setUrl(defaultMapping.getUrl());
    }

    public DefaultMapping toDefaultMapping(){
        DefaultMapping defaultMapping = new DefaultMapping();
        defaultMapping.setPermissionHashSet(getPermissionHashSet());
        defaultMapping.setRoleHashSet(getRoleHashSet());
        defaultMapping.setId(getId());
        defaultMapping.setUrl(getUrl());
        return defaultMapping;
    }
}

