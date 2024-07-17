package com.fiafeng.dynamicClass.utils;

import java.lang.reflect.Modifier;

public class ModifiersUtils {


    public static String getModifiersInfo(int modifiers){
        StringBuilder builder = new StringBuilder();
        if (Modifier.isPublic(modifiers)){
            builder.append("public ");
        }
        if (Modifier.isPrivate(modifiers)){
            builder.append("private ");
        }
        if (Modifier.isProtected(modifiers)){
            builder.append("protected ");
        }
        if (Modifier.isAbstract(modifiers)){
            builder.append("abstract ");
        }
        if (Modifier.isStatic(modifiers)){
            builder.append("static ");
        }
        if (Modifier.isFinal(modifiers)){
            builder.append("final ");
        }

        if (Modifier.isVolatile(modifiers)){
            builder.append("volatile ");
        }

        return builder.toString();
    }
}
