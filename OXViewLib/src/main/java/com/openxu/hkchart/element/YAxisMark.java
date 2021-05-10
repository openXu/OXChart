package com.openxu.hkchart.element;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import com.openxu.utils.DensityUtil;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 坐标轴刻度标签
 */
public class YAxisMark {

    private String TAG = "YAxisMark";
    /**可设置*/
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    public Typeface numberTypeface = Typeface.DEFAULT;

    public int lineWidth;
    public int lineColor;

    public int lableNum;
    public MarkType markType;
    public int digits;   //小数点保留位数
    public String unit = "";   //单位 KW

    /**自动计算*/
//    public List<MarkPoint> markPointList = new ArrayList<>();

    public float textHeight, textLead;

    public float cal_mark;    //轴刻度间隔
    public float cal_mark_max =  Float.MIN_VALUE;    //Y轴刻度最大值
    public float cal_mark_min =  Float.MAX_VALUE;    //Y轴刻度最小值

    private YAxisMark() {
    }
    public static class Builder{
        private YAxisMark axisMark;
        public Builder(Context context) {
            axisMark = new YAxisMark();
            axisMark.textSize = DensityUtil.sp2px(context, 10);
            axisMark.textColor = Color.parseColor("#BCBCBC");
            axisMark.textSpace = DensityUtil.dip2px(context, 5);
            axisMark.lineWidth = DensityUtil.dip2px(context, 0.7f);
            axisMark.lineColor = Color.parseColor("#e7eaef");

            axisMark.lableNum = 5;
            axisMark.markType = MarkType.Integer;
        }
        public Builder numberTypeface(Typeface numberTypeface) {
            axisMark.numberTypeface = numberTypeface;
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
        public Builder lineWidth(int lineWidth) {
            axisMark.lineWidth = lineWidth;
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
        public Builder digits(int digits) {
            axisMark.digits = digits;
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
                return (long)value+"";
            case Float:
                return formattedDecimal(value, digits);
            case Percent:
                return formattedDecimalToPercentage(value, digits);
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



    /**
     * double 类型转换成保留2位小数百分数    0.1 -> 10.00%
     * @param decimal
     * @return
     */
    public static String formattedDecimalToPercentage(double decimal, int digits){
        //获取格式化对象
        NumberFormat format = NumberFormat.getPercentInstance();
//        format.setGroupingUsed(true);
        //设置百分数精确度2即保留两位小数
        format.setMinimumFractionDigits(digits);
        return format.format(decimal);
    }

    /**
     * double 类型转换成保留2位小数    0.1 -> 0.10
     * @param decimal
     * @return
     */
    public static String formattedDecimal(double decimal, int digits){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(digits);
        /*
         * setMinimumFractionDigits设置成2
         * 如果不这么做，那么当value的值是100.00的时候返回100
         * 而不是100.00
         */
//        nf.setMinimumFractionDigits(digits);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        /*
         * 如果想输出的格式用逗号隔开，可以设置成true
         */
        nf.setGroupingUsed(false);
        return nf.format(decimal);
    }





}