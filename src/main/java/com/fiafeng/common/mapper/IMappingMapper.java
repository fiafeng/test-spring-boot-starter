package com.fiafeng.common.mapper;

import com.fiafeng.common.pojo.Interface.IBaseMapping;

import java.util.List;

public interface IMappingMapper {

    <T extends IBaseMapping> boolean insertMapping(T mapping);

    <T extends IBaseMapping> boolean insertMappingList(List<T> mappingList);

    <T extends IBaseMapping> List<T> selectMappingListAll();

    <T extends IBaseMapping> boolean updateMapping(T mapping);


    boolean deletedMappingById(Long mappingId);

    boolean deletedMappingList(List<Long> mappingId);

    <T extends IBaseMapping> T selectMappingById(Long mappingId);


    <T extends IBaseMapping> T selectMappingByUrl(String url);
}
