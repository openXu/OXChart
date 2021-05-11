package com.openxu.hkchart.data;
/**
 * LineChart 的数据
 */
public class LinePoint {

    private float valuey ;        //y轴值
    private String valuex;        //x轴刻度值
    private String valuexfocus;   //x轴焦点值，如果为null，则默认为valuex
//    private PointF point;

    public LinePoint(String valuex, float valuey) {
        this.valuey = valuey;
        this.valuex = valuex;
    }

    public LinePoint(String valuex, String valuexfocus, float valuey) {
        this.valuey = valuey;
        this.valuex = valuex;
        this.valuexfocus = valuexfocus;
    }

    public String getValuexfocus() {
        return valuexfocus;
    }

    public void setValuexfocus(String valuexfocus) {
        this.valuexfocus = valuexfocus;
    }
//    public PointF getPoint() {
//        return point;
//    }
//
//    public void setPoint(PointF point) {
//        this.point = point;
//    }

    public float getValuey() {
        return valuey;
    }

    public void setValuey(float valuey) {
        this.valuey = valuey;
    }

    public String getValuex() {
        return valuex;
    }

    public void setValuex(String valuex) {
        this.valuex = valuex;
    }


}
