package com.fiafeng.mapping.controller;


import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.Interface.IMappingMapper;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.pojo.Vo.FiafengStaticBean;
import com.fiafeng.mapping.pojo.Interface.IBaseMapping;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.IBaseRole;
import com.fiafeng.common.service.ICacheService;
import com.fiafeng.common.service.IPermissionService;
import com.fiafeng.common.service.IRoleService;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mapping.pojo.RequestMappingBean;
import com.fiafeng.mapping.pojo.vo.RequestMappingDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

@RestController
@RequestMapping("/mapping")
@ConditionalEnableProperty("fiafeng.mapping.enable")
public class MappingController {


    @Autowired
    private RequestMappingInfoHandlerMapping requestMappingHandlerMapping;

    @Autowired
    RequestMappingBean requestMappingBean;

    @Autowired
    IMappingMapper mappingMapper;

    @Autowired
    IRoleService roleService;

    @Autowired
    IPermissionService permissionService;

    @Autowired
    ICacheService cacheService;


    @PostMapping("/addPermission")
    public AjaxResult addMappingPermission(@RequestBody JSONObject jsonObject) {
        IBaseMapping defaultMapping = jsonObject.toJavaObject(FiafengSpringUtils.getBean(IBaseMapping.class).getClass());
        IBaseMapping iBaseMapping = checkPermissionList(defaultMapping);
        // 添加权限
        iBaseMapping.getPermissionHashSet().addAll(defaultMapping.getPermissionHashSet());
        iBaseMapping.setRoleHashSet(null);
        if (!mappingMapper.updateMapping(iBaseMapping)) {
            throw new ServiceException("添加权限限制失败");
        }

        return AjaxResult.success("添加权限限制成功");
    }


    @PostMapping("/addUrl")
    public AjaxResult addMappingByUrl(@RequestBody HashMap<String, Object> hashMap) {
        String url = (String) hashMap.getOrDefault("url", "");
        url = FiafengStaticBean.searchTree.valueExistTree(url);
        if (url == null) {
            throw new ServiceException("无法添加url,url不存在");
        }

        RequestMappingDataVO dataVO = getRequestMappingVO(url);
        if (!requestMappingHandlerMapping.getHandlerMethods().containsKey(dataVO.getRequestMappingInfo())) {
            if (mappingMapper.insertMapping(dataVO.toDefaultMapping())) {
                requestMappingHandlerMapping.registerMapping(
                        dataVO.getRequestMappingInfo(),
                        dataVO.getHandlerMethod(),
                        dataVO.getHandlerMethod().getMethod());
                FiafengStaticBean.searchTree.insert(url);
                return AjaxResult.success("添加映射成功");
            }else {
                throw new ServiceException("添加url映射失败");
            }
        }
        return AjaxResult.error("当前url已经存在了");
    }


    @PostMapping("/addRole")
    public AjaxResult addMappingRole(@RequestBody JSONObject jsonObject) {

        IBaseMapping bean = FiafengSpringUtils.getBean(IBaseMapping.class);
        IBaseMapping defaultMapping = jsonObject.toJavaObject(bean.getClass());

        IBaseMapping iBaseMapping = checkRoleList(defaultMapping);
        iBaseMapping.getRoleHashSet().addAll(defaultMapping.getRoleHashSet());
        iBaseMapping.setPermissionHashSet(null);
        if (!mappingMapper.updateMapping(iBaseMapping)) {
            throw new ServiceException("添加角色限制失败");
        }
        return AjaxResult.success("添加角色限制成功");
    }


    @PostMapping("/deletedRole")
    public AjaxResult deletedRole(@RequestBody JSONObject jsonObject) {
        IBaseMapping bean = FiafengSpringUtils.getBean(IBaseMapping.class);
        IBaseMapping defaultMapping = jsonObject.toJavaObject(bean.getClass());

        IBaseMapping iBaseMapping = checkRoleList(defaultMapping);
        for (String roleName : defaultMapping.getRoleHashSet()) {
            iBaseMapping.getRoleHashSet().remove(roleName);
        }
        if (!mappingMapper.updateMapping(iBaseMapping)) {
            throw new ServiceException("添加角色限制失败");
        }
        for (IBaseMapping mapping : requestMappingBean.getBaseMappingList()) {
            if (mapping.getUrl().equals(iBaseMapping.getUrl()) && Objects.equals(mapping.getId(), iBaseMapping.getId())){
                mapping.setRoleHashSet(iBaseMapping.getRoleHashSet());
            }
        }
        return AjaxResult.success("添加角色限制成功");
    }


    @PostMapping("/deletedPermission")
    public AjaxResult deletedMappingPermission(@RequestBody JSONObject jsonObject) {

        IBaseMapping bean = FiafengSpringUtils.getBean(IBaseMapping.class);
        IBaseMapping defaultMapping = jsonObject.toJavaObject(bean.getClass());

        IBaseMapping iBaseMapping = checkPermissionList(defaultMapping);
        // 移除权限
        for (String permissionName : defaultMapping.getPermissionHashSet()) {
            iBaseMapping.getPermissionHashSet().remove(permissionName);
        }
        if (!mappingMapper.updateMapping(iBaseMapping)) {
            throw new ServiceException("添加权限限制失败,请检查id参数");
        }
        for (IBaseMapping mapping : requestMappingBean.getBaseMappingList()) {
            if (mapping.getUrl().equals(iBaseMapping.getUrl()) && Objects.equals(mapping.getId(), iBaseMapping.getId())){
                mapping.setPermissionHashSet(iBaseMapping.getRoleHashSet());
            }
        }


        return AjaxResult.success("添加权限限制成功");
    }


    @PostMapping("/deletedUrl")
    public AjaxResult deletedMappingByUrl(@RequestBody HashMap<String, Object> hashMap) {
        String url = (String) hashMap.getOrDefault("url", "");
        if ("".equals(url)) {
            throw new ServiceException("url不允许为空");
        }
        String deletedUrl = FiafengStaticBean.searchTree.valueExistTree(url);
        if (deletedUrl == null) {
            throw new ServiceException("无法添加url,url不存在");
        }
        RequestMappingDataVO dataVO = getRequestMappingVO(url);
        if (!requestMappingHandlerMapping.getHandlerMethods().containsKey(dataVO.getRequestMappingInfo())) {
            throw new ServiceException("当前url不存在映射");
        }
        if (dataVO.getId() != null) {
            if (!mappingMapper.deletedMappingById(dataVO.getId())) {
                throw new ServiceException("删除映射时出现意料之外i的异常");
            }
        }else {
            IBaseMapping iBaseMapping = mappingMapper.selectMappingByUrl(url);
            if (iBaseMapping == null){
                throw new ServiceException("id和url都不存在数据库");
            }
            if (!mappingMapper.deletedMappingById(iBaseMapping.getId())) {
                throw new ServiceException("删除映射时出现意料之外i的异常");
            }
        }

        requestMappingHandlerMapping.unregisterMapping(dataVO.getRequestMappingInfo());
        FiafengStaticBean.searchTree.removeNode(url);
        return AjaxResult.success("删除映射成功");
    }


    @GetMapping("/getAll")
    public Object printMappings() {
        return mappingMapper.selectMappingListAll();
    }


    /**
     * 检查url，返回对应的注册信息
     */
    private RequestMappingDataVO getRequestMappingVO(String url) {
        if (url == null || url.isEmpty()) {
            throw new ServiceException("url不允许为空");
        }
        if (url.startsWith("/mapping")) {
            throw new ServiceException("不允许删除当前映射");
        }
        HashMap<String, Integer> urlHashMap = requestMappingBean.getUrlHashMap();
        if (!urlHashMap.containsKey(url)) {
            throw new ServiceException("当前url不存在");
        }

        // 通过url获取对应的注册消息
        return requestMappingBean.getDefaultMappingList().get(urlHashMap.get(url));
    }

    /**
     * 检查映射参数是否正确，
     *
     * @param defaultMapping 参数
     */
    private void checkMappingParaByRoleOrPermission(IBaseMapping defaultMapping) {
        if (defaultMapping == null || defaultMapping.getUrl() == null) {
            throw new ServiceException("参数传递错误");
        }
        if (defaultMapping.getId() == null) {
            throw new ServiceException("id不允许为空");
        }
        if (FiafengStaticBean.searchTree.valueExistTree(defaultMapping.getUrl()) == null) {
            throw new ServiceException("无法添加url,url不存在");
        }
    }

    private IBaseMapping checkRoleList(IBaseMapping defaultMapping) {
        checkMappingParaByRoleOrPermission(defaultMapping);
        if (defaultMapping.getRoleHashSet().isEmpty()) {
            throw new ServiceException("角色列表不存在");
        }
        HashSet<String> roleHashSet = new HashSet<>();
        for (IBaseRole iBaseRole : roleService.queryRoleListAll()) {
            roleHashSet.add(iBaseRole.getName());
        }
        if (!defaultMapping.getRoleHashSet().isEmpty()) {
            for (String roleName : defaultMapping.getRoleHashSet()) {
                // 判断角色名是否存在
                if (!roleHashSet.contains(roleName)) {
                    throw new ServiceException("角色不存在，请检查角色列表");
                }
            }
        }
        IBaseMapping iBaseMapping = mappingMapper.selectMappingById(defaultMapping.getId());
        if (iBaseMapping == null) {
            throw new ServiceException("id不存在");
        }
        return iBaseMapping;
    }


    private IBaseMapping checkPermissionList(IBaseMapping defaultMapping) {
        checkMappingParaByRoleOrPermission(defaultMapping);

        if (defaultMapping.getPermissionHashSet().isEmpty()) {
            throw new ServiceException("权限列表不存在");
        }
        HashSet<String> permissionHashSet = new HashSet<>();
        for (IBasePermission iBasePermission : permissionService.queryPermissionListALl()) {
            permissionHashSet.add(iBasePermission.getName());
        }
        if (!defaultMapping.getPermissionHashSet().isEmpty()) {
            for (String permissionName : defaultMapping.getPermissionHashSet()) {
                // 判断角色名是否存在
                if (!permissionHashSet.contains(permissionName)) {
                    throw new ServiceException("权限不存在，请检查权限列表");
                }
            }
        }
        IBaseMapping iBaseMapping = mappingMapper.selectMappingById(defaultMapping.getId());
        if (iBaseMapping == null) {
            throw new ServiceException("id不存在");
        }
        return iBaseMapping;
    }
}
