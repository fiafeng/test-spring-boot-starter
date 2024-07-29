package com.fiafeng.mybatis.utils;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

import java.util.ArrayList;
import java.util.List;

public class JavassistAnnotationUtils {

    public static ConstPool constPool = JavassistUtils.constPool;

    public static Annotation getJavassistAnnotation(Class<? extends java.lang.annotation.Annotation> annotationClass,
                                                    String name, Object value) {

        Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
        MemberValue memberValue = getMemberValue(value);
        annotation.addMemberValue(name, memberValue);
        return annotation;
    }

    public static MemberValue   getMemberValue(Object value) {
        MemberValue memberValue = null;
        Class<?> valueClass = value.getClass();
        if (valueClass == String.class) {
            memberValue = getMemberValue((String) value);
        } else if (valueClass == Long.class) {
            memberValue = getMemberValue((Long) value);
        } else if (valueClass == Integer.class ) {
            memberValue = getMemberValue((Integer) value);
        } else if (valueClass == Boolean.class) {
            memberValue = getMemberValue((Boolean) value);
        } else if (valueClass.isEnum()) {
            memberValue = getMemberValue((Enum) value);
        }  else if (valueClass.isArray()) {
            memberValue = getMemberValue((Object[]) value);
        }  else if (valueClass == Character.class) {
            memberValue = getMemberValue((Character) value);
        }  else if (valueClass == Byte.class) {
            memberValue = getMemberValue((Byte) value);
        }  else if (valueClass == Float.class) {
            memberValue = getMemberValue((Float) value);
        }  else if (valueClass == Double.class) {
            memberValue = getMemberValue((Double) value);
        }  else if (valueClass == Annotation.class) {
            memberValue = getMemberValue((Annotation) value);
        }  else if (valueClass == Short.class) {
            memberValue = getMemberValue((Short) value);
        }  else if (valueClass == Class.class) {
            memberValue = getMemberValue((Class) value);
        }

        else {
            throw new RuntimeException("没有找到匹配的类型");
        }

        return memberValue;
    }

    public static EnumMemberValue getMemberValue(Enum<?> enumValue) {
        EnumMemberValue memberValue = new EnumMemberValue(constPool);
        memberValue.setType(enumValue.getClass().getName());
        memberValue.setValue(enumValue.name());
        return memberValue;
    }

    public static StringMemberValue getMemberValue(String value) {
        StringMemberValue memberValue = new StringMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }


    public static LongMemberValue getMemberValue(Long value) {
        LongMemberValue memberValue = new LongMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static IntegerMemberValue getMemberValue(Integer value) {
        IntegerMemberValue memberValue = new IntegerMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static BooleanMemberValue getMemberValue(Boolean value) {
        BooleanMemberValue memberValue = new BooleanMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static ByteMemberValue getMemberValue(Byte value) {
        ByteMemberValue memberValue = new ByteMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static CharMemberValue getMemberValue(Character value) {
        CharMemberValue memberValue = new CharMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static FloatMemberValue getMemberValue(Float value) {
        FloatMemberValue memberValue = new FloatMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static DoubleMemberValue getMemberValue(Double value) {
        DoubleMemberValue memberValue = new DoubleMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static ShortMemberValue getMemberValue(Short value) {
        ShortMemberValue memberValue = new ShortMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static AnnotationMemberValue getMemberValue(Annotation value) {
        AnnotationMemberValue memberValue = new AnnotationMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

    public static ClassMemberValue getMemberValue(Class<?> value) {
        ClassMemberValue memberValue = new ClassMemberValue(constPool);
        memberValue.setValue(value.getName());
        return memberValue;
    }

    public static <T> ArrayMemberValue getMemberValue(T[] values) {
        List<MemberValue> memberValueList = new ArrayList<>();
        for (T value : values) {
            MemberValue memberValue = getMemberValue(value);
            memberValueList.add(memberValue);
        }
        MemberValue[] value = memberValueList.toArray(new MemberValue[0]);

        ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
        memberValue.setValue(value);
        return memberValue;
    }

}
