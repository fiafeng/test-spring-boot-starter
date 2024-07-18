package com.fiafeng.mybatis.factory;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.springframework.core.convert.converter.Converter;


public class ObjectFactoryConverter implements Converter<String, ObjectFactory> {
    @Override
    public ObjectFactory convert(String source) {
        try {
            return (ObjectFactory) Class.forName(source).newInstance();
        }catch (Exception ignore){

        }
        return null;
    }
}
