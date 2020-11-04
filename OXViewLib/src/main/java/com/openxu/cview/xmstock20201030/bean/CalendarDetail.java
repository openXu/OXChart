package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 日历数据
 */
public class CalendarDetail {

    private String id;
    private String date;

    private String week;
    private String title;
    private String importance;
    private List<String> relative_sectors;

    private String start_date;
    private String end_date;
    private List<CalendarDataStock> stock_list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public List<String> getRelative_sectors() {
        return relative_sectors;
    }

    public void setRelative_sectors(List<String> relative_sectors) {
        this.relative_sectors = relative_sectors;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public List<CalendarDataStock> getStock_list() {
        return stock_list;
    }

    public void setStock_list(List<CalendarDataStock> stock_list) {
        this.stock_list = stock_list;
    }
}
