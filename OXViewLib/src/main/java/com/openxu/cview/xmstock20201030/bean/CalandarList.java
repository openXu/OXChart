package com.openxu.cview.xmstock20201030.bean;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : CalandarList
 * version : 1.0
 * description : 日历数据
 */
public class CalandarList {

    private int code;
    private String err_info;

    private CalendarListData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CalendarListData getData() {
        return data;
    }

    public void setData(CalendarListData data) {
        this.data = data;
    }

    public String getErr_info() {
        return err_info;
    }

    public void setErr_info(String err_info) {
        this.err_info = err_info;
    }
}
