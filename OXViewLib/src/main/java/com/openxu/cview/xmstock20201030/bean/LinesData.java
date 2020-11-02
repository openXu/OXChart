package com.openxu.cview.xmstock20201030.bean;

public class LinesData {

    private float num1;
    private float num2;
    private String xlable;

    public LinesData(float num1, float num2, String xlable) {
        this.num1 = num1;
        this.num2 = num2;
        this.xlable = xlable;
    }

    public float getNum1() {
        return num1;
    }

    public void setNum1(float num1) {
        this.num1 = num1;
    }

    public float getNum2() {
        return num2;
    }

    public void setNum2(float num2) {
        this.num2 = num2;
    }

    public String getXlable() {
        return xlable;
    }

    public void setXlable(String xlable) {
        this.xlable = xlable;
    }
}
