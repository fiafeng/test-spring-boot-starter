package com.fiafeng.common.properties;


/**
 * 是否开启的属性接口
 */
public interface IEnableProperties extends IFiafengProperties{

    Boolean getEnable();

    void setEnable(Boolean enable);
}
