package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 日历数据
 */
public class CalendarData {

    private String date;
    private String week;
    private List<CalendarDataStock> stock_list;

    @Override
    public String toString() {
        return "CalendarData{" +
                "date='" + date + '\'' +
                ", week='" + week + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List<CalendarDataStock> getStock_list() {
        return stock_list;
    }

    public void setStock_list(List<CalendarDataStock> stock_list) {
        this.stock_list = stock_list;
    }
}
