package com.fiafeng.common.mapper;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;



@BeanDefinitionOrderAnnotation()
@Component
public class DefaultUserRoleMapper implements IUserRoleMapper {

    ConcurrentHashMap<Long, IBaseUserRole> userRoleMap;


    AtomicLong atomicLong = new AtomicLong(1);

    public ConcurrentHashMap<Long,IBaseUserRole> getUserRoleMap() {
        if (userRoleMap == null) {
            userRoleMap = new ConcurrentHashMap<>();
            IBaseUserRole userRole = FiafengSpringUtils.getBeanObject(IBaseUserRole.class);
            userRole.setId(1L);
            userRole.setRoleId(1L);
            userRole.setUserId(1L);
            userRoleMap.put(1L, userRole);
        }
        return userRoleMap;
    }


    /**
     * @param userRole 用户角色关系
     * @return 用户角色关系
     */
    @Override
    public  int insertUserRole(IBaseUserRole userRole) {
        for (IBaseUserRole iBaseUserRole : getUserRoleMap().values()) {
            if (Objects.equals(iBaseUserRole.getUserId(), userRole.getUserId()) && Objects.equals(iBaseUserRole.getRoleId(), userRole.getRoleId())){
                throw new ServiceException("新增角色用户时候，当前用户已经拥有当前当前橘色");
            }
        }

        long andIncrement = atomicLong.getAndIncrement();
        userRole.setId(andIncrement);
        getUserRoleMap().put(andIncrement, userRole);
        return 1;
    }

    /**
     * @param userId     用户Id
     * @param roleIdList 用户角色列表
     * @return 用户角色列表
     */
    @Override
    public int updateUserRoleList(Long userId, List<Long> roleIdList) {
        List<Long> longList = new ArrayList<>();
        for (IBaseUserRole iBaseRolePermission : getUserRoleMap().values()) {
            if (Objects.equals(iBaseRolePermission.getUserId(), userId)) {
                longList.add(iBaseRolePermission.getId());
            }
        }
        for (Long id : longList) {
            getUserRoleMap().remove(id);
        }
        for (Long roleId : roleIdList) {
            IBaseUserRole userRole = FiafengSpringUtils.getBeanObject(IBaseUserRole.class);
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            insertUserRole(userRole);

        }


        return 0;
    }

    /**
     * @param userRole 用户角色关系
     * @return s
     */
    @Override
    public  int deleteUserRole(IBaseUserRole userRole) {
        if (userRole.getId() != null){
            deleteUserRoleById(userRole.getId());
        }
        if (userRole.getUserId() == null ||userRole.getRoleId() == null){
            throw new ServiceException("准备删除用户角色关系时，参数缺少");
        }


        for (IBaseUserRole iBaseRolePermission : getUserRoleMap().values()) {
            if (Objects.equals(iBaseRolePermission.getRoleId(), userRole.getRoleId())
                    && Objects.equals(iBaseRolePermission.getUserId(), userRole.getUserId())) {
                getUserRoleMap().remove(iBaseRolePermission.getId());
                return 1;
            }
        }


        return 0;
    }

    @Override
    public int deleteUserRoleById(Long id) {
        getUserRoleMap().remove(id);
        return 1;
    }

    /**
     * @param userId 用户Id
     * @return 用户拥有的角色信息列表
     */
    @Override
    public List<Long> selectRoleIdListByUserId(Long userId) {
        List<Long> longList = new ArrayList<>();
        for (IBaseUserRole userRole : getUserRoleMap().values()) {
            if (Objects.equals(userRole.getUserId(), userId)) {
                longList.add(userRole.getId());
            }
        }
        return longList;
    }

    @Override
    public  List<IBaseUserRole> selectUserRoleListByUserId(Long userId) {
        List<IBaseUserRole> userRoleList = new ArrayList<>();
        for (IBaseUserRole userRole : getUserRoleMap().values()) {
            if (Objects.equals(userRole.getUserId(), userId)) {
                userRoleList.add(ObjectUtils.getNewObejct(userRole));
            }
        }

        return userRoleList;

    }

    @Override
    public  List<IBaseUserRole> selectRoleListByRoleId(Long roleId) {
        List<IBaseUserRole> userRoleList = new ArrayList<>();
        for (IBaseUserRole userRole : getUserRoleMap().values()) {
            if (Objects.equals(userRole.getRoleId(), roleId)) {
                userRoleList.add(ObjectUtils.getNewObejct(userRole));
            }
        }

        return userRoleList;
    }

    @Override
    public  IBaseUserRole selectUserRoleByUserRole(IBaseUserRole userRole) {
        for (IBaseUserRole iBaseUserRole : getUserRoleMap().values()) {
            if (Objects.equals(iBaseUserRole.getRoleId(), userRole.getRoleId())
                    && Objects.equals(iBaseUserRole.getUserId(), userRole.getUserId())) {
                return ObjectUtils.getNewObejct(iBaseUserRole);
            }
        }
        return null;
    }

    @Override
    public  IBaseUserRole selectRoleListById(Long id) {
        if (getUserRoleMap().containsKey(id)){
            return ObjectUtils.getNewObejct(getUserRoleMap().get(id));
        }
        return null;
    }
}
