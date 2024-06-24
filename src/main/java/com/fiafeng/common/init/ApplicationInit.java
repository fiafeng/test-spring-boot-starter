package com.fiafeng.common.init;


import com.fiafeng.common.annotation.ApplicationInitAnnotation;

@ApplicationInitAnnotation()
public interface ApplicationInit {

   default void init(){

   };
}
