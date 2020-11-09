package com.openxu.hkchart.bean;


/**
 * autour : openXu
 * date : 2018/6/8 9:40
 * className : RoseBean
 * version : 1.0
 * description : 请添加类说明
 */
public class RoseBean {

    private float count;
    private String ClassName;

    public RoseBean() {
    }

    public RoseBean(float count, String className) {
        this.count = count;
        ClassName = className;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }
}
