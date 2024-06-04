package com.fiafeng.common.pojo;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@PojoAnnotation
@BeanDefinitionOrderAnnotation()
public class DefaultLog {

    public Long id;

    public String username;

    public String operate;

    public Date date;

}
