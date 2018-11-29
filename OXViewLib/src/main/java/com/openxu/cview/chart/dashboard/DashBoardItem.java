package com.openxu.cview.chart.dashboard;

/**
 * autour : openXu
 * date : 2018/4/20 15:14
 * className : DashBoardItem
 * version : 1.0
 * description : 请添加类说明
 */
public class DashBoardItem {

    private int color;
    private String lable;
    private float num;

    private float angle;

    public DashBoardItem() {
    }
    public DashBoardItem(int color, String lable, float num) {
        this.color = color;
        this.lable = lable;
        this.num = num;
    }

    @Override
    public String toString() {
        return "DashBoardItem{" +
                "color=" + color +
                ", lable='" + lable + '\'' +
                ", num=" + num +
                ", angle=" + angle +
                '}';
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }
}
