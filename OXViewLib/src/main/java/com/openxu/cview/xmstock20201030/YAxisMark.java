package com.openxu.cview.xmstock20201030;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import com.openxu.cview.R;
import com.openxu.cview.xmstock20201030.build.Orientation;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

//图表坐标轴

/**
 * 坐标轴刻度标签
 */
public class YAxisMark {

    public Context context;
    /**可设置*/
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    public int lableNum = 5;
    /**自动计算*/
    public List<MarkPoint> markPointList = new ArrayList<>();
    public float cal_mark;    //轴刻度间隔
    public float cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
    public float cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值
    private YAxisMark() {
    }
    public static class Builder{
        private YAxisMark axisMark;
        public Builder(Context context) {
            axisMark = new YAxisMark();
            axisMark.context = context;
            axisMark.textSize = (int)axisMark.context.getResources().getDimension(R.dimen.chart_axis_textsize);
            axisMark.textColor = Color.parseColor("#939393");
            axisMark.textSpace = DensityUtil.dip2px(axisMark.context, 5);
        }
        public Builder textSize(int textSize) {
            axisMark.textSize = textSize;
            return this;
        }
        public Builder textColor(int textColor) {
            axisMark.textColor = textColor;
            return this;
        }
        public Builder lableNum(int lableNum) {
            axisMark.lableNum = lableNum;
            return this;
        }
        public YAxisMark build(){
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