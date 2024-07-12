package com.fiafeng.mapping.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;

import java.util.List;

@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlMappingMapper extends BaseObjectMysqlMapper implements IMappingMapper {


    public <T extends IBaseMapping> int insertMapping(T mapping) {
        return insertObject(mapping);
    }

    @Override
    public <T extends IBaseMapping> int insertMappingList(List<T> mapping) {
        insertObjectList(mapping);
        return 1;
    }


    @Override
    public int deletedMappingById(Long mappingId) {
        return deletedObjectById(mappingId);
    }

    @Override
    public int deletedMappingList(List<Long> mappingIdList) {
        return deletedObjectByIdList(mappingIdList);
    }


    @Override
    public <T extends IBaseMapping> int updateMapping(T mapping) {
        return updateObject(mapping);
    }

    @Override
    public <T extends IBaseMapping> List<T> selectMappingListAll() {
        return selectObjectListAll();
    }


    public <T extends IBaseMapping> T selectMappingById(Long mappingId) {
        return selectObjectByObjectId(mappingId);
    }

    public <T extends IBaseMapping> T selectMappingByUrl(String url) {
        return selectObjectByColValue("url", url);
    }

}
