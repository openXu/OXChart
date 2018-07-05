package com.openxu.cview.xmstock.bean;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : ZhangtingStock
 * version : 1.0
 * description : 涨停股票信息
 */
public class ZhangtingStock {
//"symbol": "sh603083",
//        "name": "剑桥科技",
//        "code": "603083",
//        "trade": "43.320",
//        "updownrate": "-8.95%",
//        "hsl": "6.05%"
    private String symbol;
    private String name;
    private String code;
    private String trade;
    private String hsl;
    private String updownrate;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getHsl() {
        return hsl;
    }

    public void setHsl(String hsl) {
        this.hsl = hsl;
    }

    public String getUpdownrate() {
        return updownrate;
    }

    public void setUpdownrate(String updownrate) {
        this.updownrate = updownrate;
    }
}
