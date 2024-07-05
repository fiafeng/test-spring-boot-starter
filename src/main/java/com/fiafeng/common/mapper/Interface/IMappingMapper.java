package com.fiafeng.common.mapper.Interface;

import com.fiafeng.mapping.pojo.Interface.IBaseMapping;

import java.util.List;

public interface IMappingMapper extends IMapper{

    <T extends IBaseMapping> int insertMapping(T mapping);

    <T extends IBaseMapping> int insertMappingList(List<T> mappingList);

    <T extends IBaseMapping> List<T> selectMappingListAll();

    <T extends IBaseMapping> int updateMapping(T mapping);


    int deletedMappingById(Long mappingId);

    int deletedMappingList(List<Long> mappingId);

    <T extends IBaseMapping> T selectMappingById(Long mappingId);


    <T extends IBaseMapping> T selectMappingByUrl(String url);
}
