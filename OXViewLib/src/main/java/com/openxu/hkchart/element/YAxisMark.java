package com.openxu.hkchart.element;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import com.openxu.utils.DensityUtil;
import com.openxu.utils.NumberFormatUtil;

//图表坐标轴

/**
 * 坐标轴刻度标签
 */
public class YAxisMark {

    private String TAG = "YAxisMark";
    /**可设置*/
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离

    public int lineWidth;
    public int lineColor;

    public int lableNum;
    public MarkType markType;
    public String unit = "";   //单位 KW

    /**自动计算*/
//    public List<MarkPoint> markPointList = new ArrayList<>();

    public float textHeight, textLead;

    public float cal_mark;    //轴刻度间隔
    public float cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
    public float cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值

    public enum MarkType{
        Integer,
        Float,
        Percent
    }

    private YAxisMark() {
    }
    public static class Builder{
        private YAxisMark axisMark;
        public Builder(Context context) {
            axisMark = new YAxisMark();
            axisMark.textSize = DensityUtil.sp2px(context, 10);
            axisMark.textColor = Color.parseColor("#bcbcbc");
            axisMark.textSpace = DensityUtil.dip2px(context, 5);
            axisMark.lineWidth = DensityUtil.dip2px(context, 0.7f);
            axisMark.lineColor = Color.parseColor("#dcdcdc");

            axisMark.lableNum = 5;
            axisMark.markType = MarkType.Integer;
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
        public Builder markType(MarkType markType) {
            axisMark.markType = markType;
            return this;
        }
        public Builder unit(String unit) {
            axisMark.unit = unit;
            return this;
        }
        public YAxisMark build(){
            return axisMark;
        }
    }

    public String getMarkText(float value){
        switch (markType){
            case Integer:
                return (int)value+"";
            case Float:
                return NumberFormatUtil.formattedDecimal(value);
            case Percent:
                return NumberFormatUtil.formattedDecimalToPercentage(value);
        }
        return value+"";
    }

    public static class MarkPoint {
        public float value;    //对应轴上的值
        public PointF point;     //绘制的点的坐标

        public MarkPoint(float value, PointF point) {
            this.value = value;
            this.point = point;
        }
    }
}