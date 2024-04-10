package com.fiafeng.mapping.pojo.vo;

import com.fiafeng.mapping.pojo.DefaultMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class RequestMappingBean {

    List<RequestMappingDataVO> defaultMappingList = new ArrayList<>();

    List<DefaultMapping> baseMappingList = new ArrayList<>();

    HashMap<String, Integer> urlHashMap = new HashMap<>();
}
