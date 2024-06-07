package com.fiafeng.mapping.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserProperties;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@BeanDefinitionOrderAnnotation(value = ModelConstant.firstOrdered)
public class DefaultMysqlMappingMapper extends BaseMysqlMapper implements IMappingMapper {

    @Autowired
    FiafengMysqlUserProperties userProperties;


    public <T extends IBaseMapping> boolean insertMapping(T mapping) {
        return insertObject(mapping);
    }

    @Override
    public <T extends IBaseMapping> boolean insertMappingList(List<T> mapping) {
        insertObjectList(mapping);
        return false;
    }


    @Override
    public boolean deletedMappingById(Long mappingId) {
        return deletedObjectById(mappingId);
    }

    @Override
    public boolean deletedMappingList(List<Long> mappingIdList) {
        return deletedObjectByIdList(mappingIdList);
    }


    @Override
    public <T extends IBaseMapping> boolean updateMapping(T mapping) {
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
        return selectObjectByKeyAndValue("url", url);
    }

}
