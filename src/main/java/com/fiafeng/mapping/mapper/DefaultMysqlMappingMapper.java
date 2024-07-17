package com.fiafeng.mapping.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.common.mapper.mysql.BaseObjectMysqlMapper;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;

import java.util.List;

@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlMappingMapper extends BaseObjectMysqlMapper implements IMappingMapper {


    public int insertMapping(IBaseMapping mapping) {
        return insertObject(mapping);
    }

    @Override
    public int insertMappingList(List<IBaseMapping> mapping) {
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
    public  int updateMapping(IBaseMapping mapping) {
        return updateObject(mapping);
    }

    @Override
    public  List<IBaseMapping> selectMappingListAll() {
        return selectObjectListAll();
    }


    public  IBaseMapping selectMappingById(Long mappingId) {
        return (IBaseMapping) selectObjectByObjectId(mappingId);
    }

    public  IBaseMapping selectMappingByUrl(String url) {
        return (IBaseMapping) selectObjectByColValue("url", url);
    }

}
