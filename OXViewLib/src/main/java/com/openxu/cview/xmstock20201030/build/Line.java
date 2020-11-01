package com.openxu.cview.xmstock20201030.build;

import android.content.Context;
import android.graphics.Color;

import com.openxu.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

//图表曲线
public class Line<D>{

    public int lineColor;        //线条颜色
    public int lineWidth;        //线条粗细
    public AnimType animType;    //线条动画
    public LineType lineType;    //线条类型，默认曲线（曲线LineType.CURVE   折线LineType.BROKEN）
    public Orientation orientation;   //* 参照那个方向Y轴的坐标 Orientation.LEFT or Orientation.RIGHT
    public List<D> datas;        //* 数据
    public String field_x;       //* x轴对应的数据中的成员变量字符串
    public String field_y;       //* y轴对应的数据中的成员变量字符串
    public int dataNumCount;     //可以设置总共的数量，默认=datas.size()
    /**计算*/
    public List<DataPoint> linePointList = new ArrayList<>();

    public enum LineType{
        CURVE,   //曲线
        BROKEN   //折线

    }
    private Line() {
    }

    public static class Builder<D>{
        Line line;
        public Builder(Context context) {
            line = new Line();
            line.lineColor = Color.GREEN;
            line.lineWidth = DensityUtil.dip2px(context, 1.5f);
            line.animType = AnimType.NONE;
            line.lineType = LineType.CURVE;
        }

        public Builder lineColor(int color) {
            line.lineColor = color;
            return this;
        }
        public Builder lineWidth(int lineWidth) {
            line.lineWidth = lineWidth;
            return this;
        }
        public Builder lineType(LineType lineType) {
            line.lineType = lineType;
            return this;
        }
        public Builder orientation(Orientation orientation) {
            line.orientation = orientation;
            return this;
        }
        public Builder animType(AnimType animType) {
            line.animType = animType;
            return this;
        }

        public Builder datas(List<D> datas) {
            line.datas = datas;
            return this;
        }

        public Builder field_x(String field) {
            line.field_x = field;
            return this;
        }
        public Builder field_y(String field) {
            line.field_y = field;
            return this;
        }

        public Line<D> build(){
            return  line;
        }
    }


}