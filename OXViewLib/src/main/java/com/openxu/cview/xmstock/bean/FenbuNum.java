package com.openxu.cview.xmstock.bean;

/**
 * autour : xiami
 * date : 2018/3/13 14:59
 * className : FenbuInfo
 * version : 1.0
 * description : 涨跌分布
 */
public class FenbuNum {

    private String time;
    private String up;
    private String up_stop;
    private String down;
    private String down_stop;
    private String equal;
    private String stop;
    private String up_no_one;
    private String up_one;
        /*
        "time": "14:51",
				"up": 962,
				"up_stop": 23,
				"down": 2268,
				"down_stop": 2,
				"equal": 361,
				"stop": 137,
				"up_no_one": 0,
				"up_one": 3
         */

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getUp_stop() {
        return up_stop;
    }

    public void setUp_stop(String up_stop) {
        this.up_stop = up_stop;
    }

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getDown_stop() {
        return down_stop;
    }

    public void setDown_stop(String down_stop) {
        this.down_stop = down_stop;
    }

    public String getEqual() {
        return equal;
    }

    public void setEqual(String equal) {
        this.equal = equal;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getUp_no_one() {
        return up_no_one;
    }

    public void setUp_no_one(String up_no_one) {
        this.up_no_one = up_no_one;
    }

    public String getUp_one() {
        return up_one;
    }

    public void setUp_one(String up_one) {
        this.up_one = up_one;
    }
}
