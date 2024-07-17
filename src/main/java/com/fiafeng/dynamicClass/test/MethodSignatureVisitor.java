package com.fiafeng.dynamicClass.test;

import jdk.internal.org.objectweb.asm.*;
 
public class MethodSignatureVisitor extends ClassVisitor {
    public MethodSignatureVisitor(int api) {
        super(api);
    }
 
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv = new MethodSignatureVisitorAdapter(api, mv, name, descriptor);
        }
        return mv;
    }
 
    private static class MethodSignatureVisitorAdapter extends MethodVisitor {
        private final String name;
        private final String descriptor;
 
        public MethodSignatureVisitorAdapter(int api, MethodVisitor mv, String name, String descriptor) {
            super(api, mv);
            this.name = name;
            this.descriptor = descriptor;
        }
 
        @Override
        public void visitEnd() {
            // 打印方法签名
            System.out.println("Method: " + name + descriptor);
            // 解析并打印返回值和参数
            Type methodType = Type.getMethodType(descriptor);
            System.out.println("Return Type: " + methodType.getReturnType().getClassName());
            for (Type argType : methodType.getArgumentTypes()) {
                System.out.println("Parameter: " + argType.getClassName());
            }
            super.visitEnd();
        }
    }
}