package com.openxu.cview.xmstock.bean;

import android.graphics.PointF;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/14 10:23
 * className : FocusInfo
 * version : 1.0
 * description : 触摸焦点封装
 */
public class FocusInfo {


    private PointF point;
    private List<DataPoint> focusData;

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public List<DataPoint> getFocusData() {
        return focusData;
    }

    public void setFocusData(List<DataPoint> focusData) {
        this.focusData = focusData;
    }
}
