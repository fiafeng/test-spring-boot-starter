package com.fiafeng.common.annotation.conditional.matches;

import com.fiafeng.common.annotation.conditional.ConditionalOnClassList;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

public class ConditionalOnClassListMatches implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        try {
            Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnClassList.class.getName());
            String[] names = new String[0];
            if (annotationAttributes != null) {
                names = (String[]) annotationAttributes.get("name");
            }

            for (String name : names) {
                try {
                    Class<?> aClass = Class.forName(name);
                    return true;
                }catch (Exception ignore){
                }

            }
            return false;
        }catch (Exception e){
            return true;
        }

    }
}
