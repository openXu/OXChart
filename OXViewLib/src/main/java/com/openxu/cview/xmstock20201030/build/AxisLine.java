package com.openxu.cview.xmstock20201030.build;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;

import com.openxu.utils.DensityUtil;



/**
 * 图表轴线（网格线）
 */
public class AxisLine {

    /**可设置*/
    public AxisLineType type;   //轴线类型
    public int lineColor;       //轴线颜色
    public int lineWidth;       //轴线宽度
    /**自动计算*/
    public PointF pointStart, pointEnd;

    public static class Builder{
        private AxisLine axisLine;
        public Builder(Context context) {
            axisLine = new AxisLine();
            axisLine.lineColor = Color.parseColor("#939393");  //默认灰色
            axisLine.lineWidth = DensityUtil.dip2px(context, .8f);
        }
        public Builder lineColor(int color) {
            axisLine.lineColor = color;
            return this;
        }
        public Builder lineWidth(int lineWidth) {
            axisLine.lineWidth = lineWidth;
            return this;
        }

        public Builder lineType(AxisLineType type) {
            axisLine.type = type;
            return this;
        }

        public AxisLine build(){
            return axisLine;
        }
    }


}
