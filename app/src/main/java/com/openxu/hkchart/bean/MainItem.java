package com.openxu.hkchart.bean;

import android.app.Activity;

/**
 * Author: openXu
 * Time: 2021/5/10 17:45
 * class: MainItem
 * Description:
 */
public class MainItem {
    private String name;
    private Class<? extends  Activity> gotoClass;

    public MainItem(String name, Class<? extends Activity> gotoClass) {
        this.name = name;
        this.gotoClass = gotoClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Activity> getGotoClass() {
        return gotoClass;
    }

    public void setGotoClass(Class<? extends Activity> gotoClass) {
        this.gotoClass = gotoClass;
    }
}
