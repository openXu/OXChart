package com.openxu.chart.element;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import com.openxu.cview.R;
import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 坐标轴刻度标签
 */
public class XAxisMark {

    /**可设置*/
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    public int lableNum = 5;

//    public int lineWidth;
//    public int lineColor;

    /**计算*/
    public float textHeight, textLead, drawPointY;
    private XAxisMark() {
    }
    public static class Builder{
        private XAxisMark axisMark;
        public Builder(Context context) {
            axisMark = new XAxisMark();
            axisMark.textSize = DensityUtil.sp2px(context, 10);
            axisMark.textColor = Color.parseColor("#333333");
            axisMark.textSpace = DensityUtil.dip2px(context, 5);
//            axisMark.lineWidth = DensityUtil.dip2px(context, 0.7f);
//            axisMark.lineColor = Color.parseColor("#5E5E5E");

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
        public XAxisMark build(){
            return axisMark;
        }
    }
}