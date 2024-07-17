package com.fiafeng.common.mapper.Interface;

import com.fiafeng.mapping.pojo.Interface.IBaseMapping;

import java.util.List;

public interface IMappingMapper extends IMapper{
    int insertMapping(IBaseMapping mapping);

    int insertMappingList(List<IBaseMapping> mappingList);

     List<IBaseMapping> selectMappingListAll();

     int updateMapping(IBaseMapping mapping);


    int deletedMappingById(Long mappingId);

    int deletedMappingList(List<Long> mappingId);

     IBaseMapping selectMappingById(Long mappingId);


     IBaseMapping selectMappingByUrl(String url);
}
