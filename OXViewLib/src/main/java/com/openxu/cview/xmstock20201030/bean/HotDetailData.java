package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 概念走势
 */
public class HotDetailData {

    private String concept_id;   //529683333575
    private String concept_name;
    private String intro;
    private String pub_time;
    private List<HotDetailNew> news;
    //实时行情走势图，每个元素字段分别是：时间、现价、均价 、跌涨浮["0930",1639.83,1625.58,"0.10"]
    private List<List<Object>> real_trend_line;
    private HotDetailHotInfo hot_info;   //实时行情数据
    //概念走势图， 每个元素表示：时间、概念、热度 [20200803,"1582.08","30.00"],
    private List<List<Object>> trend_line;
    private List<DetailStock> stock_list;

    public String getConcept_id() {
        return concept_id;
    }

    public void setConcept_id(String concept_id) {
        this.concept_id = concept_id;
    }

    public String getConcept_name() {
        return concept_name;
    }

    public void setConcept_name(String concept_name) {
        this.concept_name = concept_name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPub_time() {
        return pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public List<HotDetailNew> getNews() {
        return news;
    }

    public void setNews(List<HotDetailNew> news) {
        this.news = news;
    }

    public List<List<Object>> getReal_trend_line() {
        return real_trend_line;
    }

    public void setReal_trend_line(List<List<Object>> real_trend_line) {
        this.real_trend_line = real_trend_line;
    }

    public HotDetailHotInfo getHot_info() {
        return hot_info;
    }

    public void setHot_info(HotDetailHotInfo hot_info) {
        this.hot_info = hot_info;
    }

    public List<List<Object>> getTrend_line() {
        return trend_line;
    }

    public void setTrend_line(List<List<Object>> trend_line) {
        this.trend_line = trend_line;
    }

    public List<DetailStock> getStock_list() {
        return stock_list;
    }

    public void setStock_list(List<DetailStock> stock_list) {
        this.stock_list = stock_list;
    }
}
