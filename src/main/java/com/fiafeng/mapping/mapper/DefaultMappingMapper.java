package com.fiafeng.mapping.mapper;

import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@BeanDefinitionOrderAnnotation()
@Component
public class DefaultMappingMapper implements IMappingMapper {

    volatile ConcurrentHashMap<Long, IBaseMapping> mappingListMap;

    volatile ConcurrentHashMap<String, Long> longConcurrentHashMap;

    AtomicLong atomicLong = new AtomicLong();


    public ConcurrentHashMap<Long, IBaseMapping> getMappingListMap() {
        if (mappingListMap == null) {
            synchronized (this) {
                if (mappingListMap == null) {
                    mappingListMap = new ConcurrentHashMap<>();
                    getLongConcurrentHashMap();
                }
            }
        }
        return mappingListMap;
    }

    public ConcurrentHashMap<String, Long> getLongConcurrentHashMap(){
        if (longConcurrentHashMap == null) {
            synchronized (this) {
                if (longConcurrentHashMap == null) {
                    longConcurrentHashMap = new ConcurrentHashMap<>();
                    getMappingListMap();
                }
            }
        }
        return longConcurrentHashMap;
    }


    @Override
    public int insertMapping(IBaseMapping mapping) {
        ConcurrentHashMap<String, Long> hashMap = getLongConcurrentHashMap();
        if (hashMap.getOrDefault(mapping.getUrl(), null) == null){
            long andIncrement = atomicLong.getAndIncrement();
            mapping.setId(andIncrement);
            getMappingListMap().put(andIncrement,mapping);
            getLongConcurrentHashMap().put(mapping.getUrl(),andIncrement);
            return 1;
        }
        return 0;
    }

    @Override
    public int insertMappingList(List<IBaseMapping> mappingList) {
        for (IBaseMapping baseMapping : mappingList) {
            insertMapping(baseMapping);
        }
        return 1;
    }

    @Override
    public  List<IBaseMapping> selectMappingListAll(){
        return new ArrayList<>(getMappingListMap().values());
    }

    @Override
    public  int updateMapping(IBaseMapping mapping) {
        if (getMappingListMap().containsKey(mapping.getId())){
            IBaseMapping iBaseMapping = getMappingListMap().get(mapping.getId());
            if (mapping.getUrl() != null){
                iBaseMapping.setUrl(mapping.getUrl());
            }
            if (mapping.getPermissionHashSet() != null){
                iBaseMapping.setPermissionHashSet(mapping.getPermissionHashSet());
            }
            if (mapping.getRoleHashSet() != null){
                iBaseMapping.setRoleHashSet(mapping.getRoleHashSet());
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int deletedMappingById(Long mappingId) {

        return getMappingListMap().remove(mappingId) != null ? 1 : 0;
    }

    @Override
    public int deletedMappingList(List<Long> mappingIdList) {
        List<IBaseMapping> baseMappingList = new ArrayList<>();
        for (Long mappingId : mappingIdList) {
            IBaseMapping remove = getMappingListMap().remove(mappingId);
            if (remove != null){
                baseMappingList.add(remove);
            }else {
                // TODO1 删除id不存在
                insertMappingList(baseMappingList);
                return 0;
            }
        }
        return 1;
    }

    @Override
    public  IBaseMapping selectMappingById(Long mappingId) {
        if (getMappingListMap().get(mappingId) == null){
            return null;
        }
        return getMappingListMap().get(mappingId);
    }

    @Override
    public  IBaseMapping selectMappingByUrl(String url) {
        if (getLongConcurrentHashMap().containsKey(url)){
            return getMappingListMap().get(getLongConcurrentHashMap().get(url));
        }

        return null;
    }


}
