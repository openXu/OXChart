package com.openxu.hkchart.element;

import android.content.Context;
import android.graphics.Color;

import com.openxu.utils.DensityUtil;

/**
 * 坐标轴刻度标签
 */
public class XAxisMark {

    /**可设置*/
    public int textSize;   //设置坐标文字大小
    public int textColor;  //设置坐标文字颜色
    public int textSpace;  //设置坐标字体与横轴的距离
    //2选1设置
    public int lableNum = 5;
    public String[] lables;
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
        public Builder lables(String[] lables) {
            axisMark.lables = lables;
            axisMark.lableNum = lables.length;
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