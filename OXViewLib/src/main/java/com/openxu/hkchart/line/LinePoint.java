package com.openxu.hkchart.line;

public class LinePoint {

    private Float valuey ;
    private String valuex;

//    private PointF point;

    public LinePoint(String valuex, Float valuey) {
        this.valuey = valuey;
        this.valuex = valuex;
    }

//    public PointF getPoint() {
//        return point;
//    }
//
//    public void setPoint(PointF point) {
//        this.point = point;
//    }

    public Float getValuey() {
        return valuey;
    }

    public void setValuey(Float valuey) {
        this.valuey = valuey;
    }

    public String getValuex() {
        return valuex;
    }

    public void setValuex(String valuex) {
        this.valuex = valuex;
    }


}
