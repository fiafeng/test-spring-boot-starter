package com.fiafeng.validation.properties;


import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fiafeng.validation")
public class FiafengValidationProperties implements IEnableProperties {

    /**
     * 是否开启参数校验
     */
    public Boolean enable = true;


}
