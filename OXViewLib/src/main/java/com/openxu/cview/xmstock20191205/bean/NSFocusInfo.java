package com.openxu.cview.xmstock20191205.bean;

import android.graphics.PointF;

import com.openxu.cview.xmstock.bean.DataPoint;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/14 10:23
 * className : FocusInfo
 * version : 1.0
 * description : 触摸焦点封装
 */
public class NSFocusInfo {


    private PointF point;
    private List<DataPoint> dataPoints;
    private List<String> focusData;

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public List<String> getFocusData() {
        return focusData;
    }

    public void setFocusData(List<String> focusData) {
        this.focusData = focusData;
    }
}
