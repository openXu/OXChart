package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : TrendInfo
 * version : 1.0
 * description : 涨跌趋势
 */
public class TrendInfo {

    private List<List<String>> day;
    private List<List<String>> month;
    private String trend_tag;

    public List<List<String>> getDay() {
        return day;
    }

    public void setDay(List<List<String>> day) {
        this.day = day;
    }

    public List<List<String>> getMonth() {
        return month;
    }

    public void setMonth(List<List<String>> month) {
        this.month = month;
    }

    public String getTrend_tag() {
        return trend_tag;
    }

    public void setTrend_tag(String trend_tag) {
        this.trend_tag = trend_tag;
    }
}
