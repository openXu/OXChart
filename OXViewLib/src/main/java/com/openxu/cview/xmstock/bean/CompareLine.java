package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : CompareLine
 * version : 1.0
 * description : 涨跌对比
 */
public class CompareLine {

    private List<List<String>> stock_updown_all;   //全部
    private List<List<String>> stock_updown_sz;    //深市
    private List<List<String>> stock_updown_sh;    //深沪
    private List<List<String>> stock_updown_small; //中小
    private List<List<String>> stock_updown_gem;   //创业

    public List<List<String>> getStock_updown_all() {
        return stock_updown_all;
    }

    public void setStock_updown_all(List<List<String>> stock_updown_all) {
        this.stock_updown_all = stock_updown_all;
    }

    public List<List<String>> getStock_updown_sz() {
        return stock_updown_sz;
    }

    public void setStock_updown_sz(List<List<String>> stock_updown_sz) {
        this.stock_updown_sz = stock_updown_sz;
    }

    public List<List<String>> getStock_updown_sh() {
        return stock_updown_sh;
    }

    public void setStock_updown_sh(List<List<String>> stock_updown_sh) {
        this.stock_updown_sh = stock_updown_sh;
    }

    public List<List<String>> getStock_updown_small() {
        return stock_updown_small;
    }

    public void setStock_updown_small(List<List<String>> stock_updown_small) {
        this.stock_updown_small = stock_updown_small;
    }

    public List<List<String>> getStock_updown_gem() {
        return stock_updown_gem;
    }

    public void setStock_updown_gem(List<List<String>> stock_updown_gem) {
        this.stock_updown_gem = stock_updown_gem;
    }
}
