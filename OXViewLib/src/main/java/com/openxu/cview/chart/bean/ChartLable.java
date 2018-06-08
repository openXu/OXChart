package com.openxu.cview.chart.bean;

/**
 * autour : openXu
 * date : 2017/10/11 14:08
 * className : ChartLable
 * version : 1.0
 * description : 图表lable文字对象，包含文字大小和颜色
 */
public class ChartLable {

    private String text;
    private int textSize;
    private int textColor;

    public ChartLable(String text, int textSize, int textColor) {
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
    }
    public ChartLable() {
    }
    @Override
    public String toString() {
        return "ChartLable{" +
                "text='" + text + '\'' +
                ", textSize=" + textSize +
                ", textColor=" + textColor +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
