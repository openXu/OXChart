package com.openxu.cview.xmstock.bean;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : CompareInfo
 * version : 1.0
 * description : 涨跌对比
 */
public class CompareInfo {

    private CompareLine compare_line;
    private String compare_tag;

    public CompareLine getCompare_line() {
        return compare_line;
    }

    public void setCompare_line(CompareLine compare_line) {
        this.compare_line = compare_line;
    }

    public String getCompare_tag() {
        return compare_tag;
    }

    public void setCompare_tag(String compare_tag) {
        this.compare_tag = compare_tag;
    }
}
