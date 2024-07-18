package com.fiafeng.dynamicClass.utils;

import com.fiafeng.dynamicClass.pojo.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicUtils {

    static List<String> annotationNameIgnoreList = Arrays.asList("equals", "hashCode", "toString", "annotationType");
    static List<String> annotationMethodNameIgnoreList = Arrays.asList("equals", "hashCode", "toString", "annotationType");

    static List<String> classMethodNameIgnoreList = Arrays.asList("equals", "hashCode", "toString", "annotationType", "wait", "getClass", "notify", "notifyAll");


    public static DynamicClass getDynamicClass(Class<?> aclass) {
        DynamicClass dynamicClass = new DynamicClass();
        String simpleName = aclass.getSimpleName();
        Class<?> parentclass = aclass.getSuperclass();

        List<DynamicMethod> dynamicMethodList = getDynamicMethodList(aclass);
        List<DynamicField> dynamicFieldList = getDynamicFieldList(aclass);
        List<DynamicAnnotation> dynamicAnnotationList = getDynamicAnnotationList(aclass.getAnnotations());

        dynamicClass.setName(simpleName);
        dynamicClass.setType(aclass);
        dynamicClass.setParentClass(parentclass);

        dynamicClass.setFieldList(dynamicFieldList);
        dynamicClass.setMethodList(dynamicMethodList);
        dynamicClass.setAnnotationList(dynamicAnnotationList);
        return dynamicClass;
    }

    public static List<DynamicField> getDynamicFieldList(Class<?> aClass) {
        List<DynamicField> dynamicFieldList = new ArrayList<>();
        Field[] classFields = aClass.getFields();
        for (Field aClassField : classFields) {
            String fieldName = aClassField.getName();
            Class<?> fieldType = aClassField.getType();
            List<DynamicAnnotation> annotatedList = getDynamicAnnotationList(aClassField.getAnnotations());
            Type genericType = aClassField.getGenericType();
            List<Class<?>> classParameterType = getClassParameterType(genericType);

            DynamicField dynamicField = new DynamicField();
            dynamicField.setAnnotatedList(annotatedList);
            dynamicField.setName(fieldName);
            dynamicField.setModifiers(aClassField.getModifiers());
            dynamicField.setType(fieldType);
            dynamicField.setComponentTypeList(classParameterType);

            dynamicFieldList.add(dynamicField);
        }

        return dynamicFieldList;
    }

    public static List<DynamicMethod> getDynamicMethodList(Class<?> aclass) {

        List<DynamicMethod> dynamicMethodList = new ArrayList<>();

        Method[] aClassMethods = aclass.getMethods();
        for (Method classMethod : aClassMethods) {
            if (classMethodNameIgnoreList.contains(classMethod.getName())) {
                continue;
            }
            // 获取返回参数
            DynamicArg dynamicReturnArg = getMethodReturnArg(classMethod);

            // 获取方法参数信息
            List<DynamicArg> dynamicArgList = getMethodParamArgList(classMethod);

            // 获取方法上面的注解信息
            List<DynamicAnnotation> dynamicAnnotationList = getDynamicAnnotationList(classMethod.getAnnotations());

            String methodName = classMethod.getName();

            int modifiers = classMethod.getModifiers();

            DynamicMethod dynamicMethod = new DynamicMethod();

            Class<?>[] exceptionTypes = classMethod.getExceptionTypes();

            dynamicMethod.setName(methodName);
            dynamicMethod.setModifiers(modifiers);
            dynamicMethod.setArgList(dynamicArgList);
            dynamicMethod.setAnnotationList(dynamicAnnotationList);
            dynamicMethod.setReturnArg(dynamicReturnArg);

            dynamicMethodList.add(dynamicMethod);
        }
        return dynamicMethodList;
    }


    public static List<DynamicException> getExceptionList(Class<?>[] exceptionTypes){
        List<DynamicException> dynamicExceptionList = new ArrayList<>();
        for (Class<?> exceptionType : exceptionTypes) {
            DynamicException dynamicException = new DynamicException();

            dynamicException.setName(exceptionType.getSimpleName());
            dynamicException.setType(exceptionType);

            dynamicExceptionList.add(dynamicException);
        }

        return dynamicExceptionList;
    }

    public static List<DynamicAnnotation> getDynamicAnnotationList(Annotation[] annotations) {
        List<DynamicAnnotation> dynamicAnnotationList = new ArrayList<>();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            List<DynamicAnnotationMethod> dynamicAnnotationMethodList = getAnnotationMethodList(annotation);
            String annotationName = annotationClass.getSimpleName();

            DynamicAnnotation dynamicAnnotation = new DynamicAnnotation();

            dynamicAnnotation.setName(annotationName);
            dynamicAnnotation.setValueList(dynamicAnnotationMethodList);
            dynamicAnnotation.setType(annotationClass);

            dynamicAnnotationList.add(dynamicAnnotation);
        }

        return dynamicAnnotationList;
    }


    public static List<DynamicAnnotationMethod> getAnnotationMethodList(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        List<DynamicAnnotationMethod> dynamicAnnotationMethodList = new ArrayList<>();
        Method[] annotationMethods = annotationClass.getMethods();
        for (Method annotationMethod : annotationMethods) {
            String annotationMethodName = annotationMethod.getName();
            if (annotationNameIgnoreList.contains(annotationMethodName)) {
                continue;
            }
            Object defaultValue = null;
            try {
                defaultValue = annotationMethod.invoke(annotation);
            } catch (Exception ignore) {
                continue;
            }

            Class<?> returnType = defaultValue.getClass();
            DynamicAnnotationMethod dynamicAnnotationMethod = new DynamicAnnotationMethod();
            dynamicAnnotationMethod.setName(annotationMethodName);
            dynamicAnnotationMethod.setValue(defaultValue);
            dynamicAnnotationMethod.setReturnType(returnType);
            dynamicAnnotationMethodList.add(dynamicAnnotationMethod);
        }
        return dynamicAnnotationMethodList;
    }

    public static List<DynamicArg> getMethodParamArgList(Method classMethod) {
        List<DynamicArg> dynamicArgList = new ArrayList<>();
        for (Parameter parameter : classMethod.getParameters()) {

            Class<?> parameterType = parameter.getType();
            String parameterName = parameter.getName();
            String paramPackageName = DynamicUtils.getImport(parameterType);
            List<Class<?>> classParameterTypeList = getClassParameterType(parameterType);

            DynamicArg dynamicArg = new DynamicArg();
            if (!classParameterTypeList.isEmpty()) {
                dynamicArg.setParamType(true);
                dynamicArg.setComponentTypeList(classParameterTypeList);
            }

            dynamicArg.setName(parameterName);
            dynamicArg.setType(parameterType);
            dynamicArgList.add(dynamicArg);
        }
        return dynamicArgList;
    }


    public static DynamicArg getMethodReturnArg(Method classMethod) {
        List<Class<?>> componentTypeList = new ArrayList<>();
        // 获取返回参数
        Type genericReturnType = classMethod.getGenericReturnType();
        Class<?> returnType = classMethod.getReturnType();
        // 但他时Class.class的时候，Class<?> actualTypeArgument = (Class<?>) type强转失败
        if (returnType != Class.class) {
            componentTypeList = getClassParameterType(genericReturnType);
        }
        DynamicArg dynamicReturnArg = DynamicArg.getReturn();
        dynamicReturnArg.setType(returnType);
        if (!componentTypeList.isEmpty()) {
            dynamicReturnArg.setParamType(true);
            dynamicReturnArg.setComponentTypeList(componentTypeList);
        }
        return dynamicReturnArg;
    }

    private static List<Class<?>> getClassParameterType(Type genericType) {
        List<Class<?>> componentTypeList = new ArrayList<>();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length != 0) {
                componentTypeList = new ArrayList<>();
                for (Type type : typeArguments) {
                    String typeName = type.getTypeName();
                    if ("java.lang.Class<?>".equals(typeName)) {
                        componentTypeList.add(Class.class);
                    } else if (type instanceof Class) {
                        //强转
                        Class<?> actualTypeArgument = (Class<?>) type;
                        //获取实际参数的类名
                        componentTypeList.add(actualTypeArgument);
                    }else {
                        System.out.println(type);
                    }
                }
            }
        }
        return componentTypeList;
    }


    public static String getImport(Class<?> type) {
        return type.getCanonicalName();
    }


    public static String getPackageName(Class<?> aclass) {
        return aclass.getPackage() == null ? "" : aclass.getPackage().getName();

    }
}
