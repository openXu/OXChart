package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : TopDetailData
 * version : 1.0
 * description : 券商热点与走势对比
 */
public class TopDetailData {

    private String concept_id;
    private String concept_name;
    private String title;
    private String content;
    private String opentime;
    private List<DetailStock> stock_list;
    private List<List<Object>> trend_line;
    private List<TopDetailNew> news;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public List<DetailStock> getStock_list() {
        return stock_list;
    }

    public void setStock_list(List<DetailStock> stock_list) {
        this.stock_list = stock_list;
    }

    public List<List<Object>> getTrend_line() {
        return trend_line;
    }

    public void setTrend_line(List<List<Object>> trend_line) {
        this.trend_line = trend_line;
    }

    public List<TopDetailNew> getNews() {
        return news;
    }

    public void setNews(List<TopDetailNew> news) {
        this.news = news;
    }
}
