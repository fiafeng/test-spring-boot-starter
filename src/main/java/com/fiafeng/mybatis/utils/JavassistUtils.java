package com.fiafeng.mybatis.utils;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

public class JavassistUtils {

    public static ConstPool constPool;
    public static ClassPool classPool = ClassPool.getDefault();


    public static CtField getNewFiled(CtClass newClass, CtField ctField) {
        try {
            return new CtField(ctField.getType(), ctField.getName(), newClass);
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addAnnotation(CtClass ctClass, Annotation annotation) {
        ctClass.getClassFile().addAttribute(getAnnotationsAttribute(annotation));
    }

    public static void addAnnotation(CtMethod ctMethod, Annotation annotation) {
        ctMethod.getMethodInfo().addAttribute(getAnnotationsAttribute(annotation));
    }

    public static void addAnnotation(CtField ctField, Annotation annotation) {
        ctField.getFieldInfo().addAttribute(getAnnotationsAttribute(annotation));
    }

    public static AnnotationsAttribute getAnnotationsAttribute() {

        if (constPool != null){
            return new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }else {
            throw new RuntimeException();
        }
    }

    public static AnnotationsAttribute getAnnotationsAttribute(CtClass ctClass) {
        return new AnnotationsAttribute(ctClass.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
    }

    public static <T> AnnotationsAttribute getAnnotationsAttribute(Class<? extends java.lang.annotation.Annotation> annotationClass, String name, T t) {
        AnnotationsAttribute annotationsAttribute = getAnnotationsAttribute();
        Annotation annotation = getAnnotation(annotationClass, name, t);
        annotationsAttribute.addAnnotation(annotation);
        return annotationsAttribute;
    }

    public static <T> AnnotationsAttribute getAnnotationsAttribute(Class<? extends java.lang.annotation.Annotation> annotationClass, T t) {
        return getAnnotationsAttribute(annotationClass, "value", t);
    }

    public static AnnotationsAttribute getAnnotationsAttribute(Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return getAnnotationsAttribute(annotationClass, "value", null);
    }

    public static AnnotationsAttribute getAnnotationsAttribute(Annotation annotation) {
        AnnotationsAttribute annotationsAttribute = getAnnotationsAttribute();
        annotationsAttribute.addAnnotation(annotation);
        return annotationsAttribute;
    }

    public static <T> Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass, String name, T t) {

        Annotation annotation = new Annotation(annotationClass.getName(), constPool);
        if (t != null) {
            MemberValue memberValue = JavassistAnnotationUtils.getMemberValue(t);
            annotation.addMemberValue(name, memberValue);
        }
        return annotation;
    }


    public static <T> Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass, T t) {
        return getAnnotation(annotationClass, "value", t);
    }


    public static Annotation getAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return getAnnotation(annotationClass, "value", null);
    }


    public static <T> Annotation getAnnotation(CtClass ctClass, Class<? extends java.lang.annotation.Annotation> annotationClass, String name, T t) {

        Annotation annotation = new Annotation(annotationClass.getName(), ctClass.getClassFile().getConstPool());
        if (t != null) {
            MemberValue memberValue = JavassistAnnotationUtils.getMemberValue(t);
            annotation.addMemberValue(name, memberValue);
        }
        return annotation;
    }

    public static <T> Annotation getAnnotation(CtClass ctClass, Class<? extends java.lang.annotation.Annotation> annotationClass, T t) {
        return getAnnotation(ctClass, annotationClass, "value", t);
    }

    public static Annotation getAnnotation(CtClass ctClass, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        return getAnnotation(ctClass, annotationClass, "value", null);
    }

}
