package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.annotation.*;
import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.properties.FiafengMybatisProperties;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Component
@ApplicationProcessorAnnotation(-1)
public class MybatisPlusApplicationProcessor extends ApplicationProcessor {

    FiafengMybatisProperties mybatisProperties;

    @Override
    public void postProcessBeanFactory() {
        Map<String, IMysqlTableProperties> beansOfType = FiafengSpringUtils.getBeansOfType(IMysqlTableProperties.class);
        Map<String, FiafengMybatisProperties> mybatisPropertiesMap = FiafengSpringUtils.getBeansOfType(FiafengMybatisProperties.class);
        for (FiafengMybatisProperties value : mybatisPropertiesMap.values()) {
            mybatisProperties  = value;
            break;
        }


        for (Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>> classClassEntry : MybatisPlusUtils.getHashMap().entrySet()) {
            Class<? extends IMapper> mapperClass = classClassEntry.getKey();
            Class<? extends IBasePojo> pojoClass = classClassEntry.getValue();

            String pojoSubstringName = pojoClass.getSimpleName().substring(5);
            try {
                FiafengSpringUtils.getBeansOfType(mapperClass);
            } catch (BeansException e) {
                for (String beanName : beansOfType.keySet()) {
                    String name = beanName;
                    if (beanName.contains(".")) {
                        beanName = beanName.substring(beanName.lastIndexOf(".") + 1);
                    }
                    if (beanName.equals("FiafengMysql" + pojoSubstringName + "Properties")) {
                        IMysqlTableProperties tableProperties = beansOfType.get(name);
                        Map<String, ? extends IBasePojo> beanFactoryBeansOfType = FiafengSpringUtils.beanFactory.getBeansOfType(pojoClass);
                        for (String pojoName : beanFactoryBeansOfType.keySet()) {
                            if (pojoName.equals("default" + pojoSubstringName) || pojoName.equals("Default" + pojoSubstringName)) {
                                try {
                                    Class<?> aClass = createdMybatisPojoClass(beanFactoryBeansOfType.get(pojoName).getClass(), tableProperties, pojoClass);
                                    ObjectClassUtils.registerBean(aClass, null);

                                } catch (NotFoundException | CannotCompileException | IOException exception) {
                                    throw new RuntimeException(exception);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    private Class<?> createdMybatisPojoClass(Class<? extends IBasePojo> defaultClass, IMysqlTableProperties properties, Class<? extends IBasePojo> defaultInterface)
            throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();

        // 获取源类的CtClass对象
        CtClass originalClass = null;
        try {
            originalClass = pool.get(defaultClass.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        boolean deletedField = false;




        // 创建新类的CtClass对象，这里使用复制构造函数
        CtClass newClass = pool.makeClass(originalClass.getPackageName() + ".Mybatis" + originalClass.getSimpleName());
        for (CtField ctField : originalClass.getDeclaredFields()) {
            ClassFile classFile = newClass.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            CtField newCtField = new CtField(ctField.getType(), ctField.getName(), newClass);
            if (!deletedField && mybatisProperties.getTombstone() && mybatisProperties.getTombstoneFieldName().equals(newCtField.getName())){
                // 逻辑删除字段
                setTableLogicAnnotation(ctField);
                deletedField = true;

            } else if (properties.getIdName().equals(newCtField.getName())) {
                // 主键字段
                AnnotationsAttribute ctFieldAnnotationsAttribute = new AnnotationsAttribute(newCtField.getFieldInfo().getConstPool(), AnnotationsAttribute.visibleTag);
                Annotation tableIdAnnotation = getEnumAnnotation(TableId.class, IdType.class, IdType.AUTO.name(), constPool);
                ctFieldAnnotationsAttribute.addAnnotation(tableIdAnnotation);
                newCtField.getFieldInfo().addAttribute(ctFieldAnnotationsAttribute);
            }
            // 添加属性
            newClass.addField(newCtField);

            // 给属性添加对应的set和get方法
            addSetterGetter(defaultInterface, newCtField, newClass);
        }

        if (!deletedField && mybatisProperties.getTombstone()){
            // 原本的类没有添加了逻辑删除属性，

            CtField ctField = new CtField(newClass.getClassPool().get(Boolean.class.getName()), mybatisProperties.getTombstoneFieldName(), newClass);
            newClass.addField(ctField);
            setTableLogicAnnotation(ctField);

            addSetterGetter(defaultInterface, ctField, newClass);
        }

        // 创建对应的构造方法
        for (CtConstructor constructor : originalClass.getConstructors()) {
            CtConstructor ctNewConstructor = CtNewConstructor.make(constructor.getParameterTypes(), constructor.getExceptionTypes(), newClass);
            newClass.addConstructor(ctNewConstructor);
        }

        // 继承所有的接口
        for (CtClass anInterface : originalClass.getInterfaces()) {
            newClass.addInterface(anInterface);
        }

        ConstPool constPool = newClass.getClassFile().getConstPool();

        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        // 添加 @BeanDefinitionOrderAnnotation 注解和值
        Annotation beanDefinitionOrderAnnotation = getIntegerAnnotation(BeanDefinitionOrderAnnotation.class, ModelConstant.fifthOrdered, constPool);
        // 添加 @TableName 注解和值
        Annotation tableNameAnnotation = getStringAnnotation(TableName.class, properties.getTableName(), constPool);

        // 添加 @PojoAnnotation 注解
        Annotation pojoAnnotationAnnotation = new Annotation(PojoAnnotation.class.getCanonicalName(), constPool);


        annotationsAttribute.addAnnotation(tableNameAnnotation);
        annotationsAttribute.addAnnotation(beanDefinitionOrderAnnotation);
        annotationsAttribute.addAnnotation(pojoAnnotationAnnotation);

        newClass.getClassFile().addAttribute(annotationsAttribute);
//        newClass.writeFile("C:\\Users\\issuser\\Desktop\\");
        newClass.writeFile();
        return newClass.toClass();
    }

    private static void addSetterGetter(Class<? extends IBasePojo> defaultInterface, CtField newCtField, CtClass newClass) throws NotFoundException, CannotCompileException {


        String ctFieldTypeName = newCtField.getType().getName();
        String fieldName = newCtField.getName();
        String method = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String set = "    public " + defaultInterface.getName() + " set" + method + "(" + ctFieldTypeName + " " + fieldName + " ){\n" +
                "        this." + fieldName + " = " + fieldName + ";\n" +
                "        return this;\n" +
                "    }";
        CtMethod setter = CtMethod.make(set, newClass);
        newClass.addMethod(setter);
        String get = "public " + ctFieldTypeName + " get" + method + "() {\n" +
                "                    return " + fieldName + ";\n" +
                "                }";

        CtMethod getter = CtMethod.make(get, newClass);
        newClass.addMethod(getter);
    }

    /**
     * 设置逻辑删除的注解
     * @param ctField 属性信息
     */
    private void setTableLogicAnnotation(CtField ctField) {

        AnnotationsAttribute ctFieldAnnotationsAttribute = new AnnotationsAttribute(ctField.getFieldInfo().getConstPool(), AnnotationsAttribute.visibleTag);
        Annotation tableIdAnnotation = getStringAnnotation(TableLogic.class, "0", ctField.getFieldInfo().getConstPool());
        StringMemberValue stringMemberValue = new StringMemberValue(ctField.getFieldInfo().getConstPool());
        stringMemberValue.setValue("1");
        tableIdAnnotation.addMemberValue("delval", stringMemberValue);
        ctFieldAnnotationsAttribute.addAnnotation(tableIdAnnotation);
        ctField.getFieldInfo().addAttribute(ctFieldAnnotationsAttribute);
    }

    public static Annotation getEnumAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass,
                                               Class<?> typeClass, String value, ConstPool constPool) {
        Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
        EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
        enumMemberValue.setType(typeClass.getName());
        enumMemberValue.setValue(value);
        annotation.addMemberValue("value", enumMemberValue);
        return annotation;

    }



    public static Annotation getStringAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass, String value, ConstPool constPool) {
        Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
        StringMemberValue stringMemberValue = new StringMemberValue(constPool);
        stringMemberValue.setValue(value);
        annotation.addMemberValue("value", stringMemberValue);
        return annotation;
    }

    public static Annotation getLongAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass, Long value, ConstPool constPool) {
        Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
        LongMemberValue longMemberValue = new LongMemberValue(constPool);
        longMemberValue.setValue(value);
        annotation.addMemberValue("value", longMemberValue);
        return annotation;
    }

    public static Annotation getIntegerAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass, Integer value, ConstPool constPool) {
        Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
        IntegerMemberValue longMemberValue = new IntegerMemberValue(constPool);
        longMemberValue.setValue(value);
        annotation.addMemberValue("value", longMemberValue);
        return annotation;
    }


    public static Class<?> generateClass(Class<? extends IBasePojo> defaultClass, IMysqlTableProperties properties, Class<? extends IBasePojo> defaultInterface) {

        String packageName = "com.fiafeng.mybatis.pojo.";
        String className = "Mybatis" + defaultClass.getSimpleName();

        try {        // 添加一个方法到类中
            ClassPool pool = ClassPool.getDefault();

            CtClass ctClass = pool.makeClass(packageName + className);
            CtClass ctInterface = pool.makeInterface(IBasePermission.class.getName());
            ctClass.addInterface(ctInterface);

            ClassFile classFile = ctClass.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            // 添加 @BeanDefinitionOrderAnnotation 注解和值
            Annotation beanDefinitionOrderAnnotation = new Annotation(BeanDefinitionOrderAnnotation.class.getCanonicalName(), constPool);
            EnumMemberValue BeanDefinitionOrderEnumMemberValue = new EnumMemberValue(constPool);
            BeanDefinitionOrderEnumMemberValue.setType(Integer.class.getName());
            BeanDefinitionOrderEnumMemberValue.setValue("10");
            beanDefinitionOrderAnnotation.addMemberValue("value", BeanDefinitionOrderEnumMemberValue);

            // 添加 @TableName 注解和值
            Annotation tableNameAnnotation = new Annotation(TableName.class.getName(), constPool);
            EnumMemberValue tableNameEnumMemberValue = new EnumMemberValue(constPool);
            tableNameEnumMemberValue.setType(String.class.getName());
            tableNameEnumMemberValue.setValue(properties.getTableName());
            tableNameAnnotation.addMemberValue("value", tableNameEnumMemberValue);

            // 添加 @PojoAnnotation 注解
            Annotation pojoAnnotationAnnotation = new Annotation(PojoAnnotation.class.getCanonicalName(), constPool);

            CtClass pojoCtClass = pool.get(defaultClass.getName());
            for (CtField ctField : pojoCtClass.getDeclaredFields()) {
                FieldInfo fieldInfo = ctField.getFieldInfo();
                String fieldName = ctField.getName();
                String method = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                if (properties.getIdName().equals(fieldName)) {
                    AnnotationsAttribute ctFieldAnnotationsAttribute = new AnnotationsAttribute(fieldInfo.getConstPool(), AnnotationsAttribute.visibleTag);
                    Annotation tableIdAnnotation = new Annotation(TableId.class.getCanonicalName(), constPool);
                    EnumMemberValue tableIdEnumMemberValue = new EnumMemberValue(constPool);
                    tableIdEnumMemberValue.setType(IdType.class.getName());
                    tableIdEnumMemberValue.setValue(IdType.AUTO.name());
                    tableIdAnnotation.addMemberValue("value", tableIdEnumMemberValue);
                    ctFieldAnnotationsAttribute.addAnnotation(tableIdAnnotation);
                    fieldInfo.addAttribute(ctFieldAnnotationsAttribute);
                }
                // 添加属性
                classFile.addField(fieldInfo);

                String set = "    public " + defaultInterface.getName() + " set" + method + "(Long " + fieldName + " ){\n" +
                        "        this." + fieldName + " = " + fieldName + ";\n" +
                        "        return this;\n" +
                        "    }";
                CtMethod setter = CtMethod.make(set, ctClass);
                ctClass.addMethod(setter);
                CtClass ctFieldType = ctField.getType();
                String get = "public " + ctFieldType + " get" + method + "() {\n" +
                        "                    return " + fieldName + ";\n" +
                        "                }";
                CtMethod getter = CtMethod.make(get, ctClass);
                ctClass.addMethod(getter);

            }


            annotationsAttribute.addAnnotation(tableNameAnnotation);
            annotationsAttribute.addAnnotation(beanDefinitionOrderAnnotation);
            annotationsAttribute.addAnnotation(pojoAnnotationAnnotation);

            classFile.addAttribute(annotationsAttribute);


            ctClass.addMethod(ToStringMethod(ctClass));
            ctClass.addMethod(EqualsMethod(ctClass));
            ctClass.addMethod(HashCodeMethod(ctClass));
            ctClass.addMethod(CanEqualMethod(ctClass));

            ctClass.writeFile("C:\\Users\\issuser\\Desktop\\");
            return ctClass.toClass(ClassPool.getDefault().getClassLoader(), Class.class.getProtectionDomain());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static CtMethod EqualsMethod(CtClass cc) throws CannotCompileException, NotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("public boolean equals(Object o) {\n");
        sb.append("    if (this == o) return true;\n");
        sb.append("    if (o == null || getClass() != o.getClass()) return false;\n");
        sb.append("    " + cc.getName() + " other = (" + cc.getName() + ") o;\n");

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
        sb.append("    return \"").append(cc.getSimpleName()).append("{\" +\n");

        // 遍历所有字段生成 toString 表示
        boolean isFirst = true;
        for (CtField field : cc.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // 忽略静态和瞬态字段
            }
            if (!isFirst) {
                sb.append("        + \", \" +\n");
            }
            String fieldName = field.getName();
            String fieldType = field.getType().getName();
            sb.append("        \"").append(fieldName).append("=\" + ").append(fieldName).append(".toString(").append(")\n");
            isFirst = false;
        }

        sb.append("    ;\n");
        sb.append("}\n");
        String string = sb.toString();
        return CtNewMethod.make(string, cc);
    }

    public static CtMethod HashCodeMethod(CtClass cc) throws CannotCompileException, NotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append("public int hashCode() {\n");
        sb.append("    int result = 1;\n");


        // 遍历所有字段生成hashCode计算
        for (CtField field : cc.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue; // 忽略静态和瞬态字段
            }

            sb.append("    result = ");
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
        StringBuilder sb = new StringBuilder();
        sb.append("protected boolean canEqual(Object obj) {\n");
        sb.append("    return obj instanceof ").append(cc.getName()).append(";\n");
        sb.append("};\n");

        return CtNewMethod.make(sb.toString(), cc);
    }
}
