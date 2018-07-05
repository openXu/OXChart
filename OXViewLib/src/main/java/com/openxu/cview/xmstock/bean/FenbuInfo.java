package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : FenbuInfo
 * version : 1.0
 * description : 涨跌分布
 */
public class FenbuInfo {

    private List<List<String>> fenbu_line;
    private FenbuNum fenbu_num;
    private String fenbu_tag;

    public List<List<String>> getFenbu_line() {
        return fenbu_line;
    }

    public void setFenbu_line(List<List<String>> fenbu_line) {
        this.fenbu_line = fenbu_line;
    }

    public FenbuNum getFenbu_num() {
        return fenbu_num;
    }

    public void setFenbu_num(FenbuNum fenbu_num) {
        this.fenbu_num = fenbu_num;
    }

    public String getFenbu_tag() {
        return fenbu_tag;
    }

    public void setFenbu_tag(String fenbu_tag) {
        this.fenbu_tag = fenbu_tag;
    }
}
