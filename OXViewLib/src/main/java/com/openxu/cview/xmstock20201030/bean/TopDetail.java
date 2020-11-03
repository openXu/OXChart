package com.openxu.cview.xmstock20201030.bean;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : TopDetail
 * version : 1.0
 * description : 券商热点与走势对比
 */
public class TopDetail {

    private int code;
    private String err_info;

    private TopDetailData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TopDetailData getData() {
        return data;
    }

    public void setData(TopDetailData data) {
        this.data = data;
    }

    public String getErr_info() {
        return err_info;
    }

    public void setErr_info(String err_info) {
        this.err_info = err_info;
    }
}
