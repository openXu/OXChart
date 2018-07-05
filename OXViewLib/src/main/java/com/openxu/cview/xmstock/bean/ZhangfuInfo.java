package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : ZhangfuInfo
 * version : 1.0
 * description : 涨幅
 */
public class ZhangfuInfo {

    private List<List<String>> zhangfu_line;
    private String zhangfu_tag;

    public List<List<String>> getZhangfu_line() {
        return zhangfu_line;
    }

    public void setZhangfu_line(List<List<String>> zhangfu_line) {
        this.zhangfu_line = zhangfu_line;
    }

    public String getZhangfu_tag() {
        return zhangfu_tag;
    }

    public void setZhangfu_tag(String zhangfu_tag) {
        this.zhangfu_tag = zhangfu_tag;
    }
}
