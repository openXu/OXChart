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
    public SharedPreferences getSp() {
        return context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().clear().commit();
    }

}

