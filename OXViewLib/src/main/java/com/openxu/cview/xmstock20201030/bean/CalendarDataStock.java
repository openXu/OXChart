package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 日历数据
 */
public class CalendarDataStock {

    private String name;
    private String code;
    private String symbol;
    private int flag;
    private String chg;
    private String count;
    private String max_real_chg;
    private List<String> trend_line;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getChg() {
        return chg;
    }

    public void setChg(String chg) {
        this.chg = chg;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMax_real_chg() {
        return max_real_chg;
    }

    public void setMax_real_chg(String max_real_chg) {
        this.max_real_chg = max_real_chg;
    }

    public List<String> getTrend_line() {
        return trend_line;
    }

    public void setTrend_line(List<String> trend_line) {
        this.trend_line = trend_line;
    }
}
