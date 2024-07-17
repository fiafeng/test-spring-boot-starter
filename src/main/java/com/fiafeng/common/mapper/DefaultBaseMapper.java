package com.fiafeng.common.mapper;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.utils.ObjectUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultBaseMapper {

    private ConcurrentHashMap<Long, IBasePojo> objectListMap;

    private final AtomicLong atomicLong = new AtomicLong(1);

    @Getter
    @Setter
    private String idName = "id";


    public ConcurrentHashMap<Long, IBasePojo> getObjectListMap() {
        if (objectListMap == null) {
            objectListMap = new ConcurrentHashMap<>();
        }

        return objectListMap;
    }


    public int insertObject(IBasePojo basePojo) {
        long andIncrement;
        synchronized (this) {
            andIncrement = atomicLong.getAndIncrement();
            basePojo.setId(andIncrement);
        }
        getObjectListMap().put(andIncrement, basePojo);
        return 1;
    }


    public int deletedById(Long id) {
        if (!getObjectListMap().containsKey(id)) {
            return -1;
        }
        return getObjectListMap().remove(id) == null ? 0 : 1;
    }


    public int updateById(IBasePojo basePojo) {
        if (getObjectListMap().containsKey(basePojo.getId())) {
            return -1;
        }
        IBasePojo pojo = getObjectListMap().get(basePojo.getId());
        for (Field field : pojo.getClass().getFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue1 = field.get(pojo);
                Object fieldValue2 = field.get(basePojo);
                if (fieldValue1 != null && fieldValue1 != fieldValue2) {
                    field.set(pojo, fieldValue1);
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        return 1;
    }

    public IBasePojo selectById(Long id) {
        if (getObjectListMap().containsKey(id)) {
            return ObjectUtils.getNewObejct(getObjectListMap().get(id));
        }
        return null;
    }

    public List<IBasePojo> selectList() {
        List<IBasePojo> basePojoList = new ArrayList<>();
        for (Long id : getObjectListMap().keySet()) {
            basePojoList.add(ObjectUtils.getNewObejct(getObjectListMap().get(id)));
        }
        return basePojoList;
    }

    private List<IBasePojo> selectListByObject(IBasePojo iBasePojo) {
        List<IBasePojo> basePojoList = new ArrayList<>();
        List<Field> fieldList = new ArrayList<>();
        for (Field field : iBasePojo.getClass().getFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue1 = field.get(iBasePojo);
                if (fieldValue1 == null) {
                    continue;
                }
                fieldList.add(field);
            } catch (IllegalAccessException ignore) {

            }
        }
        for (IBasePojo pojo : getObjectListMap().values()) {
            boolean flag = true;
            for (Field field : fieldList) {
                try {
                    if (field.get(pojo) != field.get(iBasePojo)) {
                        flag = false;
                        break;
                    }
                } catch (IllegalAccessException ignore) {
                }
            }
            if (flag) {
                basePojoList.add(pojo);
            }
        }
        return basePojoList;
    }

}
