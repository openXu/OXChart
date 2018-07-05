package com.openxu.cview.xmstock.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2018/3/14 10:54
 * className : ChartData
 * version : 1.0
 * description : 股票数据解析封装
 */
public class ChartData {

    private FenbuInfo fenbu;            //分布

    private TrendInfo trend;           //涨跌趋势

    private CompareInfo compare;

    private ZhabanInfo zhaban;
    private List<ZhabanStock> zhaban_stock_list;

    private ZhangfuInfo zhangfu;          //涨幅

    private WeightInfo weight;          //权重

    private List<ZhangtingStock> zhangting_stock_list;

    private List<ZhangtingStock> yesterday_zhangting_stock;

    public FenbuInfo getFenbu() {
        return fenbu;
    }

    public void setFenbu(FenbuInfo fenbu) {
        this.fenbu = fenbu;
    }

    public TrendInfo getTrend() {
        return trend;
    }

    public void setTrend(TrendInfo trend) {
        this.trend = trend;
    }

    public CompareInfo getCompare() {
        return compare;
    }

    public void setCompare(CompareInfo compare) {
        this.compare = compare;
    }

    public ZhabanInfo getZhaban() {
        return zhaban;
    }

    public void setZhaban(ZhabanInfo zhaban) {
        this.zhaban = zhaban;
    }

    public List<ZhabanStock> getZhaban_stock_list() {
        return zhaban_stock_list;
    }

    public void setZhaban_stock_list(List<ZhabanStock> zhaban_stock_list) {
        this.zhaban_stock_list = zhaban_stock_list;
    }

    public ZhangfuInfo getZhangfu() {
        return zhangfu;
    }

    public void setZhangfu(ZhangfuInfo zhangfu) {
        this.zhangfu = zhangfu;
    }

    public WeightInfo getWeight() {
        return weight;
    }

    public void setWeight(WeightInfo weight) {
        this.weight = weight;
    }

    public List<ZhangtingStock> getZhangting_stock_list() {
        return zhangting_stock_list;
    }

    public void setZhangting_stock_list(List<ZhangtingStock> zhangting_stock_list) {
        this.zhangting_stock_list = zhangting_stock_list;
    }

    public List<ZhangtingStock> getYesterday_zhangting_stock() {
        return yesterday_zhangting_stock;
    }

    public void setYesterday_zhangting_stock(List<ZhangtingStock> yesterday_zhangting_stock) {
        this.yesterday_zhangting_stock = yesterday_zhangting_stock;
    }
}
