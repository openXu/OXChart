package com.openxu.chart.bar;

import android.graphics.Rect;
import android.graphics.Region;

public class Bar {

    private float valuey;
    private String valuex;
    //bar绘制矩形
    private Rect rect;
    //触摸相关
    private Region region;     //扇形区域--用于判断手指触摸点是否在此范围

    public Bar(String valuex, float valuey) {
        this.valuey = valuey;
        this.valuex = valuex;
    }

    public float getValuey() {
        return valuey;
    }

    public void setValuey(float valuey) {
        this.valuey = valuey;
    }

    public String getValuex() {
        return valuex;
    }

    public void setValuex(String valuex) {
        this.valuex = valuex;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
