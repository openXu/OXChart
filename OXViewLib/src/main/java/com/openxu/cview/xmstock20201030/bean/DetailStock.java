package com.openxu.cview.xmstock20201030.bean;

import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : TopDetailStock
 * version : 1.0
 * description : 券商热点与走势对比
 */
public class DetailStock {

    private String name;
    private String code;
    private String symbol;
    private String remark;
    private String trade;
    private String updownrate;
    private String circ_mv;

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getUpdownrate() {
        return updownrate;
    }

    public void setUpdownrate(String updownrate) {
        this.updownrate = updownrate;
    }

    public String getCirc_mv() {
        return circ_mv;
    }

    public void setCirc_mv(String circ_mv) {
        this.circ_mv = circ_mv;
    }
}
