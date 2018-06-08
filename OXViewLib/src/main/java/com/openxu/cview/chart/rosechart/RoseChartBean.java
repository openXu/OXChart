package com.openxu.cview.chart.rosechart;

import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 11:04
 * className : RoseChartBean
 * version : 1.0
 * description : 南丁格尔玫瑰图数据
 */
public class RoseChartBean {

    private float per;
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
    private PointF tagTextPoint;

    //右侧lable
    private RectF colorRect;
    private PointF nameTextPoint;
    private PointF perTextPoint;
    private PointF dashPathPointStart;
    private PointF dashPathPointEnd;

    @Override
    public String toString() {
        return "RoseChartBean{" +
                "per=" + per +
                ", name='" + name + '\'' +
                '}';
    }

    public RoseChartBean(float per, String name) {
        this.per = per;
        this.name = name;
    }

    public PointF getDashPathPointStart() {
        return dashPathPointStart;
    }

    public void setDashPathPointStart(PointF dashPathPointStart) {
        this.dashPathPointStart = dashPathPointStart;
    }

    public PointF getDashPathPointEnd() {
        return dashPathPointEnd;
    }

    public void setDashPathPointEnd(PointF dashPathPointEnd) {
        this.dashPathPointEnd = dashPathPointEnd;
    }

    public PointF getNameTextPoint() {
        return nameTextPoint;
    }

    public void setNameTextPoint(PointF nameTextPoint) {
        this.nameTextPoint = nameTextPoint;
    }

    public PointF getPerTextPoint() {
        return perTextPoint;
    }

    public void setPerTextPoint(PointF perTextPoint) {
        this.perTextPoint = perTextPoint;
    }

    public RectF getColorRect() {
        return colorRect;
    }

    public void setColorRect(RectF colorRect) {
        this.colorRect = colorRect;
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

    public float getPer() {
        return per;
    }

    public void setPer(float per) {
        this.per = per;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
