package com.fiafeng.dynamicClass.utils;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.springframework.asm.Opcodes;

public class LocalVariableInfoVisitor extends ClassVisitor {
    public LocalVariableInfoVisitor(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodVisitor(api) {
            @Override
            public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                System.out.println("Local variable: name = " + name + ", type = " + descriptor);
            }
        };
    }


    public static void ss(String className) throws Exception {
        ClassReader classReader = new ClassReader(className); // 替换为你的类的全限定名
        ClassVisitor classVisitor = new LocalVariableInfoVisitor(Opcodes.ASM5);
        classReader.accept(classVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
    }
}