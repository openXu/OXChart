package com.openxu.cview.xmstock20201030.bean;

/**
 * Author: openXu
 * Time: 2020/11/4 12:04
 * class: DayFinish
 * Description:
 */
public class DayFinish {
    public int day;
    public int all;
    public int finish;
    public DayFinish(int day, int finish, int all) {
        this.day = day;
        this.all = all;
        this.finish = finish;
    }
}
