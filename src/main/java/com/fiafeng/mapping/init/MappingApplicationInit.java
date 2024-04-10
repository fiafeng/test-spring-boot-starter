package com.fiafeng.mapping.init;

import com.fiafeng.common.annotation.BaseMappingAnnotation;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.SpringUtils;
import com.fiafeng.mapping.pojo.DefaultMapping;
import com.fiafeng.mapping.pojo.vo.RequestMappingBean;
import com.fiafeng.mapping.pojo.vo.RequestMappingDataVO;
import com.fiafeng.common.exception.ServiceException;
import com.fiafeng.common.mapper.IMappingMapper;
import com.fiafeng.common.pojo.FiafengStaticBean;
import com.fiafeng.common.pojo.Interface.IBaseMapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;

public class MappingApplicationInit implements ApplicationListener<ContextRefreshedEvent>, BeanPostProcessor, BeanDefinitionRegistryPostProcessor {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    RequestMappingBean requestMappingBean;

    // 如果没有自动配置 DispatcherServlet，则不会注入该 bean
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 防止事件运行多次
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        ObjectClassUtils.refreshBaseMysqlMapperType(IMappingMapper.class, IBaseMapping.class);

        requestMappingHandlerMapping = SpringUtils.getBean(RequestMappingHandlerMapping.class);

        mappingSetting();

    }

    private BeanDefinitionRegistry registry;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ObjectClassUtils.removeBeanDefinitions(registry, beanFactory, IMappingMapper.class);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ObjectClassUtils.addBaseBeanType(bean, IBaseMapping.class, BaseMappingAnnotation.class);
        return bean;
    }

    IMappingMapper mappingMapper = null;

    private void mappingSetting() {
        // 获取系统内部所有的映射添加到bean里面

        mappingMapper = SpringUtils.getBean(IMappingMapper.class);

        // 获取数据库内所有连接
        List<IBaseMapping> baseMappingList = mappingMapper.selectMappingListAll();
        boolean flag = baseMappingList != null && !baseMappingList.isEmpty();
        HashMap<String, IBaseMapping> hashMap = null;
        if (flag) {
            hashMap = new HashMap<>();
            for (IBaseMapping baseMapping : baseMappingList) {
                hashMap.put(baseMapping.getUrl(), baseMapping);
            }
        }

        List<String> notExistent = new ArrayList<>(); // 数据库中不存在的url
        List<String> existentUrl = new ArrayList<>(); // 数据库中使用过的url

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
            if (pathPatternsCondition != null) {
                Set<PathPattern> patterns = pathPatternsCondition.getPatterns();
                for (PathPattern pathPattern : patterns) {
                    String url = pathPattern.getPatternString();
                    FiafengStaticBean.searchTree.insert(url);

                    DefaultMapping baseVO = new DefaultMapping();
                    baseVO.setUrl(url);
                    if (flag && hashMap.containsKey(url)) {
                        // 查询数据库获取所有映射的权限列表和角色列表
                        IBaseMapping iBaseMapping = hashMap.get(url);
                        if (iBaseMapping.getRoleHashSet() != null) {
                            baseVO.setRoleHashSet(iBaseMapping.getRoleHashSet());
                        }
                        if (iBaseMapping.getPermissionHashSet() != null) {
                            baseVO.setRoleHashSet(iBaseMapping.getRoleHashSet());
                        }
                        baseVO.setId(iBaseMapping.getId());
                        existentUrl.add(iBaseMapping.getUrl());

                    } else {
                        notExistent.add(url);
                    }
                    if (requestMappingBean == null) {
                        requestMappingBean = SpringUtils.getBean(RequestMappingBean.class);
                    }

                    requestMappingBean.getBaseMappingList().add(baseVO);

                    requestMappingBean.getUrlHashMap().put(url, requestMappingBean.getDefaultMappingList().size());

                    RequestMappingDataVO requestMappingDataVO = new RequestMappingDataVO();
                    requestMappingDataVO.setUrl(url);
                    requestMappingDataVO.setRequestMappingInfo(requestMappingInfo);
                    requestMappingDataVO.setHandlerMethod(handlerMethod);
                    requestMappingBean.getDefaultMappingList().add(requestMappingDataVO);

                }
            }
        }
        // 将url不存在数据库的添加到数据库
        if (!notExistent.isEmpty()) {
            insertMappingList(notExistent);
        }


        // 将系统内不存在的url从数据库中删除
        if (!existentUrl.isEmpty()){
            for (String url : existentUrl) {
                hashMap.remove(url);
            }
            if (!hashMap.isEmpty()){
                List<Long> longs = new ArrayList<>();
                for (IBaseMapping value : hashMap.values()) {
                    longs.add(value.getId());
                }
                mappingMapper.deletedMappingList(longs);
            }
        }

        // 删除多余的之后再次搜索所有数据
        List<IBaseMapping> mappings = mappingMapper.selectMappingListAll();
        HashMap<String, IBaseMapping> mappingHashMap = new HashMap<>();
        for (IBaseMapping mapping : mappings) {
            mappingHashMap.put(mapping.getUrl(),mapping);
        }

        // 给所有没有id的值赋值
        for (DefaultMapping baseVO : requestMappingBean.getBaseMappingList()) {
            if (baseVO.getId() == null){
                IBaseMapping iBaseMapping = mappingHashMap.get(baseVO.getUrl());
                baseVO.setId(iBaseMapping.getId());
            }

        }


    }

    private IBaseMapping getMapping(String url) {
        IBaseMapping object;
        try {
            object = SpringUtils.getBean(IBaseMapping.class);
            object.setUrl(url);

        } catch (Exception e) {
            throw new ServiceException("创建mapping映射实体类时出现意外的异常：" + e.getMessage());
        }
        return object;
    }

    public void insertMappingList(List<String> urlList) {
        List<IBaseMapping> baseMappingList = new ArrayList<>();
        for (String url : urlList) {
            baseMappingList.add(getMapping(url));
        }
        mappingMapper.insertMappingList(baseMappingList);


    }
}
