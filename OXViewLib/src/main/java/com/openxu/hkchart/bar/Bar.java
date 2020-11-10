package com.openxu.hkchart.bar;

import android.graphics.Region;

import java.util.List;

public class Bar {

    private List<Float> valuey ;
    private String valuex;
    //bar绘制矩形
//    private Rect rect;
    //触摸相关
    private Region region;     //扇形区域--用于判断手指触摸点是否在此范围

    public Bar(String valuex, List<Float> valuey) {
        this.valuey = valuey;
        this.valuex = valuex;
    }

    public List<Float> getValuey() {
        return valuey;
    }

    public void setValuey(List<Float> valuey) {
        this.valuey = valuey;
    }

    public String getValuex() {
        return valuex;
    }

    public void setValuex(String valuex) {
        this.valuex = valuex;
    }

//    public Rect getRect() {
//        return rect;
//    }

//    public void setRect(Rect rect) {
//        this.rect = rect;
//    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
