package com.openxu.cview.xmstock20201030.bean;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetail
 * version : 1.0
 * description : 概念走势
 */
public class HotDetail {

    private int code;
    private String err_info;

    private HotDetailData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public HotDetailData getData() {
        return data;
    }

    public void setData(HotDetailData data) {
        this.data = data;
    }

    public String getErr_info() {
        return err_info;
    }

    public void setErr_info(String err_info) {
        this.err_info = err_info;
    }
}
