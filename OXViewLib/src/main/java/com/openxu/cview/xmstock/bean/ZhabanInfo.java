package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : ZhabanInfo
 * version : 1.0
 * description : 炸板
 */
public class ZhabanInfo {

    private List<List<String>> zhaban_line;
    private String zhaban_tag;

    public List<List<String>> getZhaban_line() {
        return zhaban_line;
    }

    public void setZhaban_line(List<List<String>> zhaban_line) {
        this.zhaban_line = zhaban_line;
    }

    public String getZhaban_tag() {
        return zhaban_tag;
    }

    public void setZhaban_tag(String zhaban_tag) {
        this.zhaban_tag = zhaban_tag;
    }
}
