package com.fiafeng.common.utils;

public class ClassUtils {

    /**
     * 判断指定的类数组是否存在系统内，logic表示 结果集合的是 使用  与还是或
     * @param logic 结果集合的是 使用  与还是或，true 是 ‘与的关系'，false 是 ‘或的关系’
     * @param aClasses 类型数组
     * @return 判断指定的类数组是否存在系统内
     */
    public static boolean classIsExists(boolean logic, String ...aClasses){
        boolean flag = true;
        if (aClasses.length >= 1){
            for (String aClass : aClasses) {
                if (logic){
                    try {
                        Class.forName(aClass);
                    }catch (Exception e){
                        return false;
                    }
                }else {
                    try {
                        Class.forName(aClass);
                        return true;
                    }catch (Exception ignore){
                    }
                }
            }
        }
        return flag;
    }

    public static boolean classIsExistsOR(String ...aClasses){
        return classIsExists(false, aClasses);
    }

    public static boolean classIsExistsAND(String ...aClasses){
        return classIsExists(true, aClasses);
    }
}
