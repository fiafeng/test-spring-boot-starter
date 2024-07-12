package com.fiafeng.dynamicClass.controller;


import com.fiafeng.common.pojo.Dto.AjaxResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@RestController
@RequestMapping("/dynamicClass")
public class DynamicClassController {

    HashMap<String,  HashSet<Class<?>>> packageClassHashMap = new HashMap<>();

    @GetMapping("/getSystemAllClass")
    @ResponseBody
    public AjaxResult getSystemAllClass(String packageName) {
        if (packageName == null) {
            packageName = "con.fiafeng";
        }
        if (!packageClassHashMap.containsKey(packageName)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            while (classLoader != null) {
                getLoaderClasses(classLoader, packageName);
                classLoader = classLoader.getParent();

            }
        }
        return AjaxResult.success(packageClassHashMap.get(packageName));
    }


    public void getLoaderClasses(ClassLoader classLoader, String packageName) {
        try {
            Field classFiled = ClassLoader.class.getDeclaredField("classes");
            classFiled.setAccessible(true);
            Vector<Class<?>> classVector = (Vector<Class<?>>) classFiled.get(classLoader);

            HashSet<Class<?>> classHashSet = new HashSet<>();
            for (Class<?> aClass : classVector) {
                if (aClass.getName().startsWith(packageName)) {
                    classHashSet.add(aClass);
                    Package classPackage = aClass.getPackage();
                    Class<?> componentType = aClass.getComponentType();
                    AnnotatedType[] annotatedInterfaces = aClass.getAnnotatedInterfaces();
                    Class<?>[] aClassInterfaces = aClass.getInterfaces();
                    boolean isInterface = aClass.isInterface();
                    boolean isAnnotation = aClass.isAnnotation();
                    Method[] aClassMethods = aClass.getMethods();


                }
            }
            if (!packageClassHashMap.containsKey(packageName)){
                packageClassHashMap.put(packageName, classHashSet);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }


    }
}
