package com.fiafeng.common.config;

import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.common.Interceptor.FiafengI18nInterceptor;
import com.fiafeng.common.properties.FiafengI18NProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class I18nBeanConfig {

    @Autowired
    FiafengI18NProperties i18nProperties;


    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        String resourcePattern = "classpath*:i18n/*message*";
        List<String> stringList = scanModelsForI18nFolders(resourcePattern);
        HashSet<String> hashSet = new HashSet<>(stringList);
        if (i18nProperties.messagePath.contains("/")) {
            int index = i18nProperties.messagePath.lastIndexOf("/");
            String string = "*" + i18nProperties.messagePath.substring(index + 1) + "*";
            resourcePattern = "classpath*:" + i18nProperties.messagePath.substring(0, index) + string;
            List<String> list = scanModelsForI18nFolders(resourcePattern);
            hashSet.addAll(list);
        } else {
            resourcePattern = "classpath*:*" + i18nProperties.messagePath + "*";
        }

        List<String> list = scanModelsForI18nFolders(resourcePattern);
        hashSet.addAll(list);

        String[] strings = hashSet.toArray(new String[0]);
        messageSource.setBasenames(strings);
        messageSource.setDefaultEncoding("UTF-8");
        log.info("加载messageSource：{}", StringUtils.join(Arrays.stream(strings).map(String::toString).collect(Collectors.toList()), ","));
        messageSource.getMessage("user.password.not.match", null, Locale.SIMPLIFIED_CHINESE);
        return messageSource;
    }

    public List<String> scanModelsForI18nFolders(String resourcePattern) {
        try {
            String pri;

            pri = resourcePattern.substring(0, resourcePattern.lastIndexOf(":"));
            pri = pri.replaceAll("\\*", "");
            if (resourcePattern.contains("/")) {
                pri = pri + resourcePattern.substring(resourcePattern.indexOf(":"), resourcePattern.lastIndexOf("/") + 1);
            }
            // 通配规则 直接获取 Resources中的内容

            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(resourcePattern);
            List<String> list = new ArrayList<>();

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                String[] urlStrArr = resource.getURL().toString().split("/");
                int index = urlStrArr.length - 1;
                String moduleName = urlStrArr[index];
                // 通配符加+ 模块名 + /国际化数据目录
                list.add(pri + moduleName.substring(0, moduleName.length() - 11));
            }
            return list;

        } catch (Exception e) {

        }
        return new ArrayList<>();
    }
}
