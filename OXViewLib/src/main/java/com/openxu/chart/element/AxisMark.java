package com.openxu.chart.element;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import com.openxu.cview.R;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

//图表坐标轴

/**
 * 坐标轴刻度标签
 */
public class AxisMark {

    public Context context;
    /**可选设置*/
    public boolean showLable;//是否显示lable
    public boolean showMark; //是否显示坐标轴上的刻度线
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    public Orientation lableOrientation;//lable显示在坐标轴那个方向
    public AxisMarkLableType lableType;//lable显示的类型，X轴默认String，Y轴可设置百分数、int、float
    /**(必选)设置坐标轴刻度数据，以下两种必选一种设置*/
    //方式1
    public String[] lables;  //X坐标轴刻度
    //方式2
    public int lableNum = 5;
    public String field;

    /**自动计算*/
    public List<MarkPoint> markPointList = new ArrayList<>();
    public float cal_mark =  1;    //轴刻度间隔
    public float cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
    public float cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值


    private AxisMark() {
    }

    public static class Builder{
        private AxisMark axisMark;
        public Builder(Context context) {
            axisMark = new AxisMark();
            axisMark.context = context;
            axisMark.showLable = true;
            axisMark.showMark = true;
            axisMark.textSize = (int)axisMark.context.getResources().getDimension(R.dimen.chart_axis_textsize);
            axisMark.textColor = Color.parseColor("#939393");
            axisMark.textSpace = DensityUtil.dip2px(axisMark.context, 5);
            axisMark.lableOrientation = Orientation.BOTTOM;
            axisMark.lableType = AxisMarkLableType.STRING;
        }
        public Builder showLable(boolean showLable) {
            axisMark.showLable = showLable;
            return this;
        }
        public Builder showMark(boolean showMark) {
            axisMark.showMark = showMark;
            return this;
        }
        public Builder textSize(int textSize) {
            axisMark.textSize = textSize;
            return this;
        }
        public Builder textColor(int textColor) {
            axisMark.textColor = textColor;
            return this;
        }
        public Builder textSpace(int textSpace) {
            axisMark.textSpace = textSpace;
            return this;
        }
        /**
         * X轴只允许设置 Orientation.BOTTOM
         * Y Left ：Orientation.LEFT / Orientation.TOP
         * Y Right : Orientation.RIGHT / Orientation.TOP
         * @param lableOrientation
         * @return
         */
        public Builder lableOrientation(Orientation lableOrientation) {
            axisMark.lableOrientation = lableOrientation;
            return this;
        }

        public Builder lableType(AxisMarkLableType lableType) {
            axisMark.lableType = lableType;
            return this;
        }
        public Builder lables(String[] lables) {
            axisMark.lables = lables;
            axisMark.lableNum = lables.length;
            return this;
        }
        public Builder lableNum(int lableNum) {
            axisMark.lableNum = lableNum;
            return this;
        }
        public Builder field(String field) {
            axisMark.field = field;
            return this;
        }

        public AxisMark build(){
            return axisMark;
        }
    }

    public static class MarkPoint {
        public String value;    //对应轴上的值
        public PointF textPoint; //绘制文字的点的坐标
        public PointF markPoint; //绘制刻度的坐标

        public MarkPoint(String value, PointF textPoint, PointF markPoint) {
            this.value = value;
            this.textPoint = textPoint;
            this.markPoint = markPoint;
        }
    }
}