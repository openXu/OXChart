package com.openxu.cview.xmstock20201030.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * autour : xiami
 * date : 2020/11/13 14:59
 * className : HotDetailData
 * version : 1.0
 * description : 日历数据
 */
public class CalendarDataStock implements Parcelable {

    private String name;
    private String code;
    private String symbol;
    private int flag;
    private String chg;
    private String count;
    private String max_real_chg;
    private List<List<String>> trend_line;

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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getChg() {
        return chg;
    }

    public void setChg(String chg) {
        this.chg = chg;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMax_real_chg() {
        return max_real_chg;
    }

    public void setMax_real_chg(String max_real_chg) {
        this.max_real_chg = max_real_chg;
    }

    public List<List<String>> getTrend_line() {
        return trend_line;
    }

    public void setTrend_line(List<List<String>> trend_line) {
        this.trend_line = trend_line;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeString(this.symbol);
        dest.writeInt(this.flag);
        dest.writeString(this.chg);
        dest.writeString(this.count);
        dest.writeString(this.max_real_chg);
        dest.writeList(this.trend_line);
    }

    public CalendarDataStock() {
    }

    protected CalendarDataStock(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
        this.symbol = in.readString();
        this.flag = in.readInt();
        this.chg = in.readString();
        this.count = in.readString();
        this.max_real_chg = in.readString();
        this.trend_line = new ArrayList<List<String>>();
        in.readList(this.trend_line, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<CalendarDataStock> CREATOR = new Parcelable.Creator<CalendarDataStock>() {
        @Override
        public CalendarDataStock createFromParcel(Parcel source) {
            return new CalendarDataStock(source);
        }

        @Override
        public CalendarDataStock[] newArray(int size) {
            return new CalendarDataStock[size];
        }
    };
}
