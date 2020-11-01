package com.openxu.cview.xmstock20201030.build;

import android.graphics.PointF;

/**
 * 图表上的一个点，包含数据及绘制坐标
 */
public class DataPoint {

    public String valueX;    //x轴  时间
    public String valueY;    //对应y轴上的值
    public PointF point;     //绘制的点的坐标

    public DataPoint(String valueX, String valueY, PointF point) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.point = point;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "valueX='" + valueX + '\'' +
                ", valueY='" + valueY + '\'' +
                ", point=" + point +
                '}';
    }
}
