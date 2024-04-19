package com.fiafeng.mybatis.init;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fiafeng.common.annotation.ApplicationProcessorAnnotation;
import com.fiafeng.common.annotation.BeanDefinitionOrderAnnotation;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.init.ApplicationProcessor;
import com.fiafeng.common.pojo.DefaultPermission;
import com.fiafeng.common.pojo.DefaultUser;
import com.fiafeng.common.pojo.Interface.IBasePermission;
import com.fiafeng.common.pojo.Interface.base.IBasePojo;
import com.fiafeng.common.properties.mysql.FiafengMysqlPermissionProperties;
import com.fiafeng.common.properties.mysql.FiafengMysqlUserProperties;
import com.fiafeng.common.properties.mysql.IMysqlTableProperties;
import com.fiafeng.common.utils.ObjectClassUtils;
import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

@Component
@ApplicationProcessorAnnotation(-1)
public class MybatisPlusApplicationProcessor extends ApplicationProcessor {

    @Override
    public void postProcessBeanFactory() {

        Map<String, IBasePojo> beansOfType = ObjectClassUtils.beanFactory.getBeansOfType(IBasePojo.class);

        Class<?> aClass = generateClass(DefaultUser.class, FiafengSpringUtils.getBean(FiafengMysqlUserProperties.class));


        FiafengSpringUtils.registerBean(aClass.getSimpleName(),aClass, null);

    }



    public static Class<?> generateClass(Class<? extends IBasePojo> defaultClass, IMysqlTableProperties properties) {

        String packageName = "com.fiafeng.mybatis.pojo.";
        String className = "MybatisDefaultPermission";

        try {        // 添加一个方法到类中
            ClassPool pool = ClassPool.getDefault();

            CtClass ctClass = pool.makeClass(packageName + className);
            CtClass ctInterface = pool.makeInterface(IBasePermission.class.getName());
            ctClass.addInterface(ctInterface);

            ClassFile classFile = ctClass.getClassFile();
            ConstPool constPool = classFile.getConstPool();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            // 创建要添加的注解
            Annotation beanDefinitionOrderAnnotation = new Annotation(BeanDefinitionOrderAnnotation.class.getCanonicalName(), constPool);
            Annotation pojoAnnotationAnnotation = new Annotation(PojoAnnotation.class.getCanonicalName(), constPool);
            // 设置注解中的属性和值
            EnumMemberValue value = new EnumMemberValue(constPool);
            value.setType(Integer.class.getName());
            value.setValue("10");
            annotationsAttribute.addAnnotation(beanDefinitionOrderAnnotation);
            annotationsAttribute.addAnnotation(pojoAnnotationAnnotation);

            ctClass.getClassFile().addAttribute(annotationsAttribute);

            //添加toString方法
            StringBuilder builder = new StringBuilder();
            builder.append("return \"" + className + " { \n");
            for (Field declaredField : defaultClass.getDeclaredFields()) {
                String fieldName = declaredField.getName();
                builder.append("                   \" " + fieldName + " = \" +" + fieldName + " + \n ");
                CtField ctField = new CtField(pool.get(declaredField.getType().getCanonicalName()), fieldName + "", ctClass);
                ctField.setModifiers(Modifier.PUBLIC);
                ctClass.addField(ctField);
                if (properties.getIdName().equals(fieldName)) {
                    AnnotationsAttribute fieldAnnotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    // 创建要添加的注解
                    Annotation jsonFileAnnotation = new Annotation(TableId.class.getCanonicalName(), constPool);
                    // 设置注解中的属性和值
                    EnumMemberValue enumMemberValue = new EnumMemberValue(constPool);
                    enumMemberValue.setValue("AUTO");
                    enumMemberValue.setType(IdType.class.getName());

                    jsonFileAnnotation.addMemberValue("type", enumMemberValue);

                    // 把这个注解放到一个AnnotationsAttribute对象里面
                    fieldAnnotationsAttribute.addAnnotation(jsonFileAnnotation);
                    // 把这个对象怼到要打上这个注解的字段/类上面
                    ctField.getFieldInfo().addAttribute(fieldAnnotationsAttribute);
                }

                fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                CtMethod setter = new CtMethod(pool.get(IBasePermission.class.getName()), "set" + fieldName, null, ctClass);
                setter.setBody("return this;");
                CtMethod getter = CtNewMethod.getter("get" + fieldName, ctField);
                ctClass.addMethod(setter);
                ctClass.addMethod(getter);
            }
            builder.append("                \"}\";");

            ctClass.addMethod(ToStringMethod(ctClass));
            ctClass.addMethod(EqualsMethod(ctClass));
            ctClass.addMethod(HashCodeMethod(ctClass));
            ctClass.addMethod(CanEqualMethod(ctClass));

//            ctClass.writeFile("E:\\project\\java\\test-spring-boot-starter\\target\\");
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
