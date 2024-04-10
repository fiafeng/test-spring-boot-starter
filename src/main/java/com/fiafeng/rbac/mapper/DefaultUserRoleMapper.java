package com.fiafeng.rbac.mapper;


import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.mapper.IUserRoleMapper;
import com.fiafeng.common.pojo.Interface.IBaseUserRole;
import com.fiafeng.rbac.pojo.DefaultUserRole;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@BeanDefinitionOrderAnnotation()
public class DefaultUserRoleMapper implements IUserRoleMapper {

    ConcurrentHashMap<Long, HashMap<Long, Long>> userRoleMap;


    AtomicLong atomicLong = new AtomicLong(1);

    public ConcurrentHashMap<Long, HashMap<Long, Long>> getUserRoleMap() {
        if (userRoleMap == null) {
            userRoleMap = new ConcurrentHashMap<>();
            HashMap<Long,Long> userHashMap = new HashMap<>();
            userHashMap.put(1L, 1L);
            userRoleMap.put(1L, userHashMap);
        }
        return userRoleMap;
    }


    /**
     * @param userRole 用户角色关系
     * @param <T> 用户角色接口
     * @return 用户角色关系
     */
    @Override
    public <T extends IBaseUserRole> boolean insertUserRole(T userRole) {
        if (getUserRoleMap().containsKey(userRole.getUserId())) {
            getUserRoleMap().get(userRole.getUserId()).put(userRole.getRoleId(),atomicLong.getAndIncrement());
        } else {
            HashMap<Long, Long> roleHashset = new HashMap<>();
            roleHashset.put(userRole.getRoleId(), atomicLong.getAndIncrement());
            getUserRoleMap().put(userRole.getUserId(), roleHashset);
        }
        return true;
    }

    /**
     * @param userId 用户Id
     * @param roleIdList 用户角色列表
     * @return 用户角色列表
     */
    @Override
    public boolean updateUserRoleList(Long userId, List<Long> roleIdList) {
        return false;
    }

    /**
     * @param userRole 用户角色关系
     * @param <T> 用户角色接口
     * @return s
     */
    @Override
    public <T extends IBaseUserRole> boolean deleteUserRole(T userRole) {
        if (getUserRoleMap().containsKey(userRole.getUserId())) {
            if (getUserRoleMap().get(userRole.getUserId()).containsKey(userRole.getRoleId())){
                return getUserRoleMap().get(userRole.getUserId()).remove(userRole.getRoleId()) == null;
            }

        }
        return false;
    }

    @Override
    public boolean deleteUserRoleById(Long id) {
        for (Long userId : getUserRoleMap().keySet()) {
            HashMap<Long, Long> role = getUserRoleMap().get(userId);
            HashSet<Long>  hashSet = new HashSet<>(role.values());
            if (hashSet.contains(id)){
                for (Long roleId : role.keySet()) {
                    if (Objects.equals(role.get(roleId), id)){
                        return Objects.equals(role.remove(roleId), roleId);

                    }
                }
            }
        }

        return false;
    }

    /**
     * @param userId 用户Id
     * @return 用户拥有的角色信息列表
     */
    @Override
    public List<Long> selectRoleIdListByUserId(Long userId) {
        if (getUserRoleMap().containsKey(userId)){
            return new ArrayList<>(getUserRoleMap().get(userId).keySet());
        }

        return null;
    }

    @Override
    public <T extends IBaseUserRole> List<T> selectRoleListByUserIdRoleId(Long userId) {
        List<IBaseUserRole> rolePermissionList = new ArrayList<>();
        if (getUserRoleMap().containsKey(userId)){
            HashMap<Long, Long> longLongHashMap = getUserRoleMap().get(userId);
            for (Long roleId : longLongHashMap.keySet()) {
                rolePermissionList.add(
                        new DefaultUserRole()
                                .setUserId(userId)
                                .setRoleId(roleId)
                                .setId(longLongHashMap.get(roleId))
                );
            }
        }

        return (List<T>) rolePermissionList;

    }

    @Override
    public <T extends IBaseUserRole> List<T> selectRoleListByRoleId(Long roleId) {
        List<IBaseUserRole> userRoleList = new ArrayList<>();
        for (Long userId : getUserRoleMap().keySet()) {
            HashMap<Long, Long> role = getUserRoleMap().get(userId);
            if (role.containsKey(roleId)) {
                DefaultUserRole defaultUserRole = new DefaultUserRole()
                        .setRoleId(roleId)
                        .setId(role.get(roleId))
                        .setUserId(userId);
                userRoleList.add(defaultUserRole);
            }
        }
        return (List<T>) userRoleList;
    }

    @Override
    public <T extends IBaseUserRole> T selectRoleListByUserIdRoleId(T userRole) {
        if (getUserRoleMap().containsKey(userRole.getUserId())) {
            HashMap<Long, Long> hashMap = getUserRoleMap().get(userRole.getUserId());
            if (hashMap.containsKey(userRole.getRoleId())) {
                return (T) new DefaultUserRole()
                        .setUserId(userRole.getUserId())
                        .setRoleId(userRole.getRoleId())
                        .setId(hashMap.get(userRole.getRoleId()));
            }
        }
        return null;
    }

    @Override
    public <T extends IBaseUserRole> T selectRoleListById(Long id) {
        for (Long userId : getUserRoleMap().keySet()) {
            HashMap<Long, Long> role = getUserRoleMap().get(userId);
            HashSet<Long>  hashSet = new HashSet<>(role.values());
            if (hashSet.contains(id)){
                for (Long roleId : role.keySet()) {
                    if (Objects.equals(role.get(roleId), id)){
                        DefaultUserRole defaultUserRole = new DefaultUserRole()
                                .setRoleId(roleId)
                                .setId(id)
                                .setUserId(userId);

                        return (T) defaultUserRole;

                    }
                }
            }
        }
        return null;
    }
}
