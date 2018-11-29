package com.openxu.cview.stocknew.bean;

import com.openxu.utils.NumberFormatUtil;

/**
 * autour : xiami
 * date : 2018/3/14 10:54
 * className : BaseChartData
 * version : 1.0
 * description : 公司业绩数据
 */
public class GsyjChartData {

    private String name;
    private float barNum;
    private float lineNum;
    public GsyjChartData() {
    }
    public GsyjChartData(String name, float barNum, float lineNum) {
        this.name = name;
        this.barNum = barNum;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return "GsyjChartData{" +
                "name='" + name + '\'' +
                ", barNum=" + barNum +
                ", lineNum=" + lineNum +
                ", 百分比=" + NumberFormatUtil.formattedDecimalToPercentage(lineNum) +
                '}';
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBarNum() {
        return barNum;
    }

    public void setBarNum(float barNum) {
        this.barNum = barNum;
    }

    public float getLineNum() {
        return lineNum;
    }

    public void setLineNum(float lineNum) {
        this.lineNum = lineNum;
    }
}