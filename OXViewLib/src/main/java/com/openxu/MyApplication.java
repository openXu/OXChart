package com.openxu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.Stack;

/**
 * Author: openX
 * Time: 2019/2/23 14:49
 * class: BaseApplication
 * Description: Application请继承该基类
 */
public class MyApplication extends Application {

    private static Stack<Activity> activityStack = new Stack<>();
    private static Context mcontext;
    protected static MyApplication application;

    @Override
    public void onCreate() {
        application = this;
        mcontext = getApplicationContext();
        super.onCreate();
    }

    public static MyApplication getApplication() {
        return application;
    }
    public static Context getContext() {
        return mcontext;
    }




}
