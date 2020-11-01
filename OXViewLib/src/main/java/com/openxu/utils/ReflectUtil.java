package com.openxu.utils;

import java.lang.reflect.Field;

public class ReflectUtil {


    public static Object getField(Object obj, String field){
        try {
            Field field1 = obj.getClass().getDeclaredField(field);
            field1.setAccessible(true);
            return field1.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
