package com.openxu.cview.chart.bean;

import android.graphics.RectF;
import android.graphics.Region;

/**
 * autour : openXu
 * date : 2017/7/24 11:04
 * className : PieChartBean
 * version : 1.0
 * description : 柱状图数据
 */
public class BarBean {

    private float num;
    private String name;

    //bar绘制矩形
    private RectF arcRect;
    //触摸相关
    private Region region;     //扇形区域--用于判断手指触摸点是否在此范围

    @Override
    public String toString() {
        return "BarBean{" +
                "num=" + num +
                ", name='" + name + '\'' +
                '}';
    }

    public BarBean() {
    }

    public BarBean(float num, String name) {
        this.num = num;
        this.name = name;
    }
    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RectF getArcRect() {
        return arcRect;
    }

    public void setArcRect(RectF arcRect) {
        this.arcRect = arcRect;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
