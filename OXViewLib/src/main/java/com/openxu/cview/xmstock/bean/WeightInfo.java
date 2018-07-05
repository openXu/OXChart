package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : WeightInfo
 * version : 1.0
 * description : 权重
 */
public class WeightInfo {

    private List<List<String>> weight_line;
    private String weight_tag;

    public List<List<String>> getWeight_line() {
        return weight_line;
    }

    public void setWeight_line(List<List<String>> weight_line) {
        this.weight_line = weight_line;
    }

    public String getWeight_tag() {
        return weight_tag;
    }

    public void setWeight_tag(String weight_tag) {
        this.weight_tag = weight_tag;
    }
}
