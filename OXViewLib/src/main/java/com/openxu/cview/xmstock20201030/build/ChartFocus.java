package com.openxu.cview.xmstock20201030.build;

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
public class ChartFocus {


    private List<PointF> points;
    private List<Object> focusData;

    public List<PointF> getPoints() {
        return points;
    }

    public void setPoints(List<PointF> points) {
        this.points = points;
    }

    public List<Object> getFocusData() {
        return focusData;
    }

    public void setFocusData(List<Object> focusData) {
        this.focusData = focusData;
    }
}
