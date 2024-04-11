package com.fiafeng.common.annotation.conditional.matches;

import com.fiafeng.common.annotation.conditional.ConditionalEnableProperty;
import com.fiafeng.common.properties.IEnableProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class ConditionalPropertyMatches implements Condition {

    static HashMap<String, IEnableProperties> iPropertiesHashMap = new HashMap<>();


    static {
        try {
            ProtectionDomain protectionDomain = ConditionalPropertyMatches.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URL location = codeSource.getLocation();
            String path = location.getPath();
            File pathFile = new File(path);
            File[] files = pathFile.listFiles();
            // 在判断是否在源码项目
            if (files != null) {
                // 不是源码项目，是引入了jar
                for (File file : files) {
                    getSubFile(file);
                }
            } else {

                JarFile jarFile = new JarFile(pathFile);
                // 创建URLClassLoader
                URL url = pathFile.toURI().toURL(); // 将jar文件转换为URL
                URL[] urls = new URL[]{url};
                ClassLoader urlClassLoader = new URLClassLoader(urls);

                // 遍历Jar文件中的所有条目
                jarFile.stream().forEach(entry -> {
                    String name = entry.getName();
                    if (name.contains("properties") && name.endsWith(".class")) {
                        String className = name.substring(0, name.lastIndexOf('.')).replace('/', '.');
                        try {
                            Class<?> loadClass = urlClassLoader.loadClass(className);// 加载类
                            addPropertiesClass(className, loadClass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }

    private static void ooo(String path, String fileName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (fileName.contains("properties")) {
            Class<?> fileClass = Class.forName(fileName);
            addPropertiesClass(path, fileClass);
        }
    }

    private static void addPropertiesClass(String classPath, Class<?> fileClass) throws InstantiationException, IllegalAccessException {
        for (Type type : fileClass.getGenericInterfaces()) {
            if (type == IEnableProperties.class) {
                IEnableProperties iEnableProperties = (IEnableProperties) fileClass.newInstance();
                String beanName = classPath.substring(classPath.lastIndexOf(".") + 1);
                if (beanName.startsWith("Fiafeng") && beanName.endsWith("Properties")) {
                    beanName = beanName.substring(7, 8).toLowerCase() + beanName.substring(8, beanName.length() - 10);
                    iPropertiesHashMap.put(beanName, iEnableProperties);
                }

            }
        }
    }

    public static void getSubFile(File checkFile) {
        File[] files = checkFile.listFiles();
        if (files != null) {
            try {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getSubFile(file);
                    }else if (file.isFile() && file.getName().endsWith(".class")) {
                        String path = file.getPath();
                        path = path.substring(path.indexOf("com\\fiafeng"), path.indexOf(".class")).replaceAll("\\\\", ".");
                        ooo(path, path);
                    }

                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public boolean matchDefaultProperties(String key, boolean value) {
        if (key.startsWith("fiafeng.") && key.endsWith(".enable")) {
            String name = key.substring(8, key.length() - 7);
            if (iPropertiesHashMap.containsKey(name)) {
                return iPropertiesHashMap.get(name).getEnable() == value;
            } else {
                throw new RuntimeException("请建立名字为Fiafeng" + name + "Properties的类，并且需要继承，并且将类放在properties包下IProperties接口");
            }
        }
        return true;
    }


    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalEnableProperty.class.getName());
        if (annotationAttributes == null) {
            return true;
        }
        Object object = annotationAttributes.get("value");
        if (!(object instanceof String)) {
            return true;
        }
        String name = (String) object;
        if (name.lastIndexOf("enable") == -1) {
            throw new RuntimeException("输入的name必须以enable结尾");
        }

        boolean value = (boolean) annotationAttributes.get("enable");
        Environment environment = context.getEnvironment();
        String property = environment.getProperty(name);
        if (property == null) {
            return matchDefaultProperties(name, value);
        } else {
            try {
                return Boolean.parseBoolean(property) == value;
            } catch (Exception e) {
                throw new RuntimeException(name + "的值必须是true或者false");
            }
        }
    }

}
