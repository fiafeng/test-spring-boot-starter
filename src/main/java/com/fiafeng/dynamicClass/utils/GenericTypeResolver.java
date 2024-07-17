package com.fiafeng.dynamicClass.utils;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.signature.*;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static aj.org.objectweb.asm.Opcodes.*;

public class GenericTypeResolver {

    private static final String TYPE_VARIABLE_SIGNATURE = "T";

    public static Map<String, Type> resolve(String className, String methodName) throws IOException {
        ClassReader classReader = new ClassReader(className);
        TypeCollectingVisitor typeCollectingVisitor = new TypeCollectingVisitor(methodName);
        classReader.accept(typeCollectingVisitor, ClassReader.SKIP_CODE);
        return typeCollectingVisitor.getTypeArguments();
    }

    @Getter
    private static class TypeCollectingVisitor extends ClassVisitor {
        private final String methodName;

        private Map<String, Type> typeArguments = new HashMap<>();

        public TypeCollectingVisitor(String methodName) {
            super(327680);
            this.methodName = methodName;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (methodName.equals(name) && signature != null) {
                // 解析方法签名
                SignatureReader signatureReader = new SignatureReader(signature);
                signatureReader.accept(new SignatureVisitor(327680) {
                    @Override
                    public void visitFormalTypeParameter(String name) {
                        super.visitFormalTypeParameter(name);
                        // 如果是泛型类型变量，记录其名称
                        if (name.startsWith(TYPE_VARIABLE_SIGNATURE)) {
                            typeArguments.put(name.substring(1), null);
                        }
                    }

                    @Override
                    public SignatureVisitor visitClassBound() {
                        // 访问类型变量的界限
                        return this;
                    }

                    @Override
                    public SignatureVisitor visitInterfaceBound() {
                        // 访问实现的接口
                        return this;
                    }

                    @Override
                    public SignatureVisitor visitSuperclass() {
                        // 访问继承的超类
                        return this;
                    }

                    @Override
                    public SignatureVisitor visitInterface() {
                        // 访问实现的接口
                        return this;
                    }

                    @Override
                    public void visitEnd() {
                        super.visitEnd();
                    }
                });
            }
            return methodVisitor;
        }

    }
}