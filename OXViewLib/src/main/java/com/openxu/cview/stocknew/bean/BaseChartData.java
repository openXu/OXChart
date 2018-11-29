package com.openxu.cview.stocknew.bean;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/14 10:54
 * className : BaseChartData
 * version : 1.0
 * description : 图表通用数据
 */
public class BaseChartData {

    private String name;
    private float num;

    @Override
    public String toString() {
        return "BaseChartData{" +
                "name='" + name + '\'' +
                ", num=" + num +
                '}';
    }

    public BaseChartData() {
    }
    public BaseChartData(String name, float num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }
}