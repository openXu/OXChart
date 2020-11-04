package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 日历数据
 */
public class CalendarListData {

    private List<CalendarData> calendar;
    private List<CalendarDetail> calendar_detail;

    public List<CalendarData> getCalendar() {
        return calendar;
    }

    public void setCalendar(List<CalendarData> calendar) {
        this.calendar = calendar;
    }

    public List<CalendarDetail> getCalendar_detail() {
        return calendar_detail;
    }

    public void setCalendar_detail(List<CalendarDetail> calendar_detail) {
        this.calendar_detail = calendar_detail;
    }
}
