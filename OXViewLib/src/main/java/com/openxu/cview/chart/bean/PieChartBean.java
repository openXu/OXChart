package com.openxu.cview.chart.bean;

import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 11:04
 * className : PieChartBean
 * version : 1.0
 * description : 饼状图数据
 */
public class PieChartBean {

    private float num;
    private String name;

    //触摸相关
    private Region region;     //扇形区域--用于判断手指触摸点是否在此范围
    private RectF rectLable;   //矩形区域--用于判断手指触摸点是否在此范围
    //扇形相关
    private RectF arcRect;
    private float startAngle;
    private float sweepAngle;
    //tag线段
    private List<PointF> tagLinePoints;
    private String tagStr;
    private PointF tagTextPoint;

//    //右侧lable
//    private RectF colorRect;
//    private PointF nameTextPoint;
//    private PointF perTextPoint;
//    private PointF dashPathPointStart;
//    private PointF dashPathPointEnd;

    @Override
    public String toString() {
        return "RoseChartBean{" +
                "num=" + num +
                ", name='" + name + '\'' +
                '}';
    }

    public PieChartBean(float num, String name) {
        this.num = num;
        this.name = name;
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }


    public PointF getTagTextPoint() {
        return tagTextPoint;
    }

    public void setTagTextPoint(PointF tagTextPoint) {
        this.tagTextPoint = tagTextPoint;
    }

    public List<PointF> getTagLinePoints() {
        return tagLinePoints;
    }

    public void setTagLinePoints(List<PointF> tagLinePoints) {
        this.tagLinePoints = tagLinePoints;
    }

    public RectF getArcRect() {
        return arcRect;
    }

    public void setArcRect(RectF arcRect) {
        this.arcRect = arcRect;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public RectF getRectLable() {
        return rectLable;
    }

    public void setRectLable(RectF rectLable) {
        this.rectLable = rectLable;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
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
}
