package com.fiafeng.mapping.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.mysql.BaseMysqlMapper;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@BeanDefinitionOrderAnnotation(2)
public class DefaultMysqlMappingMapper extends BaseMysqlMapper implements IMappingMapper {


    @Override
    @Value("${fiafeng.mysqlTable.mapping.tableName:base_mapping}")
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    @Value("${fiafeng.mysqlTable.mapping.idName:id}")
    public void setIdName(String idName) {
        this.idName = idName;
    }

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
