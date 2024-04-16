package com.fiafeng.mapping.pojo;

import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.mapping.pojo.DefaultMapping;
import com.fiafeng.mapping.pojo.vo.RequestMappingDataVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@PojoAnnotation
public class RequestMappingBean {

    List<RequestMappingDataVO> defaultMappingList = new ArrayList<>();

    List<DefaultMapping> baseMappingList = new ArrayList<>();

    HashMap<String, Integer> urlHashMap = new HashMap<>();
}
