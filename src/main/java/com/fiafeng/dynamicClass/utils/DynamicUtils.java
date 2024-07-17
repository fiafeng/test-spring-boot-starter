package com.fiafeng.dynamicClass.utils;

import com.fiafeng.common.utils.ObjectUtils;
import com.fiafeng.dynamicClass.pojo.*;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.ParameterNode;
import jdk.internal.org.objectweb.asm.util.CheckSignatureAdapter;

import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DynamicUtils {

    private static HashMap<String, Class<?>> hashMap = new HashMap<>();

    static {
        hashMap.put("void", void.class);
        hashMap.put("int", int.class);
        hashMap.put("char", char.class);
        hashMap.put("short", short.class);
        hashMap.put("long", long.class);
        hashMap.put("float", float.class);
        hashMap.put("byte", byte.class);
        hashMap.put("boolean", boolean.class);
    }

    static List<String> annotationMethodNameIgnoreList = Arrays.asList("equals", "hashCode", "toString", "annotationType");
    static List<String> classMethodNameIgnoreList = Arrays.asList("equals", "hashCode", "toString", "annotationType", "wait", "getClass", "notify", "notifyAll");

    public static DynamicClass getDynamicClass(Class<?> aClass) throws IllegalAccessException, InvocationTargetException, IOException, ClassNotFoundException {
        DynamicClass dynamicClass = new DynamicClass();
        String aClassName = aClass.getSimpleName();

        boolean isInterface = aClass.isInterface();
        boolean isAnnotation = aClass.isAnnotation();
        boolean isAnonymousClass = aClass.isAnonymousClass();


        dynamicClass.setName(aClassName);
        String aClassPackageName = aClass.getPackage() != null ? aClass.getPackage().getName() : "";
        dynamicClass.setPackageName(aClassPackageName);
        dynamicClass.setType(aClass);
        dynamicClass.setInterface(isInterface);
        dynamicClass.setAnnotation(isAnnotation);
        dynamicClass.setAnonymousClass(isAnonymousClass);

        List<DynamicAnnotation> dynamicAnnotationList = new ArrayList<>();
        dynamicClass.setAnnotationList(dynamicAnnotationList);


        // 获取属性信息
        List<DynamicField> dynamicFieldList = new ArrayList<>();
        dynamicClass.setFieldList(dynamicFieldList);
        for (Field aClassField : aClass.getFields()) {
            DynamicField dynamicField = new DynamicField();
            String fieldName = aClassField.getName();
            Class<?> fieldType = aClassField.getType();
            List<DynamicAnnotation> annotatedList = getDynamicAnnotates(aClassField.getAnnotations());
            dynamicField.setAnnotatedList(annotatedList);
            dynamicField.setName(fieldName);
            dynamicField.setModifiers(aClassField.getModifiers());
            dynamicField.setType(fieldType);
            dynamicFieldList.add(dynamicField);
        }


        List<DynamicMethod> dynamicMethodList = new ArrayList<>();
        Method[] aClassMethods = aClass.getMethods();
        // 获取方法信息
        dynamicClass.setMethodList(new ArrayList<>());
        for (Method classMethod : aClassMethods) {
            String classMethodName = classMethod.getName();
            if (classMethodNameIgnoreList.contains(classMethodName)) {
                continue;
            }

            DynamicMethod dynamicMethod = getDynamicMethod(classMethod, aClass);
//            dynamicMethodList.add(dynamicMethod);
            dynamicClass.getMethodList().add(dynamicMethod);
        }

        dynamicClass.setParentClass(aClass.getSuperclass());


        Class<?>[] aClassInterfaces = aClass.getInterfaces();
        // 获取实现的接口信息
        for (Class<?> aClassInterface : aClassInterfaces) {

        }

        // 获取类上面的注解信息
        Annotation[] annotatedTypes = aClass.getAnnotations();
        List<DynamicAnnotation> dynamicAnnotations = getDynamicAnnotates(annotatedTypes);
        dynamicClass.setAnnotationList(dynamicAnnotations);
        return dynamicClass;
    }

    private static List<DynamicInterface> getClassInterfaces(Class<?>[] aClassInterfaces) {
        List<DynamicInterface> dynamicInterfaceList = new ArrayList<>();
        // 获取实现的接口信息
        for (Class<?> aClassInterface : aClassInterfaces) {
            String interfaceSimpleName = aClassInterface.getSimpleName();
            String interfacePackageName = aClassInterface.getPackage() != null ? aClassInterface.getPackage().getName() : "";


            DynamicInterface dynamicInterface = new DynamicInterface();


            dynamicInterfaceList.add(dynamicInterface);
        }


        return dynamicInterfaceList;
    }

    private static List<DynamicAnnotation> getDynamicAnnotates(Annotation[] annotations) throws InvocationTargetException, IllegalAccessException {
        return getDynamicAnnotates(annotations, true);
    }

    private static List<DynamicAnnotation> getDynamicAnnotates(Annotation[] annotations, boolean flag) throws InvocationTargetException, IllegalAccessException {
        List<DynamicAnnotation> dynamicAnnotationList = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Target.class
                    || annotation.annotationType() == Retention.class
                    || annotation.annotationType() == Documented.class
                    || annotation.annotationType() == Inherited.class) {
                continue;
            }

            DynamicAnnotation dynamicAnnotation = new DynamicAnnotation();
            Class<? extends Annotation> annotationType = annotation.annotationType();
            String annotationName = annotationType.getSimpleName();
            dynamicAnnotation.setName(annotationName);
            dynamicAnnotation.setType(annotationType);

            String annotationPackageName = annotationType.getPackage() == null ? "" : annotationType.getPackage().getName();
            dynamicAnnotation.setPackageName(annotationPackageName);
            List<DynamicAnnotationMethod> dynamicMethodList = new ArrayList<>();
            Method[] annotationMethods = annotationType.getMethods();
            dynamicAnnotation.setValueList(dynamicMethodList);
            for (Method annotationMethod : annotationMethods) {
                String annotationMethodName = annotationMethod.getName();
                if (annotationMethodNameIgnoreList.contains(annotationMethodName)) {
                    continue;
                }
                Class<?> returnType = annotationMethod.getReturnType();
                List<DynamicArg> dynamicArgList = new ArrayList<>();
                Object defaultValue = annotationMethod.invoke(annotation);

                DynamicAnnotationMethod dynamicAnnotationMethod = new DynamicAnnotationMethod();

                dynamicAnnotationMethod.setDefaultValue(defaultValue);
                dynamicAnnotationMethod.setReturnType(returnType);
                dynamicAnnotationMethod.setArgList(dynamicArgList);


                dynamicMethodList.add(dynamicAnnotationMethod);
            }
            dynamicAnnotationList.add(dynamicAnnotation);
            Annotation[] annotationTypeAnnotations = annotationType.getAnnotations();
            if (flag) {
                List<DynamicAnnotation> dynamicAnnotations = getDynamicAnnotates(annotationTypeAnnotations, false);
                dynamicAnnotation.setParentAnnotated(dynamicAnnotations);
            }
        }

        return dynamicAnnotationList;
    }

    private static DynamicMethod getDynamicMethod(Method classMethod, Class<?> aclass) throws IOException, ClassNotFoundException {
        DynamicMethod classMethodDynamicMethod = new DynamicMethod();
        String methodName = classMethod.getName();
        classMethodDynamicMethod.setName(methodName);

        Class<?> returnType = classMethod.getReturnType();
//        classMethodDynamicMethod.setParentObject(dynamicClass);
//        classMethodDynamicMethod.setReturnType(returnType);
        int modifiers = classMethod.getModifiers();
        classMethodDynamicMethod.setModifiers(modifiers);

        Class<?>[] parameterTypes = classMethod.getParameterTypes();
        List<DynamicArg> dynamicArgList = getDynamicArgs(parameterTypes, classMethod, aclass);
        classMethodDynamicMethod.setArgList(dynamicArgList);

        return classMethodDynamicMethod;
    }

    private static List<DynamicArg> getDynamicArgs(Class<?>[] parameterTypes, Method classMethod, Class<?> aclass) throws IOException {

        List<DynamicArg> dynamicArgList = new ArrayList<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            DynamicArg dynamicArg = new DynamicArg();
            String parameterTypeSimpleName = "arg_" + parameterType.getSimpleName() + "_" + i;
            String parameterPackageName = parameterType.getPackage() != null ? parameterType.getPackage().getName() : "";

            if (Collection.class.isAssignableFrom(parameterType)) {
                Class<?> componentType = parameterType.getComponentType();
                dynamicArg.setComponentType(componentType);
                dynamicArg.setCollection(true);
            }

            dynamicArg.setName(parameterTypeSimpleName);
            dynamicArg.setPackageName(parameterPackageName);
            dynamicArg.setType(parameterType);
            dynamicArgList.add(dynamicArg);
        }

        return dynamicArgList;
    }


    public static List<DynamicMethod> getDynamicMethods(Class<?> aclass) throws IOException, ClassNotFoundException {
        List<DynamicMethod> dynamicMethodList = new ArrayList<>();

        Method[] aClassMethods = aclass.getMethods();


        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(aclass.getPackage().getName() + "." + aclass.getSimpleName());
        classReader.accept(classNode, 0);
        // 遍历方法节点
        List<MethodNode> methods = classNode.methods;
        for (int i = 0; i < methods.size(); i++) {
            MethodNode method = methods.get(i);
            // 方法名字
            String methodName = method.name;
            // 返回值
            String returnClassName = Type.getMethodType(method.desc).getReturnType().getClassName();
            Type[] types = Type.getArgumentTypes(method.desc);

            // 参数
            List<ParameterNode> parameters = method.parameters;

            Class<?> returnClassType = getClassType(returnClassName);
            if (getClassType(returnClassName) == null) {
                returnClassType = Class.forName(returnClassName);
            }

            DynamicMethod dynamicMethod = new DynamicMethod();
            List<DynamicArg> dynamicArgList = new ArrayList<>();
            for (Method classMethod : aClassMethods) {
                if (classMethod.getName().equals(methodName)) {
                    Class<?>[] parameterTypes = classMethod.getParameterTypes();
                    if (ObjectUtils.isNotNull(parameters) && !parameters.isEmpty() && parameterTypes.length == parameters.size()) {
                        int i1 = 0;
                        for (; i1 < parameterTypes.length; i1++) {
                            Class<?> classParamType = parameterTypes[i1];
                            Class<?> paramClass = getClassType(types[i1].getClassName());
                            if (paramClass != classParamType) {
                                break;
                            }
                        }

                        if (i1 == parameterTypes.length) {
                            for (int j = 0; j < parameters.size(); j++) {
                                Class<?> classParamType = parameterTypes[j];
                                DynamicArg dynamicArg = new DynamicArg();
                                Class<?> paramClass = getClassType(types[j].getClassName());
                                String parameterPackageName = "";
                                if (paramClass != null) {
                                    parameterPackageName = paramClass.getPackage() != null ? paramClass.getPackage().getName() : "";
                                }
                                dynamicArg.setType(classParamType);
                                dynamicArg.setPackageName(parameterPackageName);
                                dynamicArg.setName(parameters.get(j).name);
                                dynamicArgList.add(dynamicArg);
//                }
                            }

                            returnClassType = classMethod.getReturnType();
                            DynamicArg returnArg = new DynamicArg();
                            returnArg.setType(returnClassType);
                            returnArg.setName("return");


                        }
                    }
                }
            }

            String signature = method.signature;
            if (signature != null) {
                if (signature.contains("<") && signature.contains(">")) {
                    String substring = signature.substring(signature.indexOf("<"), signature.indexOf(">") + 1);
                    int a = 1;
                }
            }


            dynamicMethod.setName(methodName);
            dynamicMethod.setArgList(dynamicArgList);
            dynamicMethodList.add(dynamicMethod);
        }


        return dynamicMethodList;
    }


    private static List<DynamicArg> getDynamicArgs(Method classMethod, Class<?> aclass) throws
            IOException, ClassNotFoundException {

        List<DynamicArg> dynamicArgList = new ArrayList<>();
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(aclass.getPackage().getName() + "." + aclass.getSimpleName());
        classReader.accept(classNode, 0);
        // 遍历方法节点
        HashMap<String, MethodNode> methodNodeHashMap = new HashMap<>();
        List<MethodNode> methods = classNode.methods;
        for (MethodNode methodNode : methods) {
            methodNodeHashMap.put(methodNode.name, methodNode);
        }
        String methodName = classMethod.getName();
        MethodNode methodNode = methodNodeHashMap.get(methodName);

        Type[] types = Type.getArgumentTypes(classMethod);
        List<ParameterNode> parameters = methodNode.parameters;
        if (ObjectUtils.isNotNull(parameters) && !parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                DynamicArg dynamicArg = new DynamicArg();
                Class<?> paramClass = Class.forName(types[i].getClassName());
                String parameterPackageName = paramClass.getPackage() != null ? paramClass.getPackage().getName() : "";
                dynamicArg.setType(paramClass);
                dynamicArg.setName(parameters.get(i).name);
                dynamicArg.setPackageName(parameterPackageName);
                dynamicArgList.add(dynamicArg);
            }
        }

        return dynamicArgList;
    }


    public static Class<?> getClassType(String className) {
        if (hashMap.containsKey(className)) {
            return hashMap.get(className);
        } else {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}
