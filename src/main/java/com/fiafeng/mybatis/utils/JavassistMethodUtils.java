package com.fiafeng.mybatis.utils;

import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import javassist.*;

import java.util.Objects;

public class JavassistMethodUtils {

    public static void addSetterGetter(CtClass newClass, CtField newCtField, Class<? extends IBasePojo> defaultInterface)
            throws NotFoundException, CannotCompileException {
        addSetter(newCtField, newClass, defaultInterface);
        addGetter(newClass, newCtField);
    }

    public static void addSetterGetter(CtClass newClass, CtField newCtField)
            throws NotFoundException, CannotCompileException {
        addSetter(newCtField, newClass, null);
        addGetter(newClass, newCtField);
    }


    public static void addGetter(CtClass newClass, CtField newCtField) throws NotFoundException, CannotCompileException {
        String ctFieldTypeName = newCtField.getType().getName();
        String fieldName = newCtField.getName();
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String get = "public " + ctFieldTypeName + " get" + methodName + "() {\n" +
                "                    return " + fieldName + ";\n" +
                "                }";
        CtMethod getter = CtMethod.make(get, newClass);
        newClass.addMethod(getter);
    }

    public static void addSetter(CtField newCtField, CtClass newClass, Class<? extends IBasePojo> defaultInterface) throws NotFoundException, CannotCompileException {

        String ctFieldTypeName = newCtField.getType().getName();
        String fieldName = newCtField.getName();
        String returnType = "void";
        String returnValue = "";
        if (defaultInterface != null) {
            returnType = defaultInterface.getCanonicalName();
            returnValue = "        return this;\n";
        }

        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String set = "    public " + returnType + " set" + methodName + "(" + ctFieldTypeName + " " + fieldName + " ){\n"
                + "        this." + fieldName + " = " + fieldName + ";\n"
                + returnValue
                + "    }";
        CtMethod setter = CtMethod.make(set, newClass);
        newClass.addMethod(setter);
    }

    public static CtMethod EqualsMethod(CtClass cc) throws CannotCompileException, NotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("public boolean equals(Object o) {\n");
        sb.append("    if (this == o) return true;\n");
        sb.append("    if (o == null || getClass() != o.getClass()) return false;\n");
        sb.append("    ").append(cc.getName()).append(" other = (").append(cc.getName()).append(") o;\n");

        // 遍历所有字段生成 equals 比较
        for (CtField field : cc.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // 忽略静态和瞬态字段
            }
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            sb.append("    if (!")
                    .append(Objects.class.getName()).append(".equals(")
//                    .append("this.")
                    .append(fieldName).
                    append(", other.").append(fieldName).append(")) return false;\n");
        }
        sb.append("    return true;\n");
        sb.append("};\n");
        String string = sb.toString();
        return CtNewMethod.make(string, cc);
    }


    public static CtMethod ToStringMethod(CtClass cc) throws CannotCompileException, NotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("public String toString() {\n");
        sb.append("    return \"").append(cc.getSimpleName()).append("{\"\n");

        // 遍历所有字段生成 toString 表示
        boolean isFirst = true;
        for (CtField field : cc.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // 忽略静态和瞬态字段
            }
            if (!isFirst) {
                sb.append("\t+ \", \" +\n");
            }
            String fieldName = field.getName();
//            String fieldType = field.getType().getName();
            sb.append("\t\"").append(fieldName).append("=\" + ").append(fieldName).append(".toString(").append(")\n");
            isFirst = false;
        }

        sb.append("\t\t+ \"}\";\n");
        sb.append("}\n");
        String string = sb.toString();
        return CtNewMethod.make(string, cc);
    }

    public static CtMethod HashCodeMethod(CtClass cc) throws CannotCompileException {
        StringBuilder sb = new StringBuilder();
        sb.append("public int hashCode() {\n");
        sb.append("    int result = 1;\n");


        // 遍历所有字段生成hashCode计算
        for (CtField field : cc.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // 忽略静态和瞬态字段
            }

            sb.append("    result += ");
            String fieldType = Objects.class.getName();
            sb
//                    .append(fieldType)
//                    .append(".hash(")
                    .append("result  * 31 +  ");
            String fieldName = field.getName();
            sb.append(fieldName).append(".hashCode();\n");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(";\n");

        sb.append("    return result;\n");
        sb.append("};\n");
        String string = sb.toString();
        return CtNewMethod.make(string, cc);

    }

    public static CtMethod CanEqualMethod(CtClass cc) throws CannotCompileException {
        String sb = "protected boolean canEqual(Object obj) {\n" +
                "    return obj instanceof " + cc.getName() + ";\n" +
                "};\n";

        return CtNewMethod.make(sb, cc);
    }
}
