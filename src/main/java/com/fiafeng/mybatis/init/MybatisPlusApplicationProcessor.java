package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.constant.ModelConstant;
import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.mapper.Interface.IMapper;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import com.fiafeng.mybatis.properties.FiafengMybatisProperties;
import com.fiafeng.mybatis.utils.JavassistAnnotationUtils;
import com.fiafeng.mybatis.utils.JavassistMethodUtils;
import com.fiafeng.mybatis.utils.JavassistUtils;
import com.fiafeng.mybatis.utils.MybatisPlusUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@ApplicationProcessorAnnotation(-1)
public class MybatisPlusApplicationProcessor extends ApplicationProcessor {

    FiafengMybatisProperties mybatisProperties;

    @Override
    public void postProcessBeanFactory() {
        Map<String, IMysqlTableProperties> beansOfType = FiafengSpringUtils.getBeansOfType(IMysqlTableProperties.class);
        Map<String, FiafengMybatisProperties> mybatisPropertiesMap = FiafengSpringUtils.getBeansOfType(FiafengMybatisProperties.class);
        for (FiafengMybatisProperties value : mybatisPropertiesMap.values()) {
            mybatisProperties = value;
            break;
        }


        for (Map.Entry<Class<? extends IMapper>, Class<? extends IBasePojo>> classClassEntry : MybatisPlusUtils.getHashMap().entrySet()) {
            Class<? extends IMapper> mapperClass = classClassEntry.getKey();
            Class<? extends IBasePojo> pojoClass = classClassEntry.getValue();

            String pojoSubstringName = pojoClass.getSimpleName().substring(5);
            Map<String, ? extends IMapper> beansOfType1;
            try {
                beansOfType1 = FiafengSpringUtils.getBeansOfType(mapperClass);
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
                                Class<?> generateClass;
                                try {
                                    generateClass = createdMybatisPojoClass(beanFactoryBeansOfType.get(pojoName).getClass(), tableProperties, pojoClass);
                                } catch (NotFoundException ex) {
                                    throw new RuntimeException(ex);
                                } catch (CannotCompileException ex) {
                                    throw new RuntimeException(ex);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                ObjectClassUtils.registerBean(generateClass, null);

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
        // 获取源类的CtClass对象
        CtClass originalClass = null;
        try {
            originalClass = JavassistUtils.classPool.get(defaultClass.getName());
        } catch (NotFoundException ignore) {
        }
        boolean deletedField = false;


        // 创建新类的CtClass对象，这里使用复制构造函数
        assert originalClass != null;
        CtClass newClass = JavassistUtils.classPool.makeClass(originalClass.getPackageName() + ".Mybatis" + originalClass.getSimpleName());
        JavassistUtils.constPool = newClass.getClassFile().getConstPool();

        for (CtField ctField : originalClass.getDeclaredFields()) {
            CtField newCtField = new CtField(ctField.getType(), ctField.getName(), newClass);
            if (!deletedField && mybatisProperties.getTombstone() && mybatisProperties.getTombstoneFieldName().equals(newCtField.getName())) {
                // 逻辑删除字段
                setTableLogicAnnotation(newCtField);
                deletedField = true;
            } else if (properties.getIdName().equals(newCtField.getName())) {
                // 主键字段
//                AnnotationsAttribute ctFieldAnnotationsAttribute = new AnnotationsAttribute(JavassistUtils.constPool, AnnotationsAttribute.visibleTag);
                AnnotationsAttribute ctFieldAnnotationsAttribute = JavassistUtils.getAnnotationsAttribute(newClass);
//                AnnotationsAttribute ctFieldAnnotationsAttribute = JavassistUtils.getAnnotationsAttribute(TableId.class, IdType.AUTO);

                newCtField.getFieldInfo().addAttribute(ctFieldAnnotationsAttribute);
            }
            // 添加属性
            newClass.addField(newCtField);

            // 给属性添加对应的set和get方法
            JavassistMethodUtils.addSetterGetter(newClass, newCtField);
        }

        if (!deletedField && mybatisProperties.getTombstone()) {
            // 原本的类没有添加了逻辑删除属性，

            CtField ctField = new CtField(JavassistUtils.classPool.get(Boolean.class.getName()), mybatisProperties.getTombstoneFieldName(), newClass);
            newClass.addField(ctField);
//            setTableLogicAnnotation(ctField);
            JavassistMethodUtils.addSetterGetter(newClass, ctField);
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

        newClass.addMethod(JavassistMethodUtils.ToStringMethod(newClass));
        newClass.addMethod(JavassistMethodUtils.EqualsMethod(newClass));
        newClass.addMethod(JavassistMethodUtils.HashCodeMethod(newClass));
        newClass.addMethod(JavassistMethodUtils.CanEqualMethod(newClass));

        AnnotationsAttribute annotationsAttribute = JavassistUtils.getAnnotationsAttribute(newClass);


        // 添加 @TableName 注解和值
        Annotation tableNameAnnotation = JavassistUtils.getAnnotation(newClass, TableName.class, properties.getTableName());

        // 添加 @BeanDefinitionOrderAnnotation 注解和值
        Annotation beanDefinitionOrderAnnotation = JavassistUtils.getAnnotation(newClass, BeanDefinitionOrderAnnotation.class, ModelConstant.fifthOrdered);

        // 添加 @PojoAnnotation 注解
        Annotation pojoAnnotationAnnotation = JavassistUtils.getAnnotation(newClass, PojoAnnotation.class);


        annotationsAttribute.addAnnotation(tableNameAnnotation);
        annotationsAttribute.addAnnotation(pojoAnnotationAnnotation);
        annotationsAttribute.addAnnotation(beanDefinitionOrderAnnotation);

        newClass.getClassFile().addAttribute(annotationsAttribute);
        newClass.writeFile("C:\\Users\\issuser\\Desktop\\");
//        newClass.writeFile();
        return newClass.toClass(this.getClass().getClassLoader(), Class.class.getProtectionDomain());
    }

    /**
     * 设置逻辑删除的注解
     *
     * @param ctField 属性信息
     */
    private void setTableLogicAnnotation(CtField ctField) {

        AnnotationsAttribute ctFieldAnnotationsAttribute = JavassistUtils.getAnnotationsAttribute();
        Annotation tableIdAnnotation = JavassistUtils.getAnnotation(TableLogic.class, "0");
        StringMemberValue stringMemberValue = JavassistAnnotationUtils.getMemberValue("1");
        tableIdAnnotation.addMemberValue("delval", stringMemberValue);
        ctFieldAnnotationsAttribute.addAnnotation(tableIdAnnotation);
        ctField.getFieldInfo().addAttribute(ctFieldAnnotationsAttribute);
    }
}
