package com.fiafeng.dynamicClass.controller;


import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.dynamicClass.pojo.DynamicClass;
import com.fiafeng.dynamicClass.pojo.DynamicMethod;
import com.fiafeng.dynamicClass.utils.DynamicASMUtils;
import com.fiafeng.dynamicClass.utils.DynamicUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.fiafeng.dynamicClass.utils.DynamicASMUtils.getDynamicClass;

@RestController
@RequestMapping("/dynamicClass")
public class DynamicClassController {

    HashMap<String, HashSet<Class<?>>> packageClassHashMap = new HashMap<>();


    @GetMapping("/getSystemAllClass")
    @ResponseBody
    public AjaxResult getSystemAllClass(String basePackageName) throws IOException, NoSuchFieldException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        if (StringUtils.strIsEmpty(basePackageName)) {
            basePackageName = "com.fiafeng";
        }

        HashSet<Class<?>> classHashSet = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader != null) {
            classHashSet.addAll(getLoaderClasses(classLoader, basePackageName));
            classLoader = classLoader.getParent();
            if (!packageClassHashMap.containsKey(basePackageName)) {
                packageClassHashMap.put(basePackageName, classHashSet);
            }
        }
        return AjaxResult.success(packageClassHashMap.get(basePackageName));
    }


    @GetMapping("/getSystemClassInfo")
    @ResponseBody
    public AjaxResult getSystemClassInfo(String className) throws ClassNotFoundException, IOException, InvocationTargetException, IllegalAccessException {
        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }
        Class<?> aClass = Class.forName(className);
        DynamicClass dynamicClass = getDynamicClass(aClass);

        return AjaxResult.success(dynamicClass);

    }


    @GetMapping("/getSystemClassInfoV1")
    @ResponseBody
    public AjaxResult getSystemClassInfoV1(String className) throws ClassNotFoundException, IOException {
        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }
        List<DynamicMethod> dynamicMethods = DynamicASMUtils.getDynamicMethods(Class.forName(className));

        return AjaxResult.success(dynamicMethods);

    }

//    @GetMapping("/getSystemClassInfoV2")
    @PostMapping("/getSystemClassInfoV2")
    @ResponseBody
    public AjaxResult getSystemClassInfoV2(String className, @RequestBody JSONObject jsonObject) throws ClassNotFoundException, IOException {
        String string = jsonObject.getString("className");
        if (string != null){
            className = string;
        }

        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }
       DynamicClass dynamicClass = DynamicUtils.getDynamicClass(Class.forName(className));

        return AjaxResult.success(dynamicClass);

    }


    public HashSet<Class<?>> getLoaderClasses(ClassLoader classLoader, String basePackageName) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        HashSet<Class<?>> classHashSet = null;
        Field classFiled = ClassLoader.class.getDeclaredField("classes");
        classFiled.setAccessible(true);
        Vector<Class<?>> classVector = (Vector<Class<?>>) classFiled.get(classLoader);

        classHashSet = new HashSet<>();
        CopyOnWriteArrayList<Class<?>> arrayList = new CopyOnWriteArrayList<>(classVector);

        List<DynamicClass> dynamicClassList = new ArrayList<>();
        for (Class<?> aClass : arrayList) {
            String packageName = aClass.getPackage() == null ? "" : aClass.getPackage().getName();
            if (packageName.startsWith(basePackageName)) {
                DynamicClass dynamicClass = getDynamicClass(aClass);
                classHashSet.add(aClass);
                dynamicClassList.add(dynamicClass);
            }
        }

        return classHashSet;
    }


}
