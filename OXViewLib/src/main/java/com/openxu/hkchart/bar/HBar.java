package com.openxu.hkchart.bar;

import android.graphics.Region;

public class HBar {

    private String lable;
    private Float value ;
    //bar绘制矩形
//    private Rect rect;
    //触摸相关
    private Region region;     //扇形区域--用于判断手指触摸点是否在此范围

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public HBar(String lable, Float value) {
        this.lable = lable;
        this.value = value;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
