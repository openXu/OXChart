package com.openxu.cview.xmstock20201030.build;

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
    /**可设置*/
    public boolean showLable;//是否显示lable
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    public Orientation lableOrientation;//lable显示在坐标轴那个方向
    public LABLE_TYPE lableType;//lable显示的类型，X轴默认String，Y轴可设置百分数、int、float
    public String[] lables;  //X坐标轴刻度
    //X  Y轴数据
    public int lableNum = 5;
    public List datas;
    public String field;

    /**自动计算*/
    public List<MarkPoint> markPointList = new ArrayList<>();
    public float cal_mark =  1;    //轴刻度间隔
    public float cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
    public float cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值

    public enum LABLE_TYPE{
        PERCENTAGE,  /*百分比*/
        INTEGER,
        FLOAT,
        STRING
    }

    private AxisMark() {
    }

    public static class Builder{
        private AxisMark axisMark;
        public Builder(Context context) {
            axisMark = new AxisMark();
            axisMark.context = context;
            axisMark.showLable = false;
            axisMark.textSize = (int)axisMark.context.getResources().getDimension(R.dimen.chart_axis_textsize);
            axisMark.textColor = Color.parseColor("#939393");
            axisMark.textSpace = DensityUtil.dip2px(axisMark.context, 5);
        }
        public Builder showLable(boolean showLable) {
            axisMark.showLable = showLable;
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
        public Builder lableType(LABLE_TYPE lableType) {
            axisMark.lableType = lableType;
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
        public Builder lables(String[] lables) {
            axisMark.lables = lables;
            axisMark.lableNum = lables.length;
            return this;
        }
        public Builder lableNum(int lableNum) {
            axisMark.lableNum = lableNum;
            return this;
        }
        public Builder datas(List datas) {
            axisMark.datas = datas;
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
        public PointF point;     //绘制的点的坐标

        public MarkPoint(String value, PointF point) {
            this.value = value;
            this.point = point;
        }
    }
}