package com.openxu.chart.bean;


/**
 * autour : openXu
 * date : 2018/6/8 9:40
 * className : PieChartBean
 * version : 1.0
 * description : 请添加类说明
 */
public class PieChartBean {
    private float Numner;
    private String Name;

    public PieChartBean() {
    }

    public PieChartBean(float Numner, String Name) {
        this.Numner = Numner;
        this.Name = Name;
    }

    public float getNumner() {
        return Numner;
    }

    public void setNumner(float numner) {
        Numner = numner;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
