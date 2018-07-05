package com.openxu.cview.xmstock.bean;

import android.graphics.PointF;

/**
 * autour : xiami
 * date : 2018/3/13 16:44
 * className : DataPoint
 * version : 1.0
 * description : 图表连线上单个点
 */
public class DataPoint {
    private String valueX;   //x轴  时间
    private float valueY;    //对应y轴上的值
    private PointF point;    //绘制的点的坐标
    public DataPoint() {
    }
    public DataPoint(String valueX, float valueY, PointF point) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.point = point;
    }

    public String getValueX() {
        return valueX;
    }

    public void setValueX(String valueX) {
        this.valueX = valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public void setValueY(float valueY) {
        this.valueY = valueY;
    }

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }
}
