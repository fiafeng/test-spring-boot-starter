package com.fiafeng.dynamicClass.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.dynamicClass.pojo.*;
import com.fiafeng.dynamicClass.utils.DynamicUtils;
import com.fiafeng.dynamicClass.utils.ModifiersUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping({"/dynamicClass/source", "/dynamicClass"})
public class DynamicSourceController {

    HashSet<String> stringHashSet;

    public DynamicSourceController() {
        this.stringHashSet = new HashSet<>(Arrays.asList("void", "int"));
    }

    @PostMapping(value = {"/get", "/get1"})
    public AjaxResult getSource(String className, @RequestBody JSONObject jsonObject) throws ClassNotFoundException {

        String jsonObjectString = jsonObject.getString("className");
        if (jsonObjectString != null) {
            className = jsonObjectString;
        }

        if (StringUtils.strIsEmpty(className)) {
            className = "com.fiafeng.dynamicClass.controller.DynamicSourceController";
        }
        Class<?> aClass = Class.forName(className);
        DynamicClass dynamicClass = DynamicUtils.getDynamicClass(aClass);
        List<String> stringList = new ArrayList<>();

        System.out.println("package " + dynamicClass.getPackageName() + ";\n");
        for (String string : dynamicClass.getImportList()) {
            if (!stringHashSet.contains(string)
                    && !string.startsWith("java.lang")
                    && !string.equals(className)
            ) {
                stringList.add("import " + string + ";");
            }
        }

        for (String string : stringList) {
            System.out.println(string);
        }
        System.out.println();
        List<DynamicAnnotation> annotationList = dynamicClass.getAnnotationList();
        List<String> annotationStringList = getAnnotationStringList(annotationList);
        ;
        // 输出注解信息
        for (String string : annotationStringList) {
            System.out.println(string);
        }


        int modifiers = dynamicClass.getModifiers();
        String modifiersInfo = ModifiersUtils.getModifiersInfo(modifiers);
        Class<?> dynamicClassType = dynamicClass.getType();
        String classInfo = modifiersInfo;
        if (dynamicClassType.isEnum()) {
            classInfo += " enum ";
        } else if (dynamicClassType.isInterface()) {
            classInfo += " interface ";
        } else if (dynamicClassType.isAnnotation()) {
            classInfo += " @interface ";
        } else {
            classInfo += " class ";
        }

        classInfo += dynamicClassType.getSimpleName();
        System.out.println(classInfo + " {");
        List<DynamicField> dynamicFieldList = dynamicClass.getFieldList();
        if (!dynamicFieldList.isEmpty()) {
            System.out.println();

            for (DynamicField dynamicField : dynamicFieldList) {
                StringBuilder field = new StringBuilder("\t");
                for (String string : getAnnotationStringList(dynamicField.getAnnotationList())) {
                    field.append(string).append("\n");
                }

                Class<?> fieldType = dynamicField.getType();
                field.append(fieldType.getSimpleName());
                if (dynamicField.isParamType()) {
                    field.append("<");
                    List<Class<?>> componentTypeList = dynamicField.getComponentTypeList();
                    for (Class<?> componentType : componentTypeList) {
                        field.append(componentType.getSimpleName()).append(",");
                    }

                    field = new StringBuilder(field.substring(0, field.lastIndexOf(",")));
                    field.append(">");
                }
                field.append(" ").append(dynamicField.getName()).append(";");
                System.out.println(field);

            }
            System.out.println();
        }


        for (DynamicMethod dynamicMethod : dynamicClass.getMethodList()) {
            List<String> methodAnnotationStringList = getAnnotationStringList(dynamicMethod.getAnnotationList());

            for (String string : methodAnnotationStringList) {
                System.out.println("\t" + string);
            }

            int dynamicMethodModifiers = dynamicMethod.getModifiers();
            String dynamicMethodModifierInfo = ModifiersUtils.getModifiersInfo(dynamicMethodModifiers);
            DynamicArg dynamicMethodReturnArg = dynamicMethod.getReturnArg();
            StringBuilder methodInfo = new StringBuilder(dynamicMethodModifierInfo);
            methodInfo = getReturnArgTypeInfo(dynamicMethodReturnArg, methodInfo);


            methodInfo.append(dynamicMethod.getName()).append("(");
            for (DynamicArg dynamicArg : dynamicMethod.getArgList()) {
                StringBuilder argInfo = new StringBuilder();
                List<DynamicAnnotation> argAnnotationList = dynamicArg.getAnnotationList();
                for (String string : getAnnotationStringList(argAnnotationList)) {
                    argInfo.append(string).append(" ");
                }


                argInfo = getReturnArgTypeInfo(dynamicArg, argInfo);
                argInfo.append(dynamicArg.getName()).append(" ,");

                methodInfo.append(argInfo);
            }
            if (methodInfo.toString().endsWith(",")) {
                methodInfo = new StringBuilder(new StringBuilder(methodInfo.substring(0, methodInfo.lastIndexOf(","))));
            }

            methodInfo.append("){\n\t}");


            System.out.println("\t" + methodInfo);

        }


        System.out.println("}");

        return AjaxResult.success();
    }

    private StringBuilder getReturnArgTypeInfo(DynamicArg dynamicMethodReturnArg, StringBuilder methodInfo) {
        if (dynamicMethodReturnArg.isParamType()) {
            methodInfo.append("<");
            List<Class<?>> componentTypeList = dynamicMethodReturnArg.getComponentTypeList();
            for (Class<?> componentType : componentTypeList) {
                methodInfo.append(componentType.getSimpleName()).append(",");
            }
            methodInfo = new StringBuilder(new StringBuilder(methodInfo.substring(0, methodInfo.lastIndexOf(","))));
            methodInfo.append("> ");
        } else {
            methodInfo.append(dynamicMethodReturnArg.getType().getSimpleName()).append(" ");
        }
        return methodInfo;
    }

    private static List<String> getAnnotationStringList(List<DynamicAnnotation> annotationList) {
        List<String> annotationStringList = new ArrayList<>();
        for (DynamicAnnotation dynamicAnnotation : annotationList) {
            StringBuilder string = new StringBuilder("@" + dynamicAnnotation.getType().getSimpleName());
            if (dynamicAnnotation.getValueList() != null && !dynamicAnnotation.getValueList().isEmpty()) {
                string.append("(");
                for (DynamicAnnotationMethod dynamicAnnotationMethod : dynamicAnnotation.getValueList()) {
                    Object value = dynamicAnnotationMethod.getValue();
                    Class<?> returnType = dynamicAnnotationMethod.getReturnType();
                    if (value == null
                            || (returnType == String[].class && ((String[]) value).length == 0)
                            || (returnType == String.class && ((String) value).isEmpty())
                    ) {
                        continue;
                    }

                    string.append(dynamicAnnotationMethod.getName()).append(" = ");
                    if (returnType == String[].class) {
                        String[] strings = (String[]) value;
                        string.append("{");
                        for (String s : strings) {

                            string.append("\"").append(s).append("\"").append(" ,");
                        }
                        String substring = string.substring(0, string.lastIndexOf(","));
                        string = new StringBuilder(substring).append("},");
                    } else if (returnType == RequestMethod[].class) {
                        RequestMethod[] requestMethods = (RequestMethod[]) value;
                        if (requestMethods.length == 0) {
                            continue;
                        }
                        string.append("{");
                        for (RequestMethod requestMethod : requestMethods) {
                            string.append("RequestMethod.").append(requestMethod.name()).append(" ,");
                        }
                        String substring = string.substring(0, string.lastIndexOf(","));
                        string = new StringBuilder(substring);

                        string.append("},");
                    } else {

                        string.append(value).append(" ,");
                    }

                }
                String substring = string.toString();
                if (string.indexOf(",") != -1) {
                    substring = string.substring(0, string.lastIndexOf(","));
                }
                string = new StringBuilder(substring + ")");
                if (string.lastIndexOf("()") != -1) {

                    string = new StringBuilder(string.substring(0, string.lastIndexOf("()")));
                }
            }
            annotationStringList.add(string.toString());
//            System.out.println(string);
        }
        return annotationStringList;
    }

}
