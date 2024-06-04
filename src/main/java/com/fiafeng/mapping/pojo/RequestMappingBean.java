package com.fiafeng.mapping.pojo;

import com.fiafeng.mapping.pojo.vo.RequestMappingDataVO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
public class RequestMappingBean {

    List<RequestMappingDataVO> defaultMappingList = new ArrayList<>();

    List<DefaultMapping> baseMappingList = new ArrayList<>();

    HashMap<String, Integer> urlHashMap = new HashMap<>();
}
