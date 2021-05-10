package com.openxu.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.openxu.MyApplication;

import java.io.File;

/**
 * SharedPreferences工具类
 */
public class SharedData {


    public static final String KEY_DEBUG = "debug";

    private Context context;
    private static final String SP_NAME = "OXChart";
    private static final SharedData instance = new SharedData();

    public static SharedData getInstance() {
        return instance;
    }
    private SharedData() {
        this.context = MyApplication.getApplication().getApplicationContext();
    }

    //获取sp文件名称
    public String getSpFileName(){
        return SP_NAME+".xml";
    }

    //获取sp文件
    public File getSpFile(){
        File file = new File("/data/data/com.yaxon.bubiao/shared_prefs/"+SP_NAME+".xml");
        return file;
    }
    public boolean mkSpDir(){
        File dir = new File("/data/data/com.yaxon.bubiao/shared_prefs/");
        if(!dir.exists())
            return dir.mkdir();
        return true;
    }
    public <T> T getData(String key, Class clazz) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
        Object obj;
        if (clazz == String.class)
            obj = sharedPreferences.getString(key, "");
        else if (clazz == Integer.class)
            obj = sharedPreferences.getInt(key, 0);
        else if (clazz == Float.class)
            obj = sharedPreferences.getFloat(key, 0);
        else if (clazz == Long.class)
            obj = sharedPreferences.getLong(key, 0);
        else if (clazz == Boolean.class)
            obj = sharedPreferences.getBoolean(key, false);
        else
            obj = sharedPreferences.getString(key, "");
        return (T) obj;
    }

    public void saveData(String key, Object data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (data instanceof String)
            editor.putString(key, (String) data);
        else if (data instanceof Integer)
            editor.putInt(key, (int) data);
        else if (data instanceof Float)
            editor.putFloat(key, (float) data);
        else if (data instanceof Long)
            editor.putLong(key, (long) data);
        else if (data instanceof Boolean)
            editor.putBoolean(key, (boolean) data);
        else
            editor.putString(key, (String) data);
        editor.commit();
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().clear().commit();
    }

}

